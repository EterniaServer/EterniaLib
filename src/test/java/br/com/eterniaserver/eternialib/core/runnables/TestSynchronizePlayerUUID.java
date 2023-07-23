package br.com.eterniaserver.eternialib.core.runnables;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.entities.PlayerUUID;
import br.com.eterniaserver.eternialib.database.DatabaseInterface;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

class TestSynchronizePlayerUUID {

    @Test
    void testSynchronizePlayerUUIDWithExistentPlayer() {
        DatabaseInterface databaseInterface = Mockito.mock(DatabaseInterface.class);
        PlayerUUID playerUUID = new PlayerUUID();

        final UUID uuid = UUID.randomUUID();
        final String playerName = "yurinogueira";

        playerUUID.setPlayerName(playerName);
        playerUUID.setUuid(uuid);

        Mockito.when(databaseInterface.get(PlayerUUID.class, uuid)).thenReturn(playerUUID);

        SynchronizePlayerUUID synchronizePlayerUUID = new SynchronizePlayerUUID(uuid, playerName);

        try (MockedStatic<EterniaLib> mockedStatic = Mockito.mockStatic(EterniaLib.class)) {
            mockedStatic.when(EterniaLib::getDatabase).thenReturn(databaseInterface);

            synchronizePlayerUUID.run();

            Mockito.verify(databaseInterface, Mockito.times(1)).update(
                    PlayerUUID.class, playerUUID
            );
            Mockito.verify(databaseInterface, Mockito.times(0)).insert(
                    PlayerUUID.class, playerUUID
            );
        }
    }

    @Test
    void testSynchronizePlayerUUIDWithNonExistentPlayer() {
        DatabaseInterface databaseInterface = Mockito.mock(DatabaseInterface.class);

        final UUID uuid = UUID.randomUUID();
        final String playerName = "yurinogueira";

        Mockito.when(databaseInterface.get(PlayerUUID.class, uuid)).thenReturn(null);

        SynchronizePlayerUUID synchronizePlayerUUID = new SynchronizePlayerUUID(uuid, playerName);

        try (MockedStatic<EterniaLib> mockedStatic = Mockito.mockStatic(EterniaLib.class)) {
            mockedStatic.when(EterniaLib::getDatabase).thenReturn(databaseInterface);

            synchronizePlayerUUID.run();

            Mockito.verify(databaseInterface, Mockito.times(0)).update(
                    Mockito.any(), Mockito.any(PlayerUUID.class)
            );
            Mockito.verify(databaseInterface, Mockito.times(1)).insert(
                    Mockito.any(), Mockito.any(PlayerUUID.class)
            );
        }
    }

}
