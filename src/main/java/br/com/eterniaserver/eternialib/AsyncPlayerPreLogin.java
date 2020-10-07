package br.com.eterniaserver.eternialib;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class AsyncPlayerPreLogin implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        final String playerName = event.getName();
        final UUID uuid = UUIDFetcher.getUUIDOf(playerName);

        UUIDFetcher.lookupCache.put(playerName, uuid);
        UUIDFetcher.lookupNameCache.put(uuid, playerName);

        if (!UUIDFetcher.firstLookupCache.containsKey(uuid)) {
            UUIDFetcher.firstLookupCache.put(uuid, playerName);
            EQueries.executeQuery("INSERT INTO el_cache (uuid, player_name) VALUES ('" + uuid.toString() + "', '" + playerName + "');", false);
        } else if (!UUIDFetcher.firstLookupCache.get(uuid).equals(playerName)) {
            UUIDFetcher.firstLookupCache.put(uuid, playerName);
            EQueries.executeQuery("UPDATE el_cache SET player_name='" + playerName + "' WHERE uuid='" + uuid.toString() + "';", false);
        }

    }

}
