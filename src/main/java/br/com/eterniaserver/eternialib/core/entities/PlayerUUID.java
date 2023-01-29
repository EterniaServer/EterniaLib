package br.com.eterniaserver.eternialib.core.entities;

import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;

import java.util.UUID;

@Table(tableName = "eternia_player_uuid")
public class PlayerUUID {

    @PrimaryKeyField(columnName = "uuid", type = FieldType.UUID, autoIncrement = false)
    private UUID uuid;

    @DataField(columnName = "playerName", type = FieldType.STRING, notNull = true)
    private String playerName;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

}
