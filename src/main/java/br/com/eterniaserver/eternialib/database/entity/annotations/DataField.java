package br.com.eterniaserver.eternialib.database.entity.annotations;

import br.com.eterniaserver.eternialib.database.enums.FieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataField {

    String columnName();

    FieldType type();

    boolean notNull() default false;

}
