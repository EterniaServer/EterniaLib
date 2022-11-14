package br.com.eterniaserver.eternialib.core.handlers;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.entities.PlayerUUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class PlayerHandler implements Listener {

    private final EterniaLib plugin;

    public PlayerHandler(EterniaLib plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        final String playerName = event.getName();
        final UUID uuid = event.getUniqueId();

        if (EterniaLib.getUUIDOf(playerName) != null) {
            return;
        }

        EterniaLib.registerNewUUID(playerName, uuid);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerUUID playerUUID = EterniaLib.getDatabase().getEntity(PlayerUUID.class, uuid);
            boolean shouldInsert = playerUUID == null;

            playerUUID = shouldInsert ? new PlayerUUID() : playerUUID;
            playerUUID.uuid = uuid;
            playerUUID.playerName = playerName;

            if (shouldInsert) {
                EterniaLib.getDatabase().insert(PlayerUUID.class, playerUUID);
            }
            else {
                EterniaLib.getDatabase().update(PlayerUUID.class, playerUUID);
            }
        });
    }

}
