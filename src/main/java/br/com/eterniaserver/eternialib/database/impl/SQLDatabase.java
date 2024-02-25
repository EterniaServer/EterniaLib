package br.com.eterniaserver.eternialib.database.impl;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.database.Database;
import br.com.eterniaserver.eternialib.database.HikariSourceConfiguration;
import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityReferenceDTO;
import br.com.eterniaserver.eternialib.database.Entity;
import br.com.eterniaserver.eternialib.database.dtos.SearchField;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import br.com.eterniaserver.eternialib.database.exceptions.DatabaseException;

import org.bukkit.Bukkit;

import java.lang.invoke.MethodHandle;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SQLDatabase implements Database {

    private final Map<Class<?>, Entity<?>> entityMap = new ConcurrentHashMap<>();

    private final HikariSourceConfiguration hikariCfg;

    public SQLDatabase(HikariSourceConfiguration hikariSourceConfiguration) {
        this.hikariCfg = hikariSourceConfiguration;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return hikariCfg.getDataSource().getConnection();
    }

    @Override
    public void closeAllConnections() {
        if (!hikariCfg.getDataSource().isClosed()) {
            hikariCfg.getDataSource().close();
        }
    }

    @Override
    public <T> T findBy(Class<T> objectClass, String fieldName, Object value) {
        return findAllBy(objectClass, fieldName, value).stream().findFirst().orElse(null);
    }

    @Override
    public <T> T findBy(Class<T> objectClass, SearchField... searchFields) {
        return findAllBy(objectClass, searchFields).stream().findFirst().orElse(null);
    }

    @Override
    public <T> List<T> findLike(Class<T> objectClass, String fieldName, Object value) {
        Entity<?> entity = entityMap.get(objectClass);
        EntityDataDTO<?> fieldDataDTO = entity.getDataDTO(fieldName);

        String query = hikariCfg.getSgbdInterface().selectLike(entity.tableName(), fieldDataDTO);
        return getByQuery(entity, objectClass, query, List.of(fieldDataDTO), new SearchField(fieldName, value));
    }

    @Override
    public <T> List<T> findAllBy(Class<T> objectClass, String fieldName, Object value) {
        SearchField searchField = new SearchField(fieldName, value);

        return findAllBy(objectClass, searchField);
    }

    @Override
    public <T> List<T> findAllBy(Class<T> objectClass, SearchField... searchFields) {
        Entity<?> entity = entityMap.get(objectClass);
        List<EntityDataDTO<?>> fieldDataDTOs = new ArrayList<>();

        for (SearchField searchField : searchFields) {
            fieldDataDTOs.add(entity.getDataDTO(searchField.field()));
        }

        String query = hikariCfg.getSgbdInterface().selectBy(entity.tableName(), fieldDataDTOs);
        return getByQuery(entity, objectClass, query, fieldDataDTOs, searchFields);
    }

    @Override
    public <T> List<T> listAll(Class<T> objectClass) {
        Entity<?> entity = entityMap.get(objectClass);

        String query = hikariCfg.getSgbdInterface().selectAll(entity.tableName());
        return getByQuery(entity, objectClass, query, new ArrayList<>());
    }

    @Override
    public <T> List<T> getAllInPrimaryList(Class<T> objectClass, List<Object> values) {
        List<Object> notFound = new ArrayList<>();
        List<T> entities = new ArrayList<>();

        for (Object value : values) {
            T instance = getEntity(objectClass, value);
            if (instance != null) {
                entities.add(instance);
            }
            else {
                notFound.add(value);
            }
        }

        if (notFound.isEmpty()) {
            return entities;
        }

        Entity<?> entity = entityMap.get(objectClass);
        EntityPrimaryKeyDTO<?> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();
        String query = hikariCfg.getSgbdInterface().selectByPrimaryInList(entity.tableName(), primaryKeyDTO);
        String idListString = notFound.stream().map(String::valueOf).collect(Collectors.joining(", "));

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            setValueInStatement(FieldType.STRING, 1, idListString, statement);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                T instance = objectClass.getConstructor().newInstance();
                populateObject(entity, instance, resultSet);
                entities.add(instance);

                Object primaryKey = getValueFromPrimary(primaryKeyDTO.getGetterMethod(), instance);
                entity.addEntity(primaryKey, instance);
            }

            resultSet.close();

        }
        catch (SQLException ignored) {
            loggerSQLError(query);
        }
        catch (Throwable ignored) {
            loggerEntityError(objectClass.getName());
        }

        return entities;
    }

    @Override
    public <T> T get(Class<T> objectClass, Object primaryKey) {
        T instance = getEntity(objectClass, primaryKey);
        if (instance != null) {
            return instance;
        }

        Entity<?> entity = entityMap.get(objectClass);
        EntityPrimaryKeyDTO<?> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();
        String query = hikariCfg.getSgbdInterface().selectByPrimary(entity.tableName(), primaryKeyDTO);

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            setValueInStatement(primaryKeyDTO.getFieldType(), 1, primaryKey, statement);
            ResultSet resultSet = statement.executeQuery();

            instance = objectClass.getConstructor().newInstance();
            if (resultSet.next()) {
                populateObject(entity, instance, resultSet);
            }

            resultSet.close();

        }
        catch (SQLException ignored) {
            loggerSQLError(query);
        }
        catch (Throwable ignored) {
            loggerEntityError(objectClass.getName());
        }

        entity.addEntity(primaryKey, instance);

        return instance;
    }

    @Override
    public <T> boolean save(Class<T> objectClass, T instance) {
        Entity<?> entity = entityMap.get(objectClass);
        EntityPrimaryKeyDTO<?> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();

        Object primaryValue = null;
        try {
            primaryValue = getValueFromPrimary(primaryKeyDTO.getGetterMethod(), instance);
            if (primaryValue != null) {
                update(objectClass, instance);
            }
            else {
                insert(objectClass, instance);
            }
        }
        catch (Throwable ignored) {
            loggerEntityError(objectClass.getName());
        }

        return primaryValue == null;
    }

    @Override
    public <T> void insert(Class<T> objectClass, T instance) {
        Entity<?> entity = entityMap.get(objectClass);
        EntityPrimaryKeyDTO<?> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();

        Object primaryValue = null;
        try {
            primaryValue = getValueFromPrimary(primaryKeyDTO.getGetterMethod(), instance);
        }
        catch (Throwable ignored) {
            loggerEntityError(objectClass.getName());
        }

        if (primaryValue == null) {
            insertAndGetKey(entity, instance);
        }
        else {
            onlyInsert(entity, instance);
        }
    }

    @Override
    public <T> void update(Class<T> objectClass, T instance) {
        String objectName = objectClass.getName();
        Entity<?> entity = entityMap.get(objectClass);
        EntityPrimaryKeyDTO<?> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();

        Object primaryValue = null;
        try {
            primaryValue = getValueFromPrimary(primaryKeyDTO.getGetterMethod(), instance);
            if (primaryValue == null) {
                throw new DatabaseException("Primary key is null");
            }
        }
        catch (DatabaseException exception) {
            Bukkit.getLogger().log(Level.SEVERE, "Entity class: {0}, error: {1}.", new String[]{
                    objectName, exception.getMessage()
            });
        }
        catch (Throwable ignored) {
            loggerEntityError(objectName);
        }

        if (primaryValue == null) {
            return;
        }

        String tableName = entity.tableName();
        List<? extends EntityDataDTO<?>> entityDataDTOS = entity.getEntityDataDTOList();
        String updateQuery = hikariCfg.getSgbdInterface().update(tableName, entityDataDTOS, primaryKeyDTO);
        try (
                Connection connection = getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery)
        ) {
            FieldType primaryType = primaryKeyDTO.getFieldType();

            fillStatement(updateStatement, entityDataDTOS, instance, 1);
            setValueInStatement(primaryType, (entityDataDTOS.size() + 1), primaryValue, updateStatement);

            updateStatement.execute();

            entity.addEntity(primaryValue, instance);
        }
        catch (SQLException ignored) {
            loggerSQLError(updateQuery);
        }
        catch (Throwable ignored) {
            loggerEntityError(objectName);
        }
    }

    @Override
    public <T> void delete(Class<T> objectClass, Object primaryKey) {
        Entity<?> entity = entityMap.get(objectClass);
        EntityPrimaryKeyDTO<?> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();

        String query = hikariCfg.getSgbdInterface().delete(entity.tableName(), primaryKeyDTO);
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            setValueInStatement(primaryKeyDTO.getFieldType(), 1, primaryKey, statement);
            statement.execute();
            entity.removeEntity(primaryKey);

        }
        catch (SQLException ignored) {
            loggerSQLError(query);
        }
    }

    @Override
    @SuppressWarnings("SqlSourceToSinkFlow") // This is a false positive
    public <T> void register(Class<T> entityClass, Entity<T> entity) throws DatabaseException {
        EntityPrimaryKeyDTO<T> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();
        List<EntityDataDTO<T>> dataDTOS = entity.getEntityDataDTOList();
        List<EntityReferenceDTO> referenceDTOS = entity.getReferenceColumns();

        if (entity.tableName().startsWith("%") && entity.tableName().endsWith("%")) {
            entity.setTableName(EterniaLib.getTableName(entity.tableName()));
        }

        StringBuilder builder = new StringBuilder();
        buildCreateTable(builder, entity);
        buildPrimaryKeyColumn(builder, primaryKeyDTO);
        if (!dataDTOS.isEmpty()) {
            builder.append(", ");
            buildDataColumns(builder, dataDTOS);
        }
        if (!referenceDTOS.isEmpty()) {
            builder.append(", ");
            buildReferenceColumns(builder, referenceDTOS);
        }

        builder.append(");");
        String query = builder.toString();

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            throw new DatabaseException("Error when creating " + entity.tableName() + " table.");
        }

        entityMap.put(entityClass, entity);
    }

    private <T> T getEntity(Class<T> objectClass, Object primaryKey) {
        Entity<?> entity = entityMap.get(objectClass);
        if (entity == null) {
            return null;
        }

        Object object = entity.getEntity(primaryKey);
        if (object == null) {
            return null;
        }

        return objectClass.cast(object);
    }

    private <T> List<T> getByQuery(Entity<?> entity,
                                   Class<T> objectClass,
                                   String query,
                                   List<EntityDataDTO<?>> fieldDataDTOs,
                                   SearchField... searchFields) {

        List<T> entities = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            fillStatement(statement, fieldDataDTOs, searchFields);

            ResultSet resultSet = statement.executeQuery();

            EntityPrimaryKeyDTO<?> entityPrimaryKeyDTO = entity.getEntityPrimaryKeyDTO();

            while (resultSet.next()) {
                T instance = objectClass.getConstructor().newInstance();
                populateObject(entity, instance, resultSet);
                entity.addEntity(getValueFromPrimary(entityPrimaryKeyDTO.getGetterMethod(), instance), instance);
                entities.add(instance);
            }

            resultSet.close();
        }
        catch (SQLException exception) {
            loggerSQLError(query);
        }
        catch (Throwable ignored) {
            loggerEntityError(objectClass.getName());
        }

        return entities;
    }

    private <T> void insertAndGetKey(Entity<T> entity, Object instance) {
        String tableName = entity.tableName();
        EntityPrimaryKeyDTO<T> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();
        List<EntityDataDTO<T>> entityDataDTOS = entity.getEntityDataDTOList();

        String insertQuery = hikariCfg.getSgbdInterface().insertWithoutKey(tableName, entityDataDTOS);
        String getIdQuery = hikariCfg.getSgbdInterface().getLastInsertId(tableName);

        try (
                Connection connection = getConnection();
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                PreparedStatement getIdStatement = connection.prepareStatement(getIdQuery)
        ) {
            connection.setAutoCommit(false);
            fillStatement(insertStatement, entityDataDTOS, instance, 1);

            insertStatement.execute();
            ResultSet resultSet = getIdStatement.executeQuery();
            if (resultSet.next()) {
                FieldType type = primaryKeyDTO.getFieldType();
                MethodHandle setter = primaryKeyDTO.getSetterMethod();
                String columnName = resultSet.getMetaData().getColumnName(1);
                setValueInField(setter, type, instance, resultSet, columnName);
            }

            connection.commit();
            connection.setAutoCommit(true);

            Object primaryKey = getValueFromPrimary(primaryKeyDTO.getGetterMethod(), instance);
            entity.addEntity(primaryKey, instance);
        }
        catch (SQLException ignored) {
            String twoQuery = "%s or %s".formatted(insertQuery, getIdQuery);
            loggerSQLError(twoQuery);
        }
        catch (Throwable ignored) {
            loggerEntityError(entity.getClass().getName());
        }
    }

    private <T> void onlyInsert(Entity<T> entity, Object instance) {
        String tableName = entity.tableName();
        EntityPrimaryKeyDTO<T> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();
        List<EntityDataDTO<T>> entityDataDTOS = entity.getEntityDataDTOList();

        String insertQuery = hikariCfg.getSgbdInterface().insert(tableName, entityDataDTOS, primaryKeyDTO);

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(insertQuery)
        ) {
            FieldType primaryType = primaryKeyDTO.getFieldType();
            Object primaryKey = getValueFromPrimary(primaryKeyDTO.getGetterMethod(), instance);

            setValueInStatement(primaryType, 1, primaryKey, statement);
            fillStatement(statement, entityDataDTOS, instance, 2);

            statement.execute();

            entity.addEntity(primaryKey, instance);
        }
        catch (SQLException ignored) {
            loggerSQLError(insertQuery);
        }
        catch (Throwable ignored) {
            loggerEntityError(entity.getClass().getName());
        }
    }

    private <T> void populateObject(Entity<?> entity, T instance, ResultSet resultSet) throws Throwable {
        EntityPrimaryKeyDTO<?> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();
        MethodHandle primarySetter = primaryKeyDTO.getSetterMethod();
        FieldType primaryType = primaryKeyDTO.getFieldType();
        String primaryName = primaryKeyDTO.getColumnName();

        setValueInField(primarySetter, primaryType, instance, resultSet, primaryName);

        for (EntityDataDTO<?> dataDTO : entity.getEntityDataDTOList()) {
            String columnName = dataDTO.getColumnName();
            MethodHandle setter = dataDTO.getSetterMethod();
            FieldType type = dataDTO.getFieldType();

            setValueInField(setter, type, instance, resultSet, columnName);
        }
    }

    private Object getValueFromPrimary(MethodHandle getter, Object instance) throws Throwable {
        return getter.invoke(instance);
    }

    private <T> void setValueInField(
            MethodHandle setter,
            FieldType type,
            T instance,
            ResultSet resultSet,
            String columnName
    ) throws Throwable {
        switch (type) {
            case UUID -> setter.invoke(instance, UUID.fromString(resultSet.getString(columnName)));
            case STRING, TEXT -> setter.invoke(instance, resultSet.getString(columnName));
            case INTEGER -> setter.invoke(instance, resultSet.getInt(columnName));
            case DOUBLE -> setter.invoke(instance, resultSet.getDouble(columnName));
            case DATE -> setter.invoke(instance, resultSet.getDate(columnName));
            case TIMESTAMP -> setter.invoke(instance, resultSet.getTimestamp(columnName));
            case DECIMAL -> setter.invoke(instance, resultSet.getBigDecimal(columnName));
            case BLOB -> setter.invoke(instance, resultSet.getBytes(columnName));
            default -> setter.invoke(instance, resultSet.getObject(columnName));
        }
    }

    private void fillStatement(PreparedStatement preparedStatement,
                               List<? extends EntityDataDTO<?>> entityDataDTOS,
                               Object instance,
                               int startIndex) throws Throwable {
        for (int i = startIndex; i < entityDataDTOS.size() + startIndex; i++) {
            EntityDataDTO<?> entityDataDTO = entityDataDTOS.get(i - startIndex);
            FieldType type = entityDataDTO.getFieldType();
            MethodHandle getter = entityDataDTO.getGetterMethod();

            setValueInStatement(type, i, getter.invoke(instance), preparedStatement);
        }
    }

    private void fillStatement(PreparedStatement preparedStatement,
                               List<? extends EntityDataDTO<?>> entityDataDTOS,
                               SearchField... searchFields) throws Throwable {
        for (int i = 1; i < entityDataDTOS.size() + 1; i++) {
            EntityDataDTO<?> entityDataDTO = entityDataDTOS.get(i - 1);
            FieldType type = entityDataDTO.getFieldType();

            setValueInStatement(type, i, searchFields[i - 1].value(), preparedStatement);
        }
    }

    private void setValueInStatement(
            FieldType type,
            int position,
            Object value,
            PreparedStatement statement
    ) throws SQLException {
        switch (type) {
            case UUID -> statement.setString(position, value.toString());
            case STRING, TEXT -> statement.setString(position, (String) value);
            case INTEGER -> statement.setInt(position, (Integer) value);
            case DOUBLE -> statement.setDouble(position, (Double) value);
            case DATE -> statement.setDate(position, (Date) value);
            case TIMESTAMP -> statement.setTimestamp(position, (Timestamp) value);
            case DECIMAL -> statement.setBigDecimal(position, (BigDecimal) value);
            case BLOB -> statement.setBytes(position, (byte[]) value);
            default -> statement.setObject(position, value);
        }
    }

    private void buildCreateTable(StringBuilder builder, Entity<?> entity) {
        builder.append("CREATE TABLE IF NOT EXISTS ").append(entity.tableName()).append(" (");
    }

    private void buildPrimaryKeyColumn(StringBuilder builder, EntityPrimaryKeyDTO<?> primaryKeyDTO) {
        builder.append(hikariCfg.getSgbdInterface().buildPrimaryColumn(primaryKeyDTO));
    }

    private void buildDataColumns(StringBuilder builder, List<? extends EntityDataDTO<?>> dataDTOS) {
        for (int i = 0; i < dataDTOS.size(); i++) {
            EntityDataDTO<?> dataDTO = dataDTOS.get(i);
            builder.append(hikariCfg.getSgbdInterface().buildDataColumn(dataDTO));
            if (i + 1 != dataDTOS.size()) {
                builder.append(", ");
            }
        }
    }

    private void buildReferenceColumns(StringBuilder builder, List<EntityReferenceDTO> referenceDTOS) {
        for (int i = 0; i < referenceDTOS.size(); i++) {
            EntityReferenceDTO referenceDTO = referenceDTOS.get(i);
            if (referenceDTO.getReferenceTableName().startsWith("%") && referenceDTO.getReferenceTableName().endsWith("%")) {
                referenceDTO.setReferenceTableName(EterniaLib.getTableName(referenceDTO.getReferenceTableName()));
            }

            builder.append(hikariCfg.getSgbdInterface().buildReferenceColumn(referenceDTO));
            if (i + 1 != referenceDTOS.size()) {
                builder.append(", ");
            }
        }
    }

    private void loggerEntityError(String entityClassName) {
        Bukkit.getLogger().log(Level.SEVERE, "Error reading the class: {0}.", entityClassName);
    }

    private void loggerSQLError(String sqlQuery) {
        Bukkit.getLogger().log(Level.SEVERE, "Error in the SQL query: {0}.", sqlQuery);
    }
}
