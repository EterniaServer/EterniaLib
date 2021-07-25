package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import br.com.eterniaserver.eternialib.handlers.AsyncPlayerPreLoginHandler;

import net.bytebuddy.utility.RandomString;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

class TestUUIDFetcher {

    private static ServerMock server;
    private static AsyncPlayerPreLoginHandler listener;

    @BeforeAll
    public static void setUp() throws IOException {
        server = MockBukkit.mock();
        final FileConfiguration file = YamlConfiguration.loadConfiguration(new File(Constants.CONFIG_FILE_PATH));
        file.set("sql.mysql", false);
        file.save(Constants.CONFIG_FILE_PATH);

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

        final AsyncPlayerPreLoginEvent secondEvent = new AsyncPlayerPreLoginEvent(player.getName(), ip, player.getUniqueId());
        listener.onAsyncPlayerPreLoginEvent(secondEvent);
        Assertions.assertNotNull(UUIDFetcher.getUUIDOf(player.getName()));
    }

}
