package br.com.eterniaserver.eternialib.utils;

import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.UUID;

@Table(tableName = "eternia_complete")
@Getter
@Setter
public class Complete {

    @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = true)
    @DataField(columnName = "id", type = FieldType.STRING)
    private Integer id;

    @PrimaryKeyField(columnName = "uuid", type = FieldType.UUID, autoIncrement = true)
    @DataField(columnName = "uuid", type = FieldType.UUID)
    private UUID uuid;

    @PrimaryKeyField(columnName = "first_name", type = FieldType.STRING, autoIncrement = true)
    @DataField(columnName = "firstName", type = FieldType.STRING)
    private String firstName;

    @PrimaryKeyField(columnName = "description", type = FieldType.TEXT, autoIncrement = true)
    @DataField(columnName = "description", type = FieldType.TEXT)
    private String description;

    @PrimaryKeyField(columnName = "birthdate", type = FieldType.DATE, autoIncrement = true)
    @DataField(columnName = "birthdate", type = FieldType.DATE)
    private Date birthdate;

    @PrimaryKeyField(columnName = "money", type = FieldType.DECIMAL, autoIncrement = true)
    @DataField(columnName = "money", type = FieldType.DECIMAL)
    private BigDecimal money;

    @PrimaryKeyField(columnName = "experience", type = FieldType.DOUBLE, autoIncrement = true)
    @DataField(columnName = "experience", type = FieldType.DOUBLE)
    private Double experience;

    @PrimaryKeyField(columnName = "end_date", type = FieldType.TIMESTAMP, autoIncrement = true)
    @DataField(columnName = "end_date", type = FieldType.TIMESTAMP)
    private Timestamp endDate;

}
