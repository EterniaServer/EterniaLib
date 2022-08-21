package br.com.eterniaserver.eternialib.utils;

import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;

import java.math.BigDecimal;

@Table(tableName = "player_profile")
public class ProfileEntity {

    @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = true)
    public Integer id;

    @DataField(columnName = "name", type = FieldType.STRING)
    public String name;

    @DataField(columnName = "money", type = FieldType.DECIMAL)
    public BigDecimal money;

}
