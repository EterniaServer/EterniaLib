package br.com.eterniaserver.eternialib.database.dtos;

import br.com.eterniaserver.eternialib.database.enums.FieldType;

import java.lang.reflect.Field;

public record EntityDataDTO(
        Field field,
        String columnName,
        FieldType type,
        boolean notNull
) {}
