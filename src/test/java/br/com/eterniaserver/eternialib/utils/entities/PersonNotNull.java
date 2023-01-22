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
public class PersonNotNull {

    @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = false)
    private Integer id;

    @DataField(columnName = "firstName", type = FieldType.STRING, notNull = true)
    private String firstName;

    @DataField(columnName = "birthdate", type = FieldType.DATE, notNull = true)
    private Date birthdate;

}