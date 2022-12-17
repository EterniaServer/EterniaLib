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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

        public HikariConnection(EterniaLib plugin) throws DatabaseException, ClassNotFoundException {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setPoolName("EterniaLib HikariPool");

            String databaseType = plugin.getString(Strings.DATABASE_TYPE);
            DatabaseType type = DatabaseType.valueOf(databaseType);
            this.sgbdInterface = sgbdFactory(type);

            if (type == DatabaseType.SQLITE) {
                hikariConfig.setJdbcUrl(
                        "jdbc:" + sgbdInterface.jdbcStr() +
                        plugin.getString(Strings.DATABASE_HOST)
                );
                Class.forName("org.sqlite.JDBC");
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

        private SGBDInterface sgbdFactory(DatabaseType type) throws DatabaseException {
            switch (type) {
                case MYSQL -> {
                    return new MySQLSGBD();
                }
                case MARIADB -> {
                    return new MariaDBSGBD();
                }
                case SQLITE -> {
                    return new SQLiteSGBD();
                }
                default -> throw new DatabaseException("SGBD not implemented");
            }
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
        Entity<?> entity = entityMap.get(objectClass);

        String query = sgbdInterface.selectAll(entity.tableName());
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()
        ) {
            Field primaryKey = entity.getPrimaryKey().field();

            while (resultSet.next()) {
                T instance = objectClass.getConstructor().newInstance();
                populateObject(entity, instance, resultSet);
                entity.addEntity(primaryKey.get(instance), instance);
                entities.add(instance);
            }
        }
        catch (SQLException exception) {
            // TODO alert SQL Exception
        }
        catch (
                InvocationTargetException |
                InstantiationException |
                IllegalAccessException |
                NoSuchMethodException e
        ) {
            // TODO alert Class Exception
        }

        return entities;
    }

    @Override
    public <T> T get(Class<T> objectClass, Object primaryKey) {
        Entity<?> entity = entityMap.get(objectClass);
        Object object = entity.getEntity(primaryKey);
        if (object != null) {
            return objectClass.cast(object);
        }

        EntityPrimaryKeyDTO primaryKeyDTO = entity.getPrimaryKey();
        String query = sgbdInterface.selectByPrimary(entity.tableName(), primaryKeyDTO);

        T instance = null;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            setValueInStatement(primaryKeyDTO.type(), 1, primaryKey, statement);
            ResultSet resultSet = statement.executeQuery();

            instance = objectClass.getConstructor().newInstance();
            if (resultSet.next()) {
                populateObject(entity, instance, resultSet);
            }

            resultSet.close();

        }
        catch (SQLException exception) {
            // TODO alert SQL Exception
        }
        catch (
                InvocationTargetException |
                InstantiationException |
                IllegalAccessException |
                NoSuchMethodException e
        ) {
            // TODO alert Class Exception
        }

        entity.addEntity(primaryKey, instance);

        return instance;
    }

    @Override
    public <T> void insert(Class<T> objectClass, Object instance) {
        Entity<?> entity = entityMap.get(objectClass);
        EntityPrimaryKeyDTO primaryKeyDTO = entity.getPrimaryKey();
        Field primaryField = primaryKeyDTO.field();

        Object primaryValue = null;
        try {
            primaryValue = primaryField.get(instance);
        } catch (IllegalAccessException e) {
            // TODO alert;
        }

        if (primaryValue == null) {
            insertAndGetKey(entity, instance);
        }
        else {
            onlyInsert(entity, instance);
        }
    }

    private void insertAndGetKey(Entity<?> entity, Object instance) {
        String tableName = entity.tableName();
        EntityPrimaryKeyDTO primaryKeyDTO = entity.getPrimaryKey();
        List<EntityDataDTO> entityDataDTOS = entity.getDataColumns();

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
                FieldType type = primaryKeyDTO.type();
                Field field = primaryKeyDTO.field();
                String columnName = resultSet.getMetaData().getColumnName(1);
                setValueInField(type, field, instance, resultSet, columnName);
            }

            connection.commit();
            connection.setAutoCommit(true);

            Object primaryKey = primaryKeyDTO.field().get(instance);
            entity.addEntity(primaryKey, instance);
        }
        catch (SQLException exception) {
            // TODO alert SQL Exception
        }
        catch (IllegalAccessException exception) {
            // TODO alert Class Exception
        }
    }

    private void onlyInsert(Entity<?> entity, Object instance) {
        String tableName = entity.tableName();
        EntityPrimaryKeyDTO primaryKeyDTO = entity.getPrimaryKey();
        List<EntityDataDTO> entityDataDTOS = entity.getDataColumns();

        String insertQuery = sgbdInterface.insert(tableName, entityDataDTOS, primaryKeyDTO);

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(insertQuery)
        ) {
            FieldType primaryType = primaryKeyDTO.type();
            Field primaryField = primaryKeyDTO.field();
            Object primaryKey = primaryField.get(instance);

            setValueInStatement(primaryType, 1, primaryKey, statement);
            fillStatement(statement, entityDataDTOS, instance, 2);

            statement.execute();

            entity.addEntity(primaryKey, instance);
        }
        catch (SQLException exception) {
            // TODO alert SQL Exception
        }
        catch (IllegalAccessException exception) {
            // TODO alert Class Exception
        }
    }

    @Override
    public <T> void update(Class<T> objectClass, Object instance) {
        Entity<?> entity = entityMap.get(objectClass);
        EntityPrimaryKeyDTO primaryKeyDTO = entity.getPrimaryKey();
        Field primaryField = primaryKeyDTO.field();

        Object primaryValue = null;
        try {
            primaryValue = primaryField.get(instance);
            if (primaryValue == null) {
                throw new DatabaseException("Primary key is null");
            }
        }
        catch (IllegalAccessException e) {
            // TODO alert IllegalAccessException
        }
        catch (DatabaseException e) {
            // TODO alert DatAbaseException
        }

        if (primaryValue == null) {
            return;
        }

        String tableName = entity.tableName();
        List<EntityDataDTO> entityDataDTOS = entity.getDataColumns();
        String updateQuery = sgbdInterface.update(tableName, entityDataDTOS, primaryKeyDTO);
        try (
                Connection connection = getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery)
        ) {
            FieldType primaryType = primaryKeyDTO.type();

            fillStatement(updateStatement, entityDataDTOS, instance, 1);
            setValueInStatement(primaryType, (entityDataDTOS.size() + 1), primaryValue, updateStatement);

            updateStatement.execute();

            entity.addEntity(primaryValue, instance);
        }
        catch (SQLException exception) {
            // TODO alert SQL Exception
        }
        catch (IllegalAccessException exception) {
            // TODO alert Class Exception
        }
    }

    @Override
    public void delete(Class<?> objectClass, Object primaryKey) {
        Entity<?> entity = entityMap.get(objectClass);
        EntityPrimaryKeyDTO primaryKeyDTO = entity.getPrimaryKey();

        String query = sgbdInterface.delete(entity.tableName(), primaryKeyDTO);
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            setValueInStatement(primaryKeyDTO.type(), 1, primaryKey, statement);
            statement.execute();
            entity.removeEntity(primaryKey);

        }
        catch (SQLException exception) {
            // TODO alert SQL Exception
        }
    }

    @Override
    public void register(Class<?> entityClass, Entity<?> entity) throws DatabaseException {
        EntityPrimaryKeyDTO primaryKeyDTO = entity.getPrimaryKey();
        List<EntityDataDTO> dataDTOS = entity.getDataColumns();
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
        } catch (SQLException exception) {
            throw new DatabaseException("Error when creating " + entity.tableName() + " table.");
        }

        entityMap.put(entityClass, entity);
    }

    private <T> void populateObject(Entity<?> entity, T instance, ResultSet resultSet) throws SQLException, IllegalAccessException {
        EntityPrimaryKeyDTO primaryKeyDTO = entity.getPrimaryKey();
        Field primaryField = primaryKeyDTO.field();
        FieldType primaryType = primaryKeyDTO.type();
        String primaryName = primaryKeyDTO.columnName();

        setValueInField(primaryType, primaryField, instance, resultSet, primaryName);

        for (EntityDataDTO dataDTO : entity.getDataColumns()) {
            String columnName = dataDTO.columnName();
            Field field = dataDTO.field();
            FieldType type = dataDTO.type();

            setValueInField(type, field, instance, resultSet, columnName);
        }
    }

    private <T> void setValueInField(
            FieldType type,
            Field field,
            T instance,
            ResultSet resultSet,
            String columnName
    ) throws SQLException, IllegalAccessException {
        switch (type) {
            case UUID -> field.set(instance, UUID.fromString(resultSet.getString(columnName)));
            case STRING, TEXT -> field.set(instance, resultSet.getString(columnName));
            case INTEGER -> field.set(instance, resultSet.getInt(columnName));
            case DOUBLE -> field.set(instance, resultSet.getDouble(columnName));
            case DATE, DATETIME -> field.set(instance, resultSet.getDate(columnName));
            case DECIMAL -> field.set(instance, resultSet.getBigDecimal(columnName));
            default -> field.set(instance, resultSet.getObject(columnName));
        }
    }

    private void fillStatement(PreparedStatement preparedStatement,
                               List<EntityDataDTO> entityDataDTOS,
                               Object instance,
                               int startIndex) throws IllegalAccessException, SQLException {
        for (int i = startIndex; i < entityDataDTOS.size() + startIndex; i++) {
            EntityDataDTO entityDataDTO = entityDataDTOS.get(i - startIndex);
            FieldType type = entityDataDTO.type();
            Field dataField = entityDataDTO.field();
            setValueInStatement(type, i, dataField.get(instance), preparedStatement);
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
            case DATE, DATETIME -> statement.setDate(position, (Date) value);
            case DECIMAL -> statement.setBigDecimal(position, (BigDecimal) value);
            default -> statement.setObject(position, value);
        }
    }

    private void buildCreateTable(StringBuilder builder, Entity<?> entity) {
        builder.append("CREATE TABLE IF NOT EXISTS ").append(entity.tableName()).append(" (");
    }

    private void buildPrimaryKeyColumn(StringBuilder builder, EntityPrimaryKeyDTO primaryKeyDTO) {
        builder.append(sgbdInterface.buildPrimaryColumn(primaryKeyDTO));
    }

    private void buildDataColumns(StringBuilder builder, List<EntityDataDTO> dataDTOS) {
        for (int i = 0; i < dataDTOS.size(); i++) {
            EntityDataDTO dataDTO = dataDTOS.get(i);
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
}
