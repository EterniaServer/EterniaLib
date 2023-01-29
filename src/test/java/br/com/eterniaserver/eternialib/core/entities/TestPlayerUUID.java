package br.com.eterniaserver.eternialib.core.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class TestPlayerUUID {

    @Test
    void testPlayerUUID() {
        String playerName = "yurinogueira";
        UUID uuid = UUID.randomUUID();
        PlayerUUID playerUUID = new PlayerUUID();

        playerUUID.setPlayerName(playerName);
        playerUUID.setUuid(uuid);

        Assertions.assertEquals(playerName, playerUUID.getPlayerName());
        Assertions.assertEquals(uuid, playerUUID.getUuid());
    }

}
