package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;

import br.com.eterniaserver.eternialib.core.interfaces.ReloadableConfiguration;

import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Field;

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
    void testReloadableLobbyException() {
        final ReloadableConfiguration config = plugin.getReloadableConfiguration("eternialib_lobby".hashCode());

        Assertions.assertThrows(UnsupportedOperationException.class, config::executeCritical);
    }

    @Test
    @DisplayName("Verify that the exception wont block the plugin")
    void testReloadableMessagesException() {
        final ReloadableConfiguration config = plugin.getReloadableConfiguration("eternialib_messages".hashCode());

        Assertions.assertThrows(UnsupportedOperationException.class, config::executeCritical);
    }

    @Test
    @DisplayName("test the exception of messages'")
    void verifyMessagesException() throws NoSuchFieldException, IllegalAccessException, IOException {
        final ReloadableConfiguration config = plugin.getReloadableConfiguration("eternialib_messages".hashCode());
        final Field field = config.getClass().getDeclaredField("config");
        field.setAccessible(true);

        BDDMockito.willThrow(IOException.class).given(Mockito.spy((FileConfiguration) field.get(config))).save(Constants.MESSAGES_FILE_PATH);

        config.executeConfig();
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("test the exception of configs'")
    void verifyConfigException() throws NoSuchFieldException, IllegalAccessException, IOException {
        final ReloadableConfiguration config = plugin.getReloadableConfiguration("eternialib_config".hashCode());
        final Field field = config.getClass().getDeclaredField("config");
        field.setAccessible(true);

        BDDMockito.willThrow(IOException.class).given(Mockito.spy((FileConfiguration) field.get(config))).save(Constants.CONFIG_FILE_PATH);

        config.executeConfig();
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("test the exception of lobbyCfg'")
    void verifyLobbyException() throws NoSuchFieldException, IllegalAccessException, IOException {
        final ReloadableConfiguration config = plugin.getReloadableConfiguration("eternialib_lobby".hashCode());
        final Field field = config.getClass().getDeclaredField("config");
        field.setAccessible(true);

        BDDMockito.willThrow(IOException.class).given(Mockito.spy((FileConfiguration) field.get(config))).save(Constants.LOBBY_FILE_PATH);

        config.executeConfig();
        Assertions.assertTrue(true);
    }

}
