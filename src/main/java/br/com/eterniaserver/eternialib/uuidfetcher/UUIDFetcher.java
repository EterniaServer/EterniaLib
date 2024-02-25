package br.com.eterniaserver.eternialib.uuidfetcher;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface UUIDFetcher {

    void fetchUUID(String name, Consumer<Optional<UUIDFetcherResponse>> consumer);

    void fetchName(UUID uuid, Consumer<Optional<UUIDFetcherResponse>> consumer);

    Optional<UUID> getCachedUUID(String name);

    Optional<String> getCachedName(UUID uuid);

    void cacheUUID(String name, UUID uuid);

}
