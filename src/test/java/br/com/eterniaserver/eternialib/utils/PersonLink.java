package br.com.eterniaserver.eternialib.utils;

import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.ReferenceField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import br.com.eterniaserver.eternialib.database.enums.ReferenceMode;
import lombok.Getter;
import lombok.Setter;

@Table(tableName = "eternia_person_link")
@Getter
@Setter
public class PersonLink {

    @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = true)
    private Integer id;

    @ReferenceField(columnName = "firstPersonId",
                    mode = ReferenceMode.CASCADE,
                    notNull = true,
                    referenceTableName = "eternia_person",
                    referenceColumnName = "id")
    @DataField(columnName = "firstPersonId", notNull = true, type = FieldType.INTEGER)
    private Integer firstPersonId;

    @ReferenceField(columnName = "secondPersonId",
                    mode = ReferenceMode.CASCADE,
                    notNull = true,
                    referenceTableName = "eternia_person",
                    referenceColumnName = "id")
    @DataField(columnName = "secondPersonId", notNull = true, type = FieldType.INTEGER)
    private Integer secondPersonId;

}
