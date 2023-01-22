package br.com.eterniaserver.eternialib.utils.entities;

import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Table(tableName = "eternia_person")
@Getter
@Setter
public class Person {

    @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = true)
    private Integer id;

    @DataField(columnName = "firstName", type = FieldType.STRING)
    private String firstName;

    @DataField(columnName = "birthdate", type = FieldType.DATE)
    private Date birthdate;

}
