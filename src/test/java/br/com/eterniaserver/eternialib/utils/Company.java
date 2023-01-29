package br.com.eterniaserver.eternialib.utils;

import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import lombok.Getter;
import lombok.Setter;

@Table(tableName = "eternia_company")
@Getter
@Setter
public class Company {

    @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = false)
    private Integer id;

    @PrimaryKeyField(columnName = "name", type = FieldType.STRING, autoIncrement = false)
    private String name;

}
