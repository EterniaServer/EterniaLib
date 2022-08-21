package br.com.eterniaserver.eternialib.database;

import br.com.eterniaserver.eternialib.database.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseInterface {

    Connection getConnection() throws SQLException;

    void closeAllConnections();

    <T> List<T> listAll(Class<T> objectClass);

    <T> T get(Class<T> objectClass, Object primaryKey);

    <T> void insert(Class<T> objectClass, Object instance);

    <T> void update(Class<T> objectClass, Object instance);

    void register(Class<?> entityClass, Entity<?> entity) throws DatabaseException;

}
