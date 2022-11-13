package br.com.eterniaserver.eternialib.database;

import be.seeseemelk.mockbukkit.MockBukkit;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.database.exceptions.DatabaseException;
import br.com.eterniaserver.eternialib.database.exceptions.EntityException;
import br.com.eterniaserver.eternialib.database.impl.SGBDInterface;
import br.com.eterniaserver.eternialib.database.impl.SQLDatabase;
import br.com.eterniaserver.eternialib.utils.entities.Person;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class TestSQLDatabase {

    private static SQLDatabase database;
    private static Entity<Person> personEntity;

    private static SGBDInterface sgbdInterface;
    private static HikariDataSource dataSource;

    @BeforeAll
    public static void loadAndTestRegisterEntity() throws EntityException, DatabaseException, SQLException {
        MockBukkit.mock();
        MockBukkit.load(EterniaLib.class);
        personEntity = new Entity<>(Person.class);

        sgbdInterface = Mockito.mock(SGBDInterface.class);
        dataSource = Mockito.mock(HikariDataSource.class);

        database = new SQLDatabase(dataSource, sgbdInterface);

        Connection connection = Mockito.mock(Connection.class);
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);

        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);

        database.register(Person.class, personEntity);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    void testGetConnection() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);

        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        Assertions.assertNotNull(database.getConnection());
    }

    @Test
    void testCloseAllConnectionsWithConnectionOpened() {
        Mockito.when(dataSource.isClosed()).thenReturn(false);

        database.closeAllConnections();

        Mockito.verify(dataSource, Mockito.times(1)).close();
    }

    @Test
    void testCloseAllConnectionsWithConnectionClosed() {
        Mockito.when(dataSource.isClosed()).thenReturn(true);

        database.closeAllConnections();

        Mockito.verify(dataSource, Mockito.times(0)).close();
    }

    @Test
    void testListAllPerson() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        String queryAll = "SELECT * FROM eternia_person";
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(sgbdInterface.selectAll(personEntity.tableName())).thenReturn(queryAll);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(queryAll)).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);

        Mockito.when(resultSet.next()).thenReturn(true, true, false);
        Mockito.when(resultSet.getInt("id")).thenReturn(1, 2);
        Mockito.when(resultSet.getString("firstName")).thenReturn("John", "Doe");
        Mockito.when(resultSet.getDate("birthdate")).thenReturn(Date.valueOf("2000-01-01"), Date.valueOf("2000-01-02"));

        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);

        int expectedAmount = 2;
        List<Person> result = database.listAll(Person.class);

        Assertions.assertEquals(expectedAmount, result.size());
    }

    @Test
    void testGetPerson() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        String queryGet = "SELECT * FROM eternia_person WHERE id = 1";
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Integer id = 1;
        String firstName = "John";
        Date birthdate = Date.valueOf("2000-01-01");

        Mockito.when(sgbdInterface.selectByPrimary(
                personEntity.tableName(),
                personEntity.getPrimaryKey(),
                id
        )).thenReturn(queryGet);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(queryGet)).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);

        Mockito.when(resultSet.next()).thenReturn(true, false);
        Mockito.when(resultSet.getInt("id")).thenReturn(id);
        Mockito.when(resultSet.getString("firstName")).thenReturn(firstName);
        Mockito.when(resultSet.getDate("birthdate")).thenReturn(birthdate);

        Person result = database.get(Person.class, id);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(id, result.id);
        Assertions.assertEquals(firstName, result.firstName);
        Assertions.assertEquals(birthdate, result.birthdate);
    }
}
