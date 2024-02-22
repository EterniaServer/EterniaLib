package br.com.eterniaserver.eternialib.utils;

import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import lombok.Getter;
import lombok.Setter;

@Table(tableName = "eternia_item")
@Getter
@Setter
public class Item {

    @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = true)
    private Integer id;

    @DataField(columnName = "blob", type = FieldType.BLOB, notNull = true)
    private byte[] blob;

}
