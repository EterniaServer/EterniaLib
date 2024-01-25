package br.com.eterniaserver.eternialib.database.dtos;

import br.com.eterniaserver.eternialib.database.enums.ReferenceMode;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Getter
public class EntityReferenceDTO {

    private final Field field;
    private final String columnName;
    private final String referenceColumnName;
    private final ReferenceMode mode;
    private final boolean notNull;

    @Setter
    private String referenceTableName;

    public EntityReferenceDTO(Field field, String columnName, String referenceTableName, String referenceColumnName, ReferenceMode mode, boolean notNull) {
        this.field = field;
        this.columnName = columnName;
        this.referenceTableName = referenceTableName;
        this.referenceColumnName = referenceColumnName;
        this.mode = mode;
        this.notNull = notNull;
    }


}
