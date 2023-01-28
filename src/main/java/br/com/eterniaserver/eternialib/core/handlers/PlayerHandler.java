package br.com.eterniaserver.eternialib.core.handlers;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.commands.enums.AdvancedRules;
import br.com.eterniaserver.eternialib.core.runnables.SynchronizePlayerUUID;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

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

        EterniaLib.registerNewUUID(playerName, uuid);

        SynchronizePlayerUUID synchronizePlayerUUID = new SynchronizePlayerUUID(uuid, playerName);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, synchronizePlayerUUID);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        EterniaLib.getAdvancedCmdManager().removeCommandsFromPlayer(uuid);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.hasExplicitlyChangedBlock()) {
            UUID uuid = event.getPlayer().getUniqueId();
            EterniaLib.getAdvancedCmdManager().checkHasBreakingRule(uuid, AdvancedRules.NOT_MOVE);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJump(PlayerJumpEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        EterniaLib.getAdvancedCmdManager().checkHasBreakingRule(uuid, AdvancedRules.NOT_JUMP);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        EterniaLib.getAdvancedCmdManager().checkHasBreakingRule(uuid, AdvancedRules.NOT_SNEAK);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        EterniaLib.getAdvancedCmdManager().checkHasBreakingRule(uuid, AdvancedRules.NOT_BREAK_BLOCK);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAttack(PrePlayerAttackEntityEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        EterniaLib.getAdvancedCmdManager().checkHasBreakingRule(uuid, AdvancedRules.NOT_ATTACK);
    }

}
