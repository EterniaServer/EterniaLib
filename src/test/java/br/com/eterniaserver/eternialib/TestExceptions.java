package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;

import br.com.eterniaserver.eternialib.core.interfaces.ReloadableConfiguration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;

class TestExceptions {

    private static EterniaLib plugin;

    @BeforeAll
    public static void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(EterniaLib.class);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Verify that the exception wont block the plugin")
    void testReloadableLobbyException() throws IOException {
        final FileConfiguration outConfig = Mockito.mock(YamlConfiguration.class);

        BDDMockito.willThrow(new IOException()).given(outConfig).save(Constants.LOBBY_FILE_PATH);
        final ReloadableConfiguration config = plugin.getReloadableConfiguration("eternialib_lobby".hashCode());
        config.executeConfig();

        Assertions.assertThrows(UnsupportedOperationException.class, config::executeCritical);
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Verify that the exception wont block the plugin")
    void testReloadableConfigException() throws IOException {
        final FileConfiguration outConfig = Mockito.mock(YamlConfiguration.class);

        BDDMockito.willThrow(new IOException()).given(outConfig).save(Constants.CONFIG_FILE_PATH);
        final ReloadableConfiguration config = plugin.getReloadableConfiguration("eternialib_config".hashCode());
        config.executeConfig();
        Assertions.assertTrue(true);
    }


    @Test
    @DisplayName("Verify that the exception wont block the plugin")
    void testReloadableMessagesException() throws IOException {
        final FileConfiguration outConfig = Mockito.mock(YamlConfiguration.class);

        BDDMockito.willThrow(new IOException()).given(outConfig).save(Constants.MESSAGES_FILE_PATH);
        final ReloadableConfiguration config = plugin.getReloadableConfiguration("eternialib_messages".hashCode());
        config.executeConfig();

        Assertions.assertThrows(UnsupportedOperationException.class, config::executeCritical);
        Assertions.assertTrue(true);
    }
}
