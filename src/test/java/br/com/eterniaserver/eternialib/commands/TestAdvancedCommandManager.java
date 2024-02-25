package br.com.eterniaserver.eternialib.commands;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.chat.ChatCommons;
import br.com.eterniaserver.eternialib.commands.enums.AdvancedCategory;
import br.com.eterniaserver.eternialib.commands.enums.AdvancedRules;
import br.com.eterniaserver.eternialib.commands.impl.AdvancedCommandManagerImpl;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

@SuppressWarnings("ResultOfMethodCallIgnored")
class TestAdvancedCommandManager {

    private static final int TICK_DELAY = 20;

    private EterniaLib mockServer() {
        EterniaLib eterniaLib = Mockito.mock(EterniaLib.class);
        Server server = Mockito.mock(Server.class);
        BukkitScheduler bukkitScheduler = Mockito.mock(BukkitScheduler.class);
        
        Mockito.when(eterniaLib.getServer()).thenReturn(server);
        Mockito.when(server.getScheduler()).thenReturn(bukkitScheduler);
        
        return eterniaLib;
    }
    
    @Test
    void testCheckHasBreakingRuleNoCommandNoRule() {
        EterniaLib eterniaLib = mockServer();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            advancedCommandManager.checkHasBreakingRule(UUID.randomUUID(), AdvancedRules.NOT_ATTACK);

            Mockito.verify(chatCommons, Mockito.times(0)).parseMessage(Messages.ATTACKED);
        }
    }

    @Test
    void testCheckHasBreakingRuleWithCommandNoRule() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand advancedCommand = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(advancedCommand.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(advancedCommand.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        Mockito.when(advancedCommand.hasRule(AdvancedRules.NOT_BREAK_BLOCK)).thenReturn(false);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            advancedCommandManager.addTimedCommand(advancedCommand);
            advancedCommandManager.checkHasBreakingRule(uuid, AdvancedRules.NOT_BREAK_BLOCK);

            Mockito.verify(chatCommons, Mockito.times(0)).parseMessage(Messages.BLOCK_BRAKED);
        }
    }

    @Test
    void testCheckHasBreakingRuleWithCommandWithRule() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand advancedCommand = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(advancedCommand.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(advancedCommand.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        Mockito.when(advancedCommand.hasRule(AdvancedRules.NOT_JUMP)).thenReturn(true);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            advancedCommandManager.addTimedCommand(advancedCommand);
            advancedCommandManager.checkHasBreakingRule(uuid, AdvancedRules.NOT_JUMP);

            Mockito.verify(chatCommons, Mockito.times(1)).parseMessage(Messages.JUMPED);
        }
    }

    @Test
    void testCheckHasBreakingRules() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand advancedCommand = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(advancedCommand.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(advancedCommand.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        Mockito.when(advancedCommand.hasRule(AdvancedRules.NOT_ATTACK)).thenReturn(true);
        Mockito.when(advancedCommand.hasRule(AdvancedRules.NOT_BREAK_BLOCK)).thenReturn(true);
        Mockito.when(advancedCommand.hasRule(AdvancedRules.NOT_JUMP)).thenReturn(true);
        Mockito.when(advancedCommand.hasRule(AdvancedRules.NOT_SNEAK)).thenReturn(true);
        Mockito.when(advancedCommand.hasRule(AdvancedRules.NOT_MOVE)).thenReturn(true);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            advancedCommandManager.addTimedCommand(advancedCommand);
            advancedCommandManager.checkHasBreakingRule(uuid, AdvancedRules.NOT_ATTACK);
            advancedCommandManager.addTimedCommand(advancedCommand);
            advancedCommandManager.checkHasBreakingRule(uuid, AdvancedRules.NOT_BREAK_BLOCK);
            advancedCommandManager.addTimedCommand(advancedCommand);
            advancedCommandManager.checkHasBreakingRule(uuid, AdvancedRules.NOT_JUMP);
            advancedCommandManager.addTimedCommand(advancedCommand);
            advancedCommandManager.checkHasBreakingRule(uuid, AdvancedRules.NOT_SNEAK);
            advancedCommandManager.addTimedCommand(advancedCommand);
            advancedCommandManager.checkHasBreakingRule(uuid, AdvancedRules.NOT_MOVE);

            Mockito.verify(chatCommons, Mockito.times(1)).parseMessage(Messages.ATTACKED);
            Mockito.verify(chatCommons, Mockito.times(1)).parseMessage(Messages.BLOCK_BRAKED);
            Mockito.verify(chatCommons, Mockito.times(1)).parseMessage(Messages.JUMPED);
            Mockito.verify(chatCommons, Mockito.times(1)).parseMessage(Messages.SNEAKED);
            Mockito.verify(chatCommons, Mockito.times(1)).parseMessage(Messages.MOVED);
        }
    }

    @Test
    void testGetAndRemoveCommandWithCommand() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand expect = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(expect.getCategory()).thenReturn(AdvancedCategory.CONFIRMATION);
        Mockito.when(expect.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            advancedCommandManager.addConfirmationCommand(expect);

            AdvancedCommand result = advancedCommandManager.getAndRemoveCommand(uuid);

            Assertions.assertEquals(expect, result);
        }
    }

    @Test
    void testGetAndRemoveCommandNoCommand() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand command = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(command.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(command.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        advancedCommandManager.addTimedCommand(command);

        AdvancedCommand result = advancedCommandManager.getAndRemoveCommand(uuid);

        Assertions.assertNull(result);
    }

    @Test
    void testRunTimedCommandAndAbort() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand advancedCommand = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(advancedCommand.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(advancedCommand.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        Mockito.when(advancedCommand.increaseCommandTicks(TICK_DELAY)).thenReturn(false);
        Mockito.when(advancedCommand.isAborted()).thenReturn(true);

        advancedCommandManager.addTimedCommand(advancedCommand);
        advancedCommandManager.run();

        Mockito.verify(advancedCommand, Mockito.times(1)).isAborted();
    }

    @Test
    void testRunTimedCommandNotFinished() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand advancedCommand = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(advancedCommand.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(advancedCommand.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        Mockito.when(advancedCommand.increaseCommandTicks(TICK_DELAY)).thenReturn(false);
        Mockito.when(advancedCommand.isAborted()).thenReturn(false);

        advancedCommandManager.addTimedCommand(advancedCommand);
        advancedCommandManager.run();

        Mockito.verify(advancedCommand, Mockito.times(1)).isAborted();
    }

    @Test
    void testRunTimedCommandFinished() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        BukkitTask bukkitTask = Mockito.mock(BukkitTask.class);
        AdvancedCommand advancedCommand = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(advancedCommand.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(advancedCommand.sender()).thenReturn(player);
        Mockito.when(advancedCommand.executeAsynchronously()).thenReturn(bukkitTask);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        Mockito.when(advancedCommand.increaseCommandTicks(TICK_DELAY)).thenReturn(true);
        Mockito.when(advancedCommand.isAborted()).thenReturn(false);

        advancedCommandManager.addTimedCommand(advancedCommand);
        advancedCommandManager.run();

        Mockito.verify(advancedCommand, Mockito.times(1)).execute();
    }

    @Test
    void testRunConfirmationCommandNotFinished() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        BukkitTask bukkitTask = Mockito.mock(BukkitTask.class);
        AdvancedCommand advancedCommand = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(advancedCommand.getCategory()).thenReturn(AdvancedCategory.CONFIRMATION);
        Mockito.when(advancedCommand.sender()).thenReturn(player);
        Mockito.when(advancedCommand.executeAsynchronously()).thenReturn(bukkitTask);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        Mockito.when(advancedCommand.increaseCommandTicks(TICK_DELAY)).thenReturn(false);
        Mockito.when(advancedCommand.isAborted()).thenReturn(false);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            advancedCommandManager.addConfirmationCommand(advancedCommand);
            advancedCommandManager.run();

            Mockito.verify(advancedCommand, Mockito.times(0)).execute();
        }
    }

    @Test
    void testRunConfirmationCommandFinished() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        BukkitTask bukkitTask = Mockito.mock(BukkitTask.class);
        AdvancedCommand advancedCommand = Mockito.mock(AdvancedCommand.class);
        Component message = Mockito.mock(Component.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(advancedCommand.getCategory()).thenReturn(AdvancedCategory.CONFIRMATION);
        Mockito.when(advancedCommand.sender()).thenReturn(player);
        Mockito.when(advancedCommand.executeAsynchronously()).thenReturn(bukkitTask);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        Mockito.when(advancedCommand.increaseCommandTicks(TICK_DELAY)).thenReturn(true);
        Mockito.when(advancedCommand.isAborted()).thenReturn(false);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);

            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            Mockito.when(chatCommons.parseMessage(Messages.COMMAND_CANCELLED)).thenReturn(message);

            advancedCommandManager.addConfirmationCommand(advancedCommand);
            advancedCommandManager.run();

            Mockito.verify(advancedCommand, Mockito.times(1)).abort(message);
        }
    }

    @Test
    void testRemoveCommandsFromPlayerWithConfirmationNoTimed() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand command = Mockito.mock(AdvancedCommand.class);
        Component message = Mockito.mock(Component.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(command.getCategory()).thenReturn(AdvancedCategory.CONFIRMATION);
        Mockito.when(command.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);

            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            Mockito.when(chatCommons.parseMessage(Messages.COMMAND_CANCELLED)).thenReturn(message);

            advancedCommandManager.addConfirmationCommand(command);
            advancedCommandManager.removeCommandsFromPlayer(uuid);

            Mockito.verify(command, Mockito.times(1)).abort(message);
        }
    }

    @Test
    void testRemoveCommandsFromPlayerNoConfirmationWithTimed() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand command = Mockito.mock(AdvancedCommand.class);
        Component message = Mockito.mock(Component.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(command.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(command.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);

            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            Mockito.when(chatCommons.parseMessage(Messages.COMMAND_CANCELLED)).thenReturn(message);

            advancedCommandManager.addTimedCommand(command);
            advancedCommandManager.removeCommandsFromPlayer(uuid);

            Mockito.verify(command, Mockito.times(1)).abort(message);
        }
    }

    @Test
    void testRemoveCommandsFromPlayerWithConfirmationWithTimed() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand commandTimed = Mockito.mock(AdvancedCommand.class);
        AdvancedCommand commandConfirmation = Mockito.mock(AdvancedCommand.class);
        Component message = Mockito.mock(Component.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(commandTimed.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(commandTimed.sender()).thenReturn(player);
        Mockito.when(commandConfirmation.getCategory()).thenReturn(AdvancedCategory.CONFIRMATION);
        Mockito.when(commandConfirmation.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);

            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            Mockito.when(chatCommons.parseMessage(Messages.COMMAND_CANCELLED)).thenReturn(message);

            advancedCommandManager.addTimedCommand(commandTimed);
            advancedCommandManager.addConfirmationCommand(commandConfirmation);
            advancedCommandManager.removeCommandsFromPlayer(uuid);

            Mockito.verify(commandTimed, Mockito.times(1)).abort(message);
            Mockito.verify(commandConfirmation, Mockito.times(1)).abort(message);
        }
    }

    @Test
    void testAddConfirmationCommand() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand command = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(command.getCategory()).thenReturn(AdvancedCategory.CONFIRMATION);
        Mockito.when(command.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            boolean result = advancedCommandManager.addConfirmationCommand(command);

            Assertions.assertTrue(result);
        }
    }

    @Test
    void testAddConfirmationCommandDuplicated() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand command = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(command.getCategory()).thenReturn(AdvancedCategory.CONFIRMATION);
        Mockito.when(command.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            advancedCommandManager.addConfirmationCommand(command);
            boolean result = advancedCommandManager.addConfirmationCommand(command);

            Assertions.assertFalse(result);
        }
    }

    @Test
    void testAddTimedCommand() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand command = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(command.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(command.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        boolean result = advancedCommandManager.addTimedCommand(command);

        Assertions.assertTrue(result);
    }

    @Test
    void testAddTimedCommandDuplicated() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand command = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(command.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(command.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        advancedCommandManager.addTimedCommand(command);
        boolean result = advancedCommandManager.addTimedCommand(command);

        Assertions.assertFalse(result);
    }

    @Test
    void testAbortTimedCommandWithCommand() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        AdvancedCommand command = Mockito.mock(AdvancedCommand.class);
        Component component = Mockito.mock(Component.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(command.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(command.sender()).thenReturn(player);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);

            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            Mockito.when(chatCommons.parseMessage(Messages.COMMAND_CANCELLED)).thenReturn(component);

            advancedCommandManager.addTimedCommand(command);
            advancedCommandManager.abortTimedCommand(uuid);

            Mockito.verify(chatCommons, Mockito.times(1)).parseMessage(Messages.COMMAND_CANCELLED);
        }
    }

    @Test
    void testAbortTimedCommandNoCommand() {
        EterniaLib eterniaLib = mockServer();

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            ChatCommons chatCommons = Mockito.mock(ChatCommons.class);

            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            advancedCommandManager.abortTimedCommand(uuid);

            Mockito.verify(chatCommons, Mockito.times(0)).parseMessage(Messages.COMMAND_CANCELLED);
        }
    }

    @Test
    void testGetAndRemoveTasks() {
        EterniaLib eterniaLib = mockServer();
        Player player = Mockito.mock(Player.class);
        BukkitTask bukkitTask = Mockito.mock(BukkitTask.class);
        AdvancedCommand advancedCommand = Mockito.mock(AdvancedCommand.class);

        UUID uuid = UUID.randomUUID();
        AdvancedCommandManagerImpl advancedCommandManager = new AdvancedCommandManagerImpl(eterniaLib, TICK_DELAY);

        Mockito.when(advancedCommand.getCategory()).thenReturn(AdvancedCategory.TIMED);
        Mockito.when(advancedCommand.sender()).thenReturn(player);
        Mockito.when(advancedCommand.executeAsynchronously()).thenReturn(bukkitTask);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        Mockito.when(advancedCommand.increaseCommandTicks(TICK_DELAY)).thenReturn(true);
        Mockito.when(advancedCommand.isAborted()).thenReturn(false);

        advancedCommandManager.addTimedCommand(advancedCommand);
        advancedCommandManager.run();

        BukkitTask[] tasks = advancedCommandManager.getAndRemoveTasks(uuid);

        Assertions.assertEquals(1, tasks.length);
        Assertions.assertEquals(bukkitTask, tasks[0]);
    }
}
