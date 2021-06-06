package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;

import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import br.com.eterniaserver.eternialib.core.enums.Strings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;

class TestPluginClass {

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
    @DisplayName("Just check the resources")
    void testResources() {
        Assertions.assertEquals("127.0.0.1", plugin.getString(Strings.SQL_HOST));
        Assertions.assertEquals(10, plugin.getInt(Integers.SQL_POOL_SIZE));
        Assertions.assertFalse(plugin.getBool(Booleans.MYSQL));
        Assertions.assertNotNull(plugin.getMessage(Messages.ERROR));

        Assertions.assertFalse(EterniaLib.getMySQL());
    }

}
