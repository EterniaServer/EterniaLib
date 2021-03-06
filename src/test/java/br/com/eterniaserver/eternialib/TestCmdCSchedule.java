package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import br.com.eterniaserver.eternialib.core.interfaces.CommandConfirmable;

import net.bytebuddy.utility.RandomString;

import org.bukkit.Bukkit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

class TestCmdCSchedule {

    private static ServerMock server;

    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
        MockBukkit.load(EterniaLib.class);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test with a valid user")
    void testWithValidUser() {
        final var playerMock = server.addPlayer(new RandomString(16).nextString());
        final var exampleCommand = new ExampleCommand();

        CmdConfirmationManager.scheduleCommand(playerMock, exampleCommand);

        Assertions.assertNotNull(CmdConfirmationManager.getAndRemoveCommand(playerMock.getUniqueId()));
    }

    @Test
    @DisplayName("Test with a invalid user")
    void testWithInvalidUser() {
        Assertions.assertNull(CmdConfirmationManager.getAndRemoveCommand(UUID.randomUUID()));
    }

    private final static class ExampleCommand implements CommandConfirmable {

        @Override
        public void execute() {
            Bukkit.getConsoleSender().sendMessage("Nothing to see here!");
        }
    }

}
