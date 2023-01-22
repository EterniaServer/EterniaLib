package br.com.eterniaserver.eternialib.database.impl;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.database.DatabaseInterface;
import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityReferenceDTO;
import br.com.eterniaserver.eternialib.database.Entity;
import br.com.eterniaserver.eternialib.database.enums.DatabaseType;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import br.com.eterniaserver.eternialib.database.exceptions.DatabaseException;
import br.com.eterniaserver.eternialib.database.impl.sgbds.MariaDBSGBD;
import br.com.eterniaserver.eternialib.database.impl.sgbds.MySQLSGBD;
import br.com.eterniaserver.eternialib.database.impl.sgbds.SQLiteSGBD;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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

public class SQLDatabase implements DatabaseInterface {

    public static class HikariConnection {

        private final HikariDataSource dataSource;
        private final SGBDInterface sgbdInterface;

        public HikariDataSource getDataSource() {
            return dataSource;
        }

        public SGBDInterface getSGBDInterface() {
            return sgbdInterface;
        }

        public HikariConnection(EterniaLib plugin) {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setPoolName("EterniaLib HikariPool");

            String databaseType = plugin.getString(Strings.DATABASE_TYPE);
            DatabaseType type = DatabaseType.valueOf(databaseType);
            this.sgbdInterface = switch (type) {
                case MYSQL -> new MySQLSGBD();
                case MARIADB -> new MariaDBSGBD();
                case SQLITE -> new SQLiteSGBD();
            };

            if (type == DatabaseType.SQLITE) {
                hikariConfig.setJdbcUrl(
                        "jdbc:" + sgbdInterface.jdbcStr() +
                        plugin.getString(Strings.DATABASE_HOST)
                );
                hikariConfig.setDriverClassName("org.sqlite.JDBC");
            }
            else {
                hikariConfig.setJdbcUrl(
                        "jdbc:" + sgbdInterface.jdbcStr() +
                        plugin.getString(Strings.DATABASE_HOST) +
                        ":" + plugin.getString(Strings.DATABASE_PORT) +
                        "/" + plugin.getString(Strings.DATABASE_DATABASE)
                );
                hikariConfig.setUsername(plugin.getString(Strings.DATABASE_USER));
                hikariConfig.setPassword(plugin.getString(Strings.DATABASE_PASSWORD));

                // MySQL specific configurations
                hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
                hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
                hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                // Pool configurations
                hikariConfig.setMaxLifetime(plugin.getInteger(Integers.HIKARI_MAX_LIFE_TIME));
                hikariConfig.setConnectionTimeout(plugin.getInteger(Integers.HIKARI_CONNECTION_TIME_OUT));
                hikariConfig.setLeakDetectionThreshold(plugin.getInteger(Integers.HIKARI_LEAK_THRESHOLD));
                hikariConfig.setMinimumIdle(plugin.getInteger(Integers.HIKARI_MIN_POOL_SIZE));
                hikariConfig.setMaximumPoolSize(plugin.getInteger(Integers.HIKARI_MAX_POOL_SIZE));
                hikariConfig.setAllowPoolSuspension(plugin.getBoolean(Booleans.HIKARI_ALLOW_POOL_SUSPENSION));
            }

            this.dataSource = new HikariDataSource(hikariConfig);
        }
    }

    private final HikariDataSource dataSource;
    private final SGBDInterface sgbdInterface;
    private final Map<Class<?>, Entity<?>> entityMap = new ConcurrentHashMap<>();

