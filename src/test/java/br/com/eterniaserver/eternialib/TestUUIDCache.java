package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class TestUUIDCache {

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
    void testGetUUIDFromNone() {
        String name = "yurinaomeudeus";
        UUID result = EterniaLib.getUUIDOf(name);

        Assertions.assertNull(result);
    }

    @Test
    void testGetUUIDAfterPlayerJoin() {
        String name = "yurinogueira";
        PlayerMock player = server.addPlayer(name);

        UUID expect = player.getUniqueId();
        UUID result = EterniaLib.getUUIDOf(name);

        Assertions.assertEquals(expect, result);
    }

}
