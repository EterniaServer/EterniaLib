package br.com.eterniaserver.eternialib.uuidfetcher.impl;

import br.com.eterniaserver.eternialib.uuidfetcher.UUIDFetcherResponse;
import br.com.eterniaserver.eternialib.uuidfetcher.UUIDFetcher;
import br.com.eterniaserver.eternialib.uuidfetcher.enums.UUIDResponseStatus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class UUIDFetcherImpl implements UUIDFetcher {

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
    private static final String NAME_URL = "https://api.mojang.com/user/profile/%s?at=%d";

    private final Pattern pattern = Pattern.compile("(.{8})(.{4})(.{4})(.{4})(.+)");

    private final ConcurrentMap<String, UUID> fetchByNameMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, String> fetchByUUIDMap = new ConcurrentHashMap<>();

    private final HttpClient httpClient;

    public UUIDFetcherImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void fetchUUID(String name, Consumer<Optional<UUIDFetcherResponse>> consumer) {
        Optional<UUID> cachedUUID = getCachedUUID(name);
        if (cachedUUID.isPresent()) {
            consumer.accept(Optional.of(new UUIDFetcherResponse(name, cachedUUID.get())));
            return;
        }

        executeHttpRequest(name, UUID_URL, consumer);
    }

    @Override
    public void fetchName(UUID uuid, Consumer<Optional<UUIDFetcherResponse>> consumer) {
        Optional<String> cachedName = getCachedName(uuid);
        if (cachedName.isPresent()) {
            consumer.accept(Optional.of(new UUIDFetcherResponse(cachedName.get(), uuid)));
            return;
        }

        executeHttpRequest(uuid, NAME_URL, consumer);
    }

    @Override
    public Optional<UUID> getCachedUUID(String name) {
        return Optional.ofNullable(fetchByNameMap.get(name));
    }

    @Override
    public Optional<String> getCachedName(UUID uuid) {
        return Optional.ofNullable(fetchByUUIDMap.get(uuid));
    }

    @Override
    public void cacheUUID(String name, UUID uuid) {
        fetchByNameMap.put(name, uuid);
        fetchByUUIDMap.put(uuid, name);
    }

    private void executeHttpRequest(Object object, String url, Consumer<Optional<UUIDFetcherResponse>> consumer) {
        try {
            URI uri = new URI(url.formatted(object, System.currentTimeMillis()));

            HttpRequest request = HttpRequest.newBuilder(uri).build();
            HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = httpClient.send(request, bodyHandler);

            if (response.statusCode() == UUIDResponseStatus.NOT_FOUND.code()) {
                consumer.accept(Optional.empty());
                return;
            }

            consumer.accept(Optional.of(getFromResponse(response)));
            return;
        } catch (InterruptedException e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage());
            Thread.currentThread().interrupt();
            return;
        } catch (URISyntaxException | IOException e) {
            Logger.getGlobal().info(e.getMessage());
        }

        consumer.accept(Optional.empty());
    }

    private UUIDFetcherResponse getFromResponse(HttpResponse<String> response) {
        JsonElement element = JsonParser.parseString(response.body());
        JsonObject object = element.getAsJsonObject();

        String plainUUID = object.get("id").getAsString();
        String formattedUUID = pattern.matcher(plainUUID).replaceAll("$1-$2-$3-$4-$5");


        String name = object.get("name").getAsString();
        UUID uuid = UUID.fromString(formattedUUID);

        return new UUIDFetcherResponse(name, uuid);
    }
}
