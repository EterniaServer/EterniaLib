package br.com.eterniaserver.eternialib.database;

import br.com.eterniaserver.eternialib.database.dtos.SearchField;
import br.com.eterniaserver.eternialib.database.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface Database {

    Connection getConnection() throws SQLException;

    void closeAllConnections();

    void addTableName(String key, String value);

    <T> T findBy(Class<T> objectClass, String fieldName, Object value);

    <T> T findBy(Class<T> objectClass, SearchField... searchFields);

    <T> List<T> findLike(Class<T> objectClass, String fieldName, Object value);

    <T> List<T> findAllBy(Class<T> objectClass, String fieldName, Object value);

    <T> List<T> findAllBy(Class<T> objectClass, SearchField... searchFields);

    <T> List<T> listAll(Class<T> objectClass);

    <T> List<T> getAllInPrimaryList(Class<T> objectClass, List<Object> values);

    <T> T get(Class<T> objectClass, Object primaryKey);

    <T> boolean save(Class<T> objectClass, T instance);

    <T> void insert(Class<T> objectClass, T instance);

    <T> void delete(Class<T> objectClass, Object primaryKey);

    <T> void update(Class<T> objectClass, T instance);

    <T> void register(Class<T> entityClass, Entity<T> entity) throws DatabaseException;

}
