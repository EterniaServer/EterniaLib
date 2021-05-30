package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

import br.com.eterniaserver.eternialib.handlers.AsyncPlayerPreLoginHandler;

import net.bytebuddy.utility.RandomString;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import org.junit.jupiter.api.*;

import java.net.InetAddress;

class TestUUIDFetcher {

    private static ServerMock server;
    private static AsyncPlayerPreLoginHandler listener;

    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
        listener = new AsyncPlayerPreLoginHandler(MockBukkit.load(EterniaLib.class));
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test if the UUIDFetch feature is working")
    void testUUIDFetch() {
        final PlayerMock player = server.addPlayer(new RandomString(16).nextString());
        final InetAddress ip = InetAddress.getLoopbackAddress();
        final AsyncPlayerPreLoginEvent event = new AsyncPlayerPreLoginEvent(player.getName(), ip, player.getUniqueId());

        listener.onAsyncPlayerPreLoginEvent(event);
        Assertions.assertEquals(player.getUniqueId(), UUIDFetcher.getUUIDOf(player.getName()));
    }

}
