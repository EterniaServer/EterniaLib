package br.com.eterniaserver.eternialib.core.runnables;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.entities.PlayerUUID;
import br.com.eterniaserver.eternialib.database.Database;

import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

@SuppressWarnings("ResultOfMethodCallIgnored")
class TestSynchronizePlayerUUID {

    @Test
    void testSynchronizePlayerUUIDWithExistentPlayer() {
        Database database = Mockito.mock(Database.class);
        PlayerUUID playerUUID = new PlayerUUID();

        final UUID uuid = UUID.randomUUID();
        final String playerName = "yurinogueira";

        playerUUID.setPlayerName(playerName);
        playerUUID.setUuid(uuid);

        Mockito.when(database.get(PlayerUUID.class, uuid)).thenReturn(playerUUID);

        SynchronizePlayerUUID synchronizePlayerUUID = new SynchronizePlayerUUID(uuid, playerName);

        try (MockedStatic<EterniaLib> mockedStatic = Mockito.mockStatic(EterniaLib.class)) {
            mockedStatic.when(EterniaLib::getDatabase).thenReturn(database);

            synchronizePlayerUUID.run();

            Mockito.verify(database, Mockito.times(1)).update(
                    PlayerUUID.class, playerUUID
            );
            Mockito.verify(database, Mockito.times(0)).insert(
                    PlayerUUID.class, playerUUID
            );
        }
    }

    @Test
    void testSynchronizePlayerUUIDWithNonExistentPlayer() {
        Database database = Mockito.mock(Database.class);

        final UUID uuid = UUID.randomUUID();
        final String playerName = "yurinogueira";

        Mockito.when(database.get(PlayerUUID.class, uuid)).thenReturn(null);

        SynchronizePlayerUUID synchronizePlayerUUID = new SynchronizePlayerUUID(uuid, playerName);

        try (MockedStatic<EterniaLib> mockedStatic = Mockito.mockStatic(EterniaLib.class)) {
            mockedStatic.when(EterniaLib::getDatabase).thenReturn(database);

            synchronizePlayerUUID.run();

            Mockito.verify(database, Mockito.times(0)).update(
                    Mockito.any(), Mockito.any(PlayerUUID.class)
            );
            Mockito.verify(database, Mockito.times(1)).insert(
                    Mockito.any(), Mockito.any(PlayerUUID.class)
            );
        }
    }

}
