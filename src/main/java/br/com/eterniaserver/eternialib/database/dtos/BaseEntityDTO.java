package br.com.eterniaserver.eternialib.database.dtos;

import br.com.eterniaserver.eternialib.database.enums.FieldType;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

public abstract class BaseEntityDTO {

    protected String setterName(String fieldName) {
        return "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
    }

    protected String getterName(String fieldName) {
        return "get" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
    }

    protected Class<?> getClassFromFieldType(FieldType fieldType) {
        return switch (fieldType) {
            case STRING, TEXT -> String.class;
            case UUID -> UUID.class;
            case DOUBLE -> Double.class;
            case DECIMAL -> BigDecimal.class;
            case INTEGER -> Integer.class;
            case DATE -> Date.class;
            case TIMESTAMP -> Timestamp.class;
        };
    }

}
