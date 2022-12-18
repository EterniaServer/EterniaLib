package br.com.eterniaserver.eternialib.utils.entities;

import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.ReferenceField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import br.com.eterniaserver.eternialib.database.enums.ReferenceMode;

@Table(tableName = "eternia_person_link")
public class PersonLink {

    @PrimaryKeyField(columnName = "id", type = FieldType.INTEGER, autoIncrement = true)
    public Integer id;

    @ReferenceField(columnName = "firstPersonId",
                    mode = ReferenceMode.CASCADE,
                    notNull = true,
                    referenceTableName = "eternia_person",
                    referenceColumnName = "id")
    @DataField(columnName = "firstPersonId", type = FieldType.INTEGER)
    public Integer firstPersonId;

    @ReferenceField(columnName = "secondPersonId",
                    mode = ReferenceMode.CASCADE,
                    notNull = true,
                    referenceTableName = "eternia_person",
                    referenceColumnName = "id")
    @DataField(columnName = "secondPersonId", type = FieldType.INTEGER)
    public Integer secondPersonId;

}
