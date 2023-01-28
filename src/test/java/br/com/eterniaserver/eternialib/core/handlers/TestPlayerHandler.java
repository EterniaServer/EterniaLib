package br.com.eterniaserver.eternialib.core.handlers;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.commands.AdvancedCommandManager;
import br.com.eterniaserver.eternialib.commands.enums.AdvancedRules;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

class TestPlayerHandler {

    @Test
    void testOnAsyncPlayerPreLogin() {
        AsyncPlayerPreLoginEvent event = Mockito.mock(AsyncPlayerPreLoginEvent.class);
        BukkitScheduler bukkitScheduler = Mockito.mock(BukkitScheduler.class);
        EterniaLib eterniaLib = Mockito.mock(EterniaLib.class);

        final UUID uuid = UUID.randomUUID();
        final String playerName = "brownpowder";

        Mockito.when(event.getName()).thenReturn(playerName);
        Mockito.when(event.getUniqueId()).thenReturn(uuid);

        PlayerHandler playerHandler = new PlayerHandler(eterniaLib);

        try (MockedStatic<EterniaLib> eterniaLibMockedStatic = Mockito.mockStatic(EterniaLib.class);
             MockedStatic<Bukkit> bukkitMockedStatic = Mockito.mockStatic(Bukkit.class)) {
            bukkitMockedStatic.when(Bukkit::getScheduler).thenReturn(bukkitScheduler);

            playerHandler.onAsyncPlayerPreLogin(event);

            eterniaLibMockedStatic.verify(
                    () -> EterniaLib.registerNewUUID(playerName, uuid),
                    Mockito.times(1)
            );
            bukkitMockedStatic.verify(
                    Bukkit::getScheduler,
                    Mockito.times(1)
            );
        }
    }

