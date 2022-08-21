package br.com.eterniaserver.eternialib.database.impl;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.database.DatabaseInterface;
import br.com.eterniaserver.eternialib.database.SGBDInterface;
import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityReferenceDTO;
import br.com.eterniaserver.eternialib.database.Entity;
import br.com.eterniaserver.eternialib.database.enums.DatabaseType;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import br.com.eterniaserver.eternialib.database.exceptions.DatabaseException;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseImpl implements DatabaseInterface {

    private final HikariDataSource dataSource;
    private final SGBDInterface sgbdInterface;
    private final Map<Class<?>, Entity<?>> entityMap = new ConcurrentHashMap<>();

    public DatabaseImpl(EterniaLib plugin) throws DatabaseException {
        String databaseType = plugin.getString(Strings.DATABASE_TYPE);
        DatabaseType type = DatabaseType.valueOf(databaseType);
        this.sgbdInterface = SGBDFactory(type);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(
                "jdbc:" + sgbdInterface.jdbcStr() + "://" +
                plugin.getString(Strings.DATABASE_HOST) +
                ":" + plugin.getString(Strings.DATABASE_PORT) +
                "/" + plugin.getString(Strings.DATABASE_DATABASE)
        );
        hikariConfig.setUsername(plugin.getString(Strings.DATABASE_USER));
        hikariConfig.setPassword(plugin.getString(Strings.DATABASE_PASSWORD));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("minimumIdle", plugin.getInteger(Integers.DATABASE_POOL_SIZE));
        hikariConfig.addDataSourceProperty("maximumPoolSize",  plugin.getInteger(Integers.DATABASE_POOL_SIZE));

        this.dataSource = new HikariDataSource(hikariConfig);
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
    public <T> List<T> listAll(Class<T> objectClass) {
        return null;
    }

    @Override
    public <T> T get(Class<T> objectClass, Object primaryKey) {
        Entity<?> entity = entityMap.get(objectClass);
        EntityPrimaryKeyDTO primaryKeyDTO = entity.getPrimaryKey();

        String query = sgbdInterface.selectByPrimary(entity.tableName(), primaryKeyDTO, primaryKey);
        T instance = null;
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()
        ) {
            instance = objectClass.getConstructor().newInstance();

            if (resultSet.next()) {
                populateObject(entity, instance, resultSet);
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

        return instance;
    }

    @Override
    public <T> void insert(Class<T> objectClass, Object instance) {
        Entity<?> entity = entityMap.get(objectClass);
        EntityPrimaryKeyDTO primaryKeyDTO = entity.getPrimaryKey();
        Field primaryField = primaryKeyDTO.field();

        Object primaryValue;
        try {
            primaryValue = primaryField.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (primaryValue == null) {
            insertAndGetKey(entity, instance);
        }
        else {
            onlyInsert(objectClass, instance);
        }
    }

    private <T> void insertAndGetKey(Entity<?> entity, Object instance) {
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
            for (int i = 1; i <= entityDataDTOS.size(); i++) {
                EntityDataDTO entityDataDTO = entityDataDTOS.get(i - 1);
                FieldType type = entityDataDTO.type();
                Field dataField = entityDataDTO.field();
                setValueInStatement(type, i, dataField.get(instance), insertStatement);
            }

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
        }
        catch (SQLException exception) {
            // TODO alert SQL Exception
        }
        catch (IllegalAccessException exception) {
            // TODO alert Class Exception
        }
    }

    private <T> void onlyInsert(Class<T> objectClass, Object instance) {

    }

    @Override
    public <T> void update(Class<T> objectClass, Object instance) {

    }

    @Override
    public void register(Class<?> entityClass, Entity<?> entity) throws DatabaseException {
        EntityPrimaryKeyDTO primaryKeyDTO = entity.getPrimaryKey();
        List<EntityDataDTO> dataDTOS = entity.getDataColumns();
        List<EntityReferenceDTO> referenceDTOS = entity.getReferenceColumns();

        StringBuilder builder = new StringBuilder();
        buildCreateTable(builder, entity);
        buildPrimaryKeyColumn(builder, primaryKeyDTO);
        if (dataDTOS.size() > 0) {
            builder.append(", ");
            buildDataColumns(builder, dataDTOS);
        }
        if (referenceDTOS.size() > 0) {
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
            case STRING, TEXT -> field.set(instance, resultSet.getString(columnName));
            case INTEGER -> field.set(instance, resultSet.getInt(columnName));
            case DOUBLE -> field.set(instance, resultSet.getDouble(columnName));
            case DATE, DATETIME -> field.set(instance, resultSet.getDate(columnName));
            case DECIMAL -> field.set(instance, resultSet.getBigDecimal(columnName));
            default -> field.set(instance, resultSet.getObject(columnName));
        }
    }

    private void setValueInStatement(
            FieldType type,
            int position,
            Object value,
            PreparedStatement statement
    ) throws SQLException {
        switch (type) {
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

    private SGBDInterface SGBDFactory(DatabaseType type) throws DatabaseException {
        switch (type) {
            case MYSQL -> {
                return new MySQLSGBD();
            }
            case MARIADB -> {
                return new MariaDBSGBD();
            }
            default -> throw new DatabaseException("SGBD not implemented");
        }
    }
}
