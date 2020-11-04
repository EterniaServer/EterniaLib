package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.sql.queries.Insert;
import br.com.eterniaserver.eternialib.sql.queries.Update;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class AsyncPlayerPreLogin implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String playerName = event.getName();
        UUID uuid = UUIDFetcher.getUUIDOf(playerName);

        UUIDFetcher.lookupCache.put(playerName, uuid);
        UUIDFetcher.lookupNameCache.put(uuid, playerName);

        if (!UUIDFetcher.firstLookupCache.containsKey(uuid)) {
            UUIDFetcher.firstLookupCache.put(uuid, playerName);
            Insert insert = new Insert("el_cache");
            insert.columns.set("uuid", "player_name");
            insert.values.set(uuid.toString(), playerName);
            SQL.execute(insert);
            return;
        }
        if (!UUIDFetcher.firstLookupCache.get(uuid).equals(playerName)) {
            UUIDFetcher.firstLookupCache.put(uuid, playerName);
            Update update = new Update("el_cache");
            update.set.set("player_name", playerName);
            update.where.set("uuid", uuid.toString());
            SQL.execute(update);
        }

    }

}
