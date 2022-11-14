package br.com.eterniaserver.eternialib.database;

import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityReferenceDTO;
import br.com.eterniaserver.eternialib.database.annotations.DataField;
import br.com.eterniaserver.eternialib.database.annotations.PrimaryKeyField;
import br.com.eterniaserver.eternialib.database.annotations.Table;
import br.com.eterniaserver.eternialib.database.annotations.ReferenceField;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import br.com.eterniaserver.eternialib.database.enums.ReferenceMode;
import br.com.eterniaserver.eternialib.database.exceptions.EntityException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Entity<T> {

    private final Class<T> entityClass;
    private final Field primaryKeyField;
    private final List<Field> dataFields;
    private final List<Field> referenceFields;
    private final Map<Object, Object> entitiesCache = new ConcurrentHashMap<>();

    public Entity(Class<T> entityClass) throws EntityException {
        Field[] fields = entityClass.getFields();
        List<Field> primaryKeyFields = getFieldsByAnnotation(entityClass.getFields(), PrimaryKeyField.class);

        if (primaryKeyFields.size() > 1) {
            throw new EntityException("EterniaLib only supports single primary keys");
        }

        this.primaryKeyField = primaryKeyFields.get(0);
        this.entityClass = entityClass;
        this.dataFields = getFieldsByAnnotation(fields, DataField.class);
        this.referenceFields = getFieldsByAnnotation(fields, ReferenceField.class);
    }

    public Object getEntity(Object primaryKey) {
        return entitiesCache.get(primaryKey);
    }

    public void addEntity(Object primaryKey, Object entity) {
        entitiesCache.put(primaryKey, entity);
    }

    public void removeEntity(Object primaryKey) {
        entitiesCache.remove(primaryKey);
    }

    private List<Field> getFieldsByAnnotation(Field[] fields, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(fields).filter(field -> field.isAnnotationPresent(annotationClass)).toList();
    }

    public String tableName() {
        Table table = entityClass.getAnnotation(Table.class);
        return table.tableName();
    }

    public EntityPrimaryKeyDTO getPrimaryKey() {
        PrimaryKeyField key = primaryKeyField.getAnnotation(PrimaryKeyField.class);
        String columnName = key.columnName();
        FieldType fieldType = key.type();
        boolean autoIncrement = key.autoIncrement();

        return new EntityPrimaryKeyDTO(
                primaryKeyField,
                columnName,
                fieldType,
                autoIncrement
        );
    }


    public List<EntityDataDTO> getDataColumns() {
        List<EntityDataDTO> data = new ArrayList<>();

        for (Field field : this.dataFields) {
            DataField column = field.getAnnotation(DataField.class);
            String columnName = column.columnName();
            FieldType fieldType = column.type();
            boolean notNull = column.notNull();

            EntityDataDTO dataDTO = new EntityDataDTO(
                    field,
                    columnName,
                    fieldType,
                    notNull
            );

            data.add(dataDTO);
        }

        return data;
    }

    public List<EntityReferenceDTO> getReferenceColumns() {
        List<EntityReferenceDTO> data = new ArrayList<>();

        for (Field field : this.referenceFields) {
            ReferenceField reference = field.getAnnotation(ReferenceField.class);
            String columnName = reference.columnName();
            String referenceTableName = reference.referenceTableName();
            String referenceColumnName = reference.referenceColumnName();
            ReferenceMode mode = reference.mode();
            boolean notNull = reference.notNull();

            EntityReferenceDTO referenceDTO = new EntityReferenceDTO(
                    field,
                    columnName,
                    referenceTableName,
                    referenceColumnName,
                    mode,
                    notNull
            );

            data.add(referenceDTO);
        }

        return data;
    }

}
