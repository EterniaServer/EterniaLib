package br.com.eterniaserver.eternialib.database.dtos;

import br.com.eterniaserver.eternialib.database.enums.FieldType;
import lombok.Getter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

@Getter
public class EntityPrimaryKeyDTO<T> extends BaseEntityDTO {

    private final MethodHandle getterMethod;
    private final MethodHandle setterMethod;

    private final String columnName;
    private final FieldType fieldType;
    private final boolean autoIncrement;

    public EntityPrimaryKeyDTO(
            Class<T> clazz,
            String fieldName,
            String columnName,
            FieldType fieldType,
            boolean autoIncrement
    ) throws NoSuchMethodException, IllegalAccessException {
        this.columnName = columnName;
        this.fieldType = fieldType;
        this.autoIncrement = autoIncrement;

        Class<?> fieldClassType = getClassFromFieldType(fieldType);

        Method getterMethodBase = clazz.getMethod(getterName(fieldName));
        Method setterMethodBase = clazz.getMethod(setterName(fieldName), fieldClassType);

        this.getterMethod = MethodHandles.lookup().unreflect(getterMethodBase);
        this.setterMethod = MethodHandles.lookup().unreflect(setterMethodBase);
    }

}
