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
        PlayerUUID playerUUID = EterniaLib.getDatabase().get(PlayerUUID.class, uuid);

        if (playerUUID == null || playerUUID.getUuid() == null) {
            playerUUID = new PlayerUUID(uuid, playerName);
            EterniaLib.getDatabase().insert(PlayerUUID.class, playerUUID);
        }
        else {
            playerUUID.setUuid(uuid);
            playerUUID.setPlayerName(playerName);
            EterniaLib.getDatabase().update(PlayerUUID.class, playerUUID);
        }
    }

} 
