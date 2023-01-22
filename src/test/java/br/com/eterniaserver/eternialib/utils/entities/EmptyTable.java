package br.com.eterniaserver.eternialib.utils.entities;

import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import lombok.Getter;
import lombok.Setter;

@Table(tableName = "eternia_empty_table")
@Getter
@Setter
public class EmptyTable {

    @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = true)
    private Integer id;

}
