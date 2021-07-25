package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

import br.com.eterniaserver.eternialib.core.commands.CommandConfirm;
import br.com.eterniaserver.eternialib.core.commands.Eternia;
import br.com.eterniaserver.eternialib.core.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.core.interfaces.CommandConfirmable;
import br.com.eterniaserver.eternialib.core.interfaces.ReloadableConfiguration;

import co.aikar.commands.CommandHelp;

import net.bytebuddy.utility.RandomString;

import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;

import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;


class TestCommands {

    private static ServerMock server;
    private static EterniaLib plugin;

    @BeforeAll
    public static void setUp() throws IOException {
        server = MockBukkit.mock();
        final FileConfiguration file = YamlConfiguration.loadConfiguration(new File(Constants.CONFIG_FILE_PATH));
        file.set("sql.mysql", false);
        file.set("lobby.enabled", false);
        file.save(Constants.CONFIG_FILE_PATH);

        plugin = MockBukkit.load(EterniaLib.class);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("test the 'eternia' commands")
    void eterniaCommands() throws IOException {
        final Eternia eterniaCmdManager = new Eternia(plugin);
        final ConsoleCommandSender commandSender = server.getConsoleSender();

        for (final String test : plugin.getReloadableConfigList()) {
            eterniaCmdManager.onReload(commandSender, test);
            eterniaCmdManager.onReload(commandSender, test + ":t");
            eterniaCmdManager.onReload(commandSender, test + ":n");
        }

        eterniaCmdManager.onReload(commandSender, "grupinix");
        eterniaCmdManager.onHelp(Mockito.mock(CommandHelp.class));

        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("test the 'eternia' commands")
    void verifyEterniaCommands() {
        final PlayerMock mockPlayer = server.addPlayer(new RandomString(16).nextString());

        Assertions.assertTrue(mockPlayer.performCommand("eternia"));
        Assertions.assertTrue(mockPlayer.performCommand("eternia reload oi"));
        Assertions.assertTrue(mockPlayer.performCommand("eternia reload oi"));
        Assertions.assertTrue(mockPlayer.performCommand("eternia reload config"));
        Assertions.assertTrue(mockPlayer.performCommand("eternia reload config:t"));
        Assertions.assertTrue(mockPlayer.performCommand("eternia reload messages"));
        Assertions.assertTrue(mockPlayer.performCommand("eternia reload lobby"));
    }

    @Test
    @DisplayName("test the 'confirm' commands")
    void commandConfirm() {
        final CommandConfirm commandConfirm = new CommandConfirm(plugin);
        final PlayerMock mockPlayer = server.addPlayer(new RandomString(16).nextString());

        final TestCommand command = new TestCommand();
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

        commandConfirm.onCommandHelp(Mockito.mock(CommandHelp.class));
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("test the 'confirm' commands")
    void commandConfirmInvalid() {
        final CommandConfirm commandConfirm = new CommandConfirm(plugin);
        final PlayerMock mockPlayer = server.addPlayer(new RandomString(16).nextString());

        commandConfirm.onAccept(mockPlayer);
        commandConfirm.onDeny(mockPlayer);

        Assertions.assertTrue(true);
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
