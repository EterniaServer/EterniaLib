package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestEterniaLib {

    @BeforeAll
    public static void setUp() {
        MockBukkit.mock();
        MockBukkit.load(EterniaLib.class);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Static Getters")
    void testGetters() {
        Assertions.assertNotNull(EterniaLib.getDatabase());
        Assertions.assertNotNull(EterniaLib.getCmdManager());
        Assertions.assertNotNull(EterniaLib.getVersion());
    }

}
