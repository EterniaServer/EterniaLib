package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import br.com.eterniaserver.eternialib.objects.CommandClass;
import br.com.eterniaserver.eternialib.objects.User;

import co.aikar.commands.ConditionFailedException;

import net.bytebuddy.utility.RandomString;

import org.bukkit.GameMode;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

class TestCommandManager {

    private static ServerMock server;

    @BeforeAll
    public static void setUp() throws IOException {
        server = MockBukkit.mock();
        final FileConfiguration file = YamlConfiguration.loadConfiguration(new File(Constants.CONFIG_FILE_PATH));
        file.set("sql.mysql", false);
        file.set("lobby.enabled", false);
        file.save(Constants.CONFIG_FILE_PATH);

        MockBukkit.load(EterniaLib.class);

        CommandManager.getCommandReplacements().addReplacement("command", "test");
        CommandManager.getCommandCompletions().registerStaticCompletion("completion", List.of("test", "tset"));
        CommandManager.getCommandConditions().addCondition(String.class, "pa_test", (c, exec, value) -> {
            if (value == null) {
                return;
            }
            if (value.contains("test")) {
                throw new ConditionFailedException("Can't test!");
            }
        });
        CommandManager.getCommandContexts().registerIssuerAwareContext(User.class, c -> new User(c.getPlayer()));

        CommandManager.registerCommand(new CommandClass());
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Verify with Command is registered")
    void verifyCommand() {
        final var mockPlayer = server.addPlayer(new RandomString(16).nextString());

        mockPlayer.performCommand("test");

        Assertions.assertEquals(GameMode.SPECTATOR, mockPlayer.getGameMode());
    }

    @Test
    @DisplayName("Verify CommandConditions from CommandManager")
    void verifyCommandConditions() {
        final var mockPlayer = server.addPlayer(new RandomString(16).nextString());

        mockPlayer.performCommand("test test");

        Assertions.assertNotEquals(GameMode.SPECTATOR, mockPlayer.getGameMode());
    }

    @Test
    @DisplayName("Verify the CommandContexts")
    void verifyCommandContexts() {
        final var mockPlayer = server.addPlayer(new RandomString(16).nextString());

        mockPlayer.performCommand("user");

        Assertions.assertEquals(GameMode.ADVENTURE, mockPlayer.getGameMode());
    }

}
