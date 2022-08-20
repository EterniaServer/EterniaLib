package br.com.eterniaserver.eternialib.database.dtos;

import br.com.eterniaserver.eternialib.database.enums.ReferenceMode;

import java.lang.reflect.Field;

public record EntityReferenceDTO(
        Field field,
        String columnName,
        String referenceTableName,
        String referenceColumnName,
        ReferenceMode mode,
        boolean notNull
) {}
