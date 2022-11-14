package br.com.eterniaserver.eternialib.core.entities;

import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;

import java.util.UUID;

@Table(tableName = "eternia_player_uuid")
public class PlayerUUID {

    @PrimaryKeyField(columnName = "uuid", type = FieldType.UUID, autoIncrement = false)
    public UUID uuid;

    @DataField(columnName = "playerName", type = FieldType.STRING, notNull = true)
    public String playerName;

}
