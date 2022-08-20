package br.com.eterniaserver.eternialib.database.annotations;

import br.com.eterniaserver.eternialib.database.enums.ReferenceMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

public @interface ReferenceField {

    String columnName();

    String referenceTableName();

    String referenceColumnName();

    boolean notNull() default false;

    ReferenceMode mode();

}
