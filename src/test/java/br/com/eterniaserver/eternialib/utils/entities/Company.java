package br.com.eterniaserver.eternialib.utils.entities;

import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;

@Table(tableName = "eternia_company")
public class Company {

    @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = false)
    public Integer id;

    @PrimaryKeyField(columnName = "name", type = FieldType.STRING, autoIncrement = false)
    public String name;

}
