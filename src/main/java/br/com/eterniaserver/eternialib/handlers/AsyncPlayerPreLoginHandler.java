package br.com.eterniaserver.eternialib.handlers;

import br.com.eterniaserver.eternialib.Constants;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.SQL;
import br.com.eterniaserver.eternialib.UUIDFetcher;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.core.queries.Insert;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class AsyncPlayerPreLoginHandler implements Listener {

    private final EterniaLib plugin;

    public AsyncPlayerPreLoginHandler(final EterniaLib plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {

        if (UUIDFetcher.getUUIDOf(event.getName()) != null) {
            return;
        }

        final Insert insert = new Insert(plugin.getString(Strings.SQL_TABLE));
        final String playerName = event.getName();
        final UUID uuid = event.getUniqueId();

        plugin.registerNewUUID(playerName, uuid);
        insert.columns.set(Constants.UUID_STR, Constants.PLAYER_NAME_STR);
        insert.values.set(uuid.toString(), playerName);
        SQL.execute(insert);

    }

}
