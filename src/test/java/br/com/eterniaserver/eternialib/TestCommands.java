package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import br.com.eterniaserver.eternialib.core.commands.CommandConfirm;
import br.com.eterniaserver.eternialib.core.commands.Eternia;
import br.com.eterniaserver.eternialib.core.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.core.interfaces.CommandConfirmable;
import br.com.eterniaserver.eternialib.core.interfaces.ReloadableConfiguration;

import net.bytebuddy.utility.RandomString;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;


class TestCommands {

    private static ServerMock server;
    private static EterniaLib plugin;

    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(EterniaLib.class);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("test the 'eternia' commands")
    void eterniaCommands() {
        final var eterniaCmdManager = new Eternia(plugin);

        for (final var test : plugin.getReloadableConfigList()) {
            final var commandSender = server.getConsoleSender();
            eterniaCmdManager.onReload(commandSender, test + ":t");
        }

        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("test the 'confirm' commands")
    void commandConirm() {
        final var commandConfirm = new CommandConfirm(plugin);
        final var mockPlayer = server.addPlayer(new RandomString(16).nextString());

        final var command = new TestCommand();
        Assertions.assertEquals(ConfigurationCategory.GENERIC, command.category());

        CmdConfirmationManager.scheduleCommand(mockPlayer, command);
        commandConfirm.onAccept(mockPlayer);
        Assertions.assertEquals(ConfigurationCategory.WARNING_ADVICE, command.category());

        CmdConfirmationManager.scheduleCommand(mockPlayer, command);
        commandConfirm.onDeny(mockPlayer);

        command.executeCritical();
        Assertions.assertEquals(ConfigurationCategory.GENERIC, command.category());

        command.executeConfig();
        Assertions.assertEquals(ConfigurationCategory.WARNING_ADVICE, command.category());
    }

    private final static class TestCommand implements ReloadableConfiguration, CommandConfirmable {

        private ConfigurationCategory category = ConfigurationCategory.GENERIC;

        @Override
        public ConfigurationCategory category() {
            return category;
        }

        @Override
        public void executeConfig() {
            category = ConfigurationCategory.WARNING_ADVICE;
        }

        @Override
        public void executeCritical() {
            category = ConfigurationCategory.GENERIC;
        }

        @Override
        public void execute() {
            category = ConfigurationCategory.WARNING_ADVICE;
        }
    }
}
