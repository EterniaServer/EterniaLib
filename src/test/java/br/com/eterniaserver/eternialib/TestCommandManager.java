package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import br.com.eterniaserver.eternialib.objects.CommandClass;

import co.aikar.commands.ConditionFailedException;

import net.bytebuddy.utility.RandomString;

import org.bukkit.GameMode;
import org.junit.jupiter.api.*;

import java.util.List;

class TestCommandManager {

    private static ServerMock server;

    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
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

}
