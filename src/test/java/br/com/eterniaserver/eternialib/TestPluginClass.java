package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import br.com.eterniaserver.eternialib.core.enums.Integers;
import org.junit.jupiter.api.*;

class TestPluginClass {

    public static ServerMock server;
    public static EterniaLib plugin;

    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(EterniaLib.class);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Just check the resources")
    void testTest() {
        Assertions.assertEquals(0, plugin.getInt(Integers.SQL_POOL_SIZE));
    }

}
