package br.com.eterniaserver.eternialib;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class UUIDFetcher {

    private UUIDFetcher() {
        throw new IllegalStateException("Utility class");
    }

    private static final Random RANDOM = new Random();

    protected static final Map<String, UUID> lookupCache = new HashMap<>();
    protected static final Map<UUID, String> lookupNameCache = new HashMap<>();

    public static UUID getUUIDOf(String name) {
        UUID result = lookupCache.get(name);
        if (result == null) {
            result = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
            if (Bukkit.getOnlineMode()) {
                try {
                    URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    if (connection.getResponseCode() != 400) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        JsonElement element = new JsonParser().parse(bufferedReader);
                        JsonObject object = element.getAsJsonObject();
                        String uuidAsString = object.get("id").getAsString();
                        result = parseUUIDFromString(uuidAsString);
                    }
                } catch (IOException ignore) {}
            }
            lookupCache.put(name, result);
            lookupNameCache.put(result, name);
        }
        return result;
    }

    public static String getNameOf(UUID uuid) {
        String result = lookupNameCache.get(uuid);

        if (result == null) {
            if (Bukkit.getOnlineMode()) {
                try {
                    URL url = new URL("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if(connection.getResponseCode() == 400) {
                        result = Long.toHexString(RANDOM.nextLong());
                    } else {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        JsonElement element = new JsonParser().parse(bufferedReader);
                        JsonArray array = element.getAsJsonArray();
                        JsonObject object = array.get(0).getAsJsonObject();
                        result = object.get("name").getAsString();
                    }
                } catch (IOException e) {
                    result = Long.toHexString(RANDOM.nextLong());
                }
            } else {
                result = Bukkit.getOfflinePlayer(uuid).getName();
            }
            lookupCache.put(result, uuid);
            lookupNameCache.put(uuid, result);
        }
        return result;
    }

    private static UUID parseUUIDFromString(String uuidAsString) {
        String[] parts = {
                "0x" + uuidAsString.substring(0, 8),
                "0x" + uuidAsString.substring(8, 12),
                "0x" + uuidAsString.substring(12, 16),
                "0x" + uuidAsString.substring(16, 20),
                "0x" + uuidAsString.substring(20, 32)
        };

        long mostSigBits = Long.decode(parts[0]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[1]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[2]);

        long leastSigBits = Long.decode(parts[3]);
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(parts[4]);

        return new UUID(mostSigBits, leastSigBits);
    }

}