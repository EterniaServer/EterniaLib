package br.com.eterniaserver.eternialib.core.entities;

import java.util.UUID;

import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(tableName = "%eternia_lib_player_uuid%")
public class PlayerUUID {

    @PrimaryKeyField(columnName = "uuid", type = FieldType.UUID, autoIncrement = false)
    private UUID uuid;

    @DataField(columnName = "playerName", type = FieldType.STRING, notNull = true)
    private String playerName;

}
