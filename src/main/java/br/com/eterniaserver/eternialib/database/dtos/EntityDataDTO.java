package br.com.eterniaserver.eternialib.database.dtos;

import br.com.eterniaserver.eternialib.database.enums.FieldType;
import lombok.Getter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;


@Getter
public class EntityDataDTO<T> extends BaseEntityDTO {

    private final MethodHandle getterMethod;
    private final MethodHandle setterMethod;

    private final String columnName;
    private final FieldType fieldType;
    private final boolean notNull;

    public EntityDataDTO(
            Class<T> clazz,
            String fieldName,
            String columnName,
            FieldType fieldType,
            boolean notNull
    ) throws NoSuchMethodException, IllegalAccessException {
        this.columnName = columnName;
        this.fieldType = fieldType;
        this.notNull = notNull;

        Class<?> fieldClassType = getClassFromFieldType(fieldType);

        Method getterMethod = clazz.getMethod(getterName(fieldName));
        Method setterMethod = clazz.getMethod(setterName(fieldName), fieldClassType);

        this.getterMethod = MethodHandles.lookup().unreflect(getterMethod);
        this.setterMethod = MethodHandles.lookup().unreflect(setterMethod);
    }

}
