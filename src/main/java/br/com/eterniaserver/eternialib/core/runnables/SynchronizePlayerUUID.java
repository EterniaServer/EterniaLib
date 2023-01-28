package br.com.eterniaserver.eternialib.core.runnables;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.entities.PlayerUUID;

import java.util.UUID;

public class SynchronizePlayerUUID implements Runnable {

    private final UUID uuid;
    private final String playerName;

    public SynchronizePlayerUUID(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    @Override
    public void run() {
        PlayerUUID playerUUID = EterniaLib.getDatabase().getEntity(PlayerUUID.class, uuid);
        boolean shouldInsert = playerUUID == null;

        playerUUID = shouldInsert ? new PlayerUUID() : playerUUID;
        playerUUID.setUuid(uuid);
        playerUUID.setPlayerName(playerName);

        if (shouldInsert) {
            EterniaLib.getDatabase().insert(PlayerUUID.class, playerUUID);
        }
        else {
            EterniaLib.getDatabase().update(PlayerUUID.class, playerUUID);
        }
    }

}