    public SQLDatabase(HikariDataSource dataSource, SGBDInterface sgbdInterface) {
        this.dataSource = dataSource;
        this.sgbdInterface = sgbdInterface;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void closeAllConnections() {
        if (!dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public <T> T getEntity(Class<T> objectClass, Object primaryKey) {
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

    @Override
    public <T> List<T> listAll(Class<T> objectClass) {
        List<T> entities = new ArrayList<>();
        Entity<T> entity = getEntity(objectClass);

        String query = sgbdInterface.selectAll(entity.tableName());
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()
        ) {
            EntityPrimaryKeyDTO<T> entityPrimaryKeyDTO = entity.getEntityPrimaryKeyDTO();

            while (resultSet.next()) {
                T instance = objectClass.getConstructor().newInstance();
                populateObject(entity, instance, resultSet);
                entity.addEntity(getValueFromPrimary(entityPrimaryKeyDTO.getGetterMethod(), instance), instance);
                entities.add(instance);
            }
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
        Entity<T> entity = getEntity(objectClass);
        Object object = entity.getEntity(primaryKey);
        if (object != null) {
            return objectClass.cast(object);
        }

        EntityPrimaryKeyDTO<T> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();
        String query = sgbdInterface.selectByPrimary(entity.tableName(), primaryKeyDTO);

        T instance = null;
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
    public <T> void insert(Class<T> objectClass, Object instance) {
        Entity<T> entity = getEntity(objectClass);
        EntityPrimaryKeyDTO<T> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();

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
    public <T> void update(Class<T> objectClass, Object instance) {
        String objectName = objectClass.getName();
        Entity<T> entity = getEntity(objectClass);
        EntityPrimaryKeyDTO<T> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();

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
        List<EntityDataDTO<T>> entityDataDTOS = entity.getEntityDataDTOList();
        String updateQuery = sgbdInterface.update(tableName, entityDataDTOS, primaryKeyDTO);
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
        Entity<T> entity = getEntity(objectClass);
        EntityPrimaryKeyDTO<T> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();

        String query = sgbdInterface.delete(entity.tableName(), primaryKeyDTO);
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
    public <T> void register(Class<T> entityClass, Entity<T> entity) throws DatabaseException {
        EntityPrimaryKeyDTO<T> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();
        List<EntityDataDTO<T>> dataDTOS = entity.getEntityDataDTOList();
        List<EntityReferenceDTO> referenceDTOS = entity.getReferenceColumns();

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
        } catch (SQLException ignored) {
            throw new DatabaseException("Error when creating " + entity.tableName() + " table.");
        }

        entityMap.put(entityClass, entity);
    }

    @SuppressWarnings("unchecked")
    private <T> Entity<T> getEntity(Class<T> objectClass) {
        return (Entity<T>) entityMap.get(objectClass);
    }

    private <T> void insertAndGetKey(Entity<T> entity, Object instance) {
        String tableName = entity.tableName();
        EntityPrimaryKeyDTO<T> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();
        List<EntityDataDTO<T>> entityDataDTOS = entity.getEntityDataDTOList();

        String insertQuery = sgbdInterface.insertWithoutKey(tableName, entityDataDTOS);
        String getIdQuery = sgbdInterface.getLastInsertId(tableName);

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

        String insertQuery = sgbdInterface.insert(tableName, entityDataDTOS, primaryKeyDTO);

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

    private <T> void populateObject(Entity<T> entity, T instance, ResultSet resultSet) throws Throwable {
        EntityPrimaryKeyDTO<T> primaryKeyDTO = entity.getEntityPrimaryKeyDTO();
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
            default -> setter.invoke(instance, resultSet.getObject(columnName));
        }
    }

    private <T> void fillStatement(PreparedStatement preparedStatement,
                               List<EntityDataDTO<T>> entityDataDTOS,
                               Object instance,
                               int startIndex) throws Throwable {
        for (int i = startIndex; i < entityDataDTOS.size() + startIndex; i++) {
            EntityDataDTO<T> entityDataDTO = entityDataDTOS.get(i - startIndex);
            FieldType type = entityDataDTO.getFieldType();
            MethodHandle getter = entityDataDTO.getGetterMethod();

            setValueInStatement(type, i, getter.invoke(instance), preparedStatement);
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
            default -> statement.setObject(position, value);
        }
    }

    private <T> void buildCreateTable(StringBuilder builder, Entity<T> entity) {
        builder.append("CREATE TABLE IF NOT EXISTS ").append(entity.tableName()).append(" (");
    }

    private <T> void buildPrimaryKeyColumn(StringBuilder builder, EntityPrimaryKeyDTO<T> primaryKeyDTO) {
        builder.append(sgbdInterface.buildPrimaryColumn(primaryKeyDTO));
    }

    private <T> void buildDataColumns(StringBuilder builder, List<EntityDataDTO<T>> dataDTOS) {
        for (int i = 0; i < dataDTOS.size(); i++) {
            EntityDataDTO<T> dataDTO = dataDTOS.get(i);
            builder.append(sgbdInterface.buildDataColumn(dataDTO));
            if (i + 1 != dataDTOS.size()) {
                builder.append(", ");
            }
        }
    }

    private void buildReferenceColumns(StringBuilder builder, List<EntityReferenceDTO> referenceDTOS) {
        for (int i = 0; i < referenceDTOS.size(); i++) {
            EntityReferenceDTO referenceDTO = referenceDTOS.get(i);
            builder.append(sgbdInterface.buildReferenceColumn(referenceDTO));
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
