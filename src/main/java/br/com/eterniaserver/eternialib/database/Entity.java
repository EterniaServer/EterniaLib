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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Entity<T> {

    private final Class<T> entityClass;
    private final EntityPrimaryKeyDTO<T> entityPrimaryKeyDTO;
    private final List<EntityDataDTO<T>> entityDataDTOList;
    private final List<Field> referenceFields;
    private final Map<Object, Object> entitiesCache = new ConcurrentHashMap<>();

    public Entity(Class<T> entityClass) throws EntityException, NoSuchMethodException, IllegalAccessException {
        this.entityClass = entityClass;

        Field[] fields = entityClass.getDeclaredFields();
        List<Field> primaryKeyFields = getFieldsByAnnotation(fields, PrimaryKeyField.class);

        if (primaryKeyFields.size() > 1) {
            throw new EntityException("EterniaLib only supports single primary keys");
        }

        Field primaryKeyField = primaryKeyFields.get(0);
        List<Field> dataFields = getFieldsByAnnotation(fields, DataField.class);

        this.entityPrimaryKeyDTO = getPrimaryKey(primaryKeyField);
        this.entityDataDTOList = getDataColumns(dataFields);
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

    public EntityPrimaryKeyDTO<T> getEntityPrimaryKeyDTO() {
        return entityPrimaryKeyDTO;
    }

    public List<EntityDataDTO<T>> getEntityDataDTOList() {
        return entityDataDTOList;
    }

    private EntityPrimaryKeyDTO<T> getPrimaryKey(Field primaryKeyField) throws NoSuchMethodException, IllegalAccessException {
        PrimaryKeyField key = primaryKeyField.getAnnotation(PrimaryKeyField.class);
        String columnName = key.columnName();
        FieldType fieldType = key.type();
        boolean autoIncrement = key.autoIncrement();

        return new EntityPrimaryKeyDTO<>(
                entityClass,
                primaryKeyField.getName(),
                columnName,
                fieldType,
                autoIncrement
        );
    }

    private List<EntityDataDTO<T>> getDataColumns(List<Field> dataFields) throws NoSuchMethodException, IllegalAccessException {
        List<EntityDataDTO<T>> data = new ArrayList<>();

        for (Field field : dataFields) {
            DataField column = field.getAnnotation(DataField.class);
            String columnName = column.columnName();
            FieldType fieldType = column.type();
            boolean notNull = column.notNull();

            EntityDataDTO<T> dataDTO = new EntityDataDTO<>(
                    this.entityClass,
                    field.getName(),
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
