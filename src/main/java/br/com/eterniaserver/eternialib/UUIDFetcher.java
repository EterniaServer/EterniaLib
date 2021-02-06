package br.com.eterniaserver.eternialib;

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
import java.util.UUID;

public class UUIDFetcher {

    private UUIDFetcher() {
        throw new IllegalStateException("Utility class");
    }

    protected static final Map<String, UUID> lookupCache = new HashMap<>();
    protected static final Map<UUID, String> lookupNameCache = new HashMap<>();

    public static UUID getUUIDOf(String name) {
        UUID result = lookupCache.get(name);

        if (result != null) {
            return result;
        }

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
            } catch (IOException ignore) {
                EterniaLib.report("$8[$aE$9L$8] $7Erro ao se conectar a $3api.mojang.com$8.".replace('$', (char) 0x00A7));
            }
        }

        lookupCache.put(name, result);
        lookupNameCache.put(result, name);
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