    @Test
    void testOnPlayerQuit() {
        AdvancedCommandManager commandManager = Mockito.mock(AdvancedCommandManager.class);
        PlayerQuitEvent event = Mockito.mock(PlayerQuitEvent.class);
        EterniaLib eterniaLib = Mockito.mock(EterniaLib.class);
        Player player = Mockito.mock(Player.class);

        final UUID uuid = UUID.randomUUID();

        Mockito.when(event.getPlayer()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        PlayerHandler playerHandler = new PlayerHandler(eterniaLib);

        try (MockedStatic<EterniaLib> eterniaLibMockedStatic = Mockito.mockStatic(EterniaLib.class)) {
            eterniaLibMockedStatic.when(EterniaLib::getAdvancedCmdManager).thenReturn(commandManager);

            playerHandler.onPlayerQuit(event);

            Mockito.verify(commandManager, Mockito.times(1)).removeCommandsFromPlayer(uuid);
        }
    }

    @Test
    void testOnPlayerMoveWithBlockChange() {
        AdvancedCommandManager commandManager = Mockito.mock(AdvancedCommandManager.class);
        PlayerMoveEvent event = Mockito.mock(PlayerMoveEvent.class);
        Player player = Mockito.mock(Player.class);

        EterniaLib eterniaLib = Mockito.mock(EterniaLib.class);

        final UUID uuid = UUID.randomUUID();

        Mockito.when(event.getPlayer()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        Mockito.when(event.hasExplicitlyChangedBlock()).thenReturn(true);

        PlayerHandler playerHandler = new PlayerHandler(eterniaLib);

        try (MockedStatic<EterniaLib> mockedStatic = Mockito.mockStatic(EterniaLib.class)) {
            mockedStatic.when(EterniaLib::getAdvancedCmdManager).thenReturn(commandManager);

            playerHandler.onPlayerMove(event);

            Mockito.verify(commandManager, Mockito.times(1)).checkHasBreakingRule(
                    uuid, AdvancedRules.NOT_MOVE
            );
        }
    }

    @Test
    void testOnPlayerMoveWithoutBlockChange() {
        AdvancedCommandManager commandManager = Mockito.mock(AdvancedCommandManager.class);
        PlayerMoveEvent event = Mockito.mock(PlayerMoveEvent.class);
        Player player = Mockito.mock(Player.class);

        EterniaLib eterniaLib = Mockito.mock(EterniaLib.class);

        final UUID uuid = UUID.randomUUID();

        Mockito.when(event.getPlayer()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        Mockito.when(event.hasExplicitlyChangedBlock()).thenReturn(false);

        PlayerHandler playerHandler = new PlayerHandler(eterniaLib);

        try (MockedStatic<EterniaLib> mockedStatic = Mockito.mockStatic(EterniaLib.class)) {
            mockedStatic.when(EterniaLib::getAdvancedCmdManager).thenReturn(commandManager);

            playerHandler.onPlayerMove(event);

            Mockito.verify(commandManager, Mockito.times(0)).checkHasBreakingRule(
                    uuid, AdvancedRules.NOT_MOVE
            );
        }
    }

    @Test
    void testOnPlayerJump() {
        AdvancedCommandManager commandManager = Mockito.mock(AdvancedCommandManager.class);
        PlayerJumpEvent event = Mockito.mock(PlayerJumpEvent.class);
        Player player = Mockito.mock(Player.class);

        EterniaLib eterniaLib = Mockito.mock(EterniaLib.class);

        final UUID uuid = UUID.randomUUID();

        Mockito.when(event.getPlayer()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        PlayerHandler playerHandler = new PlayerHandler(eterniaLib);

        try (MockedStatic<EterniaLib> mockedStatic = Mockito.mockStatic(EterniaLib.class)) {
            mockedStatic.when(EterniaLib::getAdvancedCmdManager).thenReturn(commandManager);

            playerHandler.onPlayerJump(event);

            Mockito.verify(commandManager, Mockito.times(1)).checkHasBreakingRule(
                    uuid, AdvancedRules.NOT_JUMP
            );
        }
    }

    @Test
    void testOnPlayerSneak() {
        AdvancedCommandManager commandManager = Mockito.mock(AdvancedCommandManager.class);
        PlayerToggleSneakEvent event = Mockito.mock(PlayerToggleSneakEvent.class);
        Player player = Mockito.mock(Player.class);

        EterniaLib eterniaLib = Mockito.mock(EterniaLib.class);

        final UUID uuid = UUID.randomUUID();

        Mockito.when(event.getPlayer()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        PlayerHandler playerHandler = new PlayerHandler(eterniaLib);

        try (MockedStatic<EterniaLib> mockedStatic = Mockito.mockStatic(EterniaLib.class)) {
            mockedStatic.when(EterniaLib::getAdvancedCmdManager).thenReturn(commandManager);

            playerHandler.onPlayerSneak(event);

            Mockito.verify(commandManager, Mockito.times(1)).checkHasBreakingRule(
                    uuid, AdvancedRules.NOT_SNEAK
            );
        }
    }

    @Test
    void testOnPlayerBreakBlock() {
        AdvancedCommandManager commandManager = Mockito.mock(AdvancedCommandManager.class);
        BlockBreakEvent event = Mockito.mock(BlockBreakEvent.class);
        Player player = Mockito.mock(Player.class);

        EterniaLib eterniaLib = Mockito.mock(EterniaLib.class);

        final UUID uuid = UUID.randomUUID();

        Mockito.when(event.getPlayer()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        PlayerHandler playerHandler = new PlayerHandler(eterniaLib);

        try (MockedStatic<EterniaLib> mockedStatic = Mockito.mockStatic(EterniaLib.class)) {
            mockedStatic.when(EterniaLib::getAdvancedCmdManager).thenReturn(commandManager);

            playerHandler.onPlayerBreakBlock(event);

            Mockito.verify(commandManager, Mockito.times(1)).checkHasBreakingRule(
                    uuid, AdvancedRules.NOT_BREAK_BLOCK
            );
        }
    }

    @Test
    void testOnPlayerAttack() {
        AdvancedCommandManager commandManager = Mockito.mock(AdvancedCommandManager.class);
        PrePlayerAttackEntityEvent event = Mockito.mock(PrePlayerAttackEntityEvent.class);
        Player player = Mockito.mock(Player.class);

        EterniaLib eterniaLib = Mockito.mock(EterniaLib.class);

        final UUID uuid = UUID.randomUUID();

        Mockito.when(event.getPlayer()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        PlayerHandler playerHandler = new PlayerHandler(eterniaLib);

        try (MockedStatic<EterniaLib> mockedStatic = Mockito.mockStatic(EterniaLib.class)) {
            mockedStatic.when(EterniaLib::getAdvancedCmdManager).thenReturn(commandManager);

            playerHandler.onPlayerAttack(event);

            Mockito.verify(commandManager, Mockito.times(1)).checkHasBreakingRule(
                    uuid, AdvancedRules.NOT_ATTACK
            );
        }
    }

}
