package br.com.eterniaserver.eternialib.database.dtos;

import br.com.eterniaserver.eternialib.database.enums.FieldType;

import java.lang.reflect.Field;

public record EntityPrimaryKeyDTO(
        Field field,
        String columnName,
        FieldType type,
        boolean autoIncrement
) {}
