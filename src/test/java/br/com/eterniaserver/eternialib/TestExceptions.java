package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;

import br.com.eterniaserver.eternialib.core.interfaces.ReloadableConfiguration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
