package br.com.eterniaserver.eternialib;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.HashMap;
import java.util.UUID;

public class AsyncPlayerPreLogin implements Listener {

    public AsyncPlayerPreLogin() {
        EQueries.executeQuery("CREATE TABLE IF NOT EXISTS el_cache (uuid varchar(36), player_name varchar(16));", false);

        final HashMap<String, String> temp = EQueries.getMapString("SELECT * FROM el_cache;", "uuid", "player_name");
        temp.forEach((k, v) -> {
            UUID uuid = UUID.fromString(k);
            UUIDFetcher.lookupCache.put(v, uuid);
            UUIDFetcher.lookupNameCache.put(uuid, v);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        final String playerName = event.getName();
        final UUID uuid = UUIDFetcher.getUUIDOf(playerName);
        if (!UUIDFetcher.lookupNameCache.containsKey(uuid)) {
            EQueries.executeQuery("INSERT INTO el_cache (uuid, player_name) ('" + uuid.toString() + "', '" + playerName + "');", false);
        } else {
            if (!UUIDFetcher.lookupNameCache.get(uuid).equals(playerName)) {
                EQueries.executeQuery("UPDATE el_cache SET='player_name' WHERE uuid='" + uuid.toString() + "';", false);
            }
        }
        UUIDFetcher.lookupCache.put(playerName, uuid);
        UUIDFetcher.lookupNameCache.put(uuid, playerName);
    }

}
