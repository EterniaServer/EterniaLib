package br.com.eterniaserver.eternialib.database.impl;

import br.com.eterniaserver.eternialib.database.Entity;
import br.com.eterniaserver.eternialib.database.exceptions.DatabaseException;
import br.com.eterniaserver.eternialib.database.exceptions.EntityException;
import br.com.eterniaserver.eternialib.utils.Company;
import br.com.eterniaserver.eternialib.utils.Person;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

class TestSQLDatabase {

    private static SQLDatabase database;
    private static Entity<Person> personEntity;
    private static SGBDInterface sgbdInterface;
    private static HikariDataSource dataSource;

    @BeforeAll
    public static void loadAndTestRegisterEntity() throws EntityException, DatabaseException, SQLException, NoSuchMethodException, IllegalAccessException {
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

    @Test
    void testGetConnection() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);

        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        Assertions.assertNotNull(database.getConnection());
    }

    @Test
    void testBlockMultiplePrimaryKeys() {
        Assertions.assertThrows(EntityException.class, () -> new Entity<>(Company.class));
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
    void testGetBy() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        String queryBy = "SELECT * FROM eternia_person WHERE firstName = ?";
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(sgbdInterface.selectBy(personEntity.tableName(), personEntity.getDataDTO("firstName"))).thenReturn(queryBy);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(queryBy)).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);

        Mockito.when(resultSet.next()).thenReturn(true, false);
        Mockito.when(resultSet.getInt("id")).thenReturn(1);
        Mockito.when(resultSet.getString("firstName")).thenReturn("Junior John");
        Mockito.when(resultSet.getDate("birthdate")).thenReturn(Date.valueOf("2000-01-01"));

        Person person = database.findBy(Person.class, "firstName", "Junior John");

        Assertions.assertEquals(1, person.getId());
        Assertions.assertEquals("Junior John", person.getFirstName());
        Assertions.assertEquals(Date.valueOf("2000-01-01"), person.getBirthdate());
    }

    @Test
    void testGetLike() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        String queryLike = "SELECT * FROM eternia_person WHERE firstName LIKE ?";
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(sgbdInterface.selectLike(personEntity.tableName(), personEntity.getDataDTO("firstName"))).thenReturn(queryLike);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(queryLike)).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);

        Mockito.when(resultSet.next()).thenReturn(true, true, false);
        Mockito.when(resultSet.getInt("id")).thenReturn(1, 2);
        Mockito.when(resultSet.getString("firstName")).thenReturn("Junior John", "Junior Doe");
        Mockito.when(resultSet.getDate("birthdate")).thenReturn(Date.valueOf("2000-01-01"), Date.valueOf("2000-01-02"));

        Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);

        int expected = 2;
        List<Person> personList = database.findLike(Person.class, "firstName", "Junior");

        Assertions.assertEquals(expected, personList.size());
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
        String queryGet = "SELECT * FROM eternia_person WHERE id = ?";
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Integer id = 1;
        String firstName = "John";
        Date birthdate = Date.valueOf("2000-01-01");

        Mockito.when(sgbdInterface.selectByPrimary(
                personEntity.tableName(),
                personEntity.getEntityPrimaryKeyDTO()
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
        Assertions.assertEquals(id, result.getId());
        Assertions.assertEquals(firstName, result.getFirstName());
        Assertions.assertEquals(birthdate, result.getBirthdate());
    }

    @Test
    void testGetPersonTwoTimes() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        String queryGet = "SELECT * FROM eternia_person WHERE id = ?";
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Integer id = 1;
        String firstName = "John";
        Date birthdate = Date.valueOf("2000-01-01");

        Mockito.when(sgbdInterface.selectByPrimary(
                personEntity.tableName(),
                personEntity.getEntityPrimaryKeyDTO()
        )).thenReturn(queryGet);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(queryGet)).thenReturn(preparedStatement);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);

        Mockito.when(resultSet.next()).thenReturn(true, false);
        Mockito.when(resultSet.getInt("id")).thenReturn(id);
        Mockito.when(resultSet.getString("firstName")).thenReturn(firstName);
        Mockito.when(resultSet.getDate("birthdate")).thenReturn(birthdate);

        database.get(Person.class, id);
        database.get(Person.class, id);

        Mockito.verify(sgbdInterface, Mockito.times(1)).selectByPrimary(
                personEntity.tableName(),
                personEntity.getEntityPrimaryKeyDTO()
        );
    }

    @Test
    void testInsertPersonWithId() throws SQLException {
        Person person = new Person();
        person.setId(1);
        person.setFirstName("John");
        person.setBirthdate(Date.valueOf("2000-01-01"));

        Connection connection = Mockito.mock(Connection.class);
        String insertQuery = "INSERT INTO eternia_person (id, firstName, birthdate) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);

        Mockito.when(sgbdInterface.insert(
                personEntity.tableName(),
                personEntity.getEntityDataDTOList(),
                personEntity.getEntityPrimaryKeyDTO()
        )).thenReturn(insertQuery);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(insertQuery)).thenReturn(preparedStatement);

        database.insert(Person.class, person);

        Mockito.verify(preparedStatement, Mockito.times(1)).execute();
    }

    @Test
    void testSaveWillInsert() throws SQLException {
        Person person = new Person();
        person.setFirstName("John");
        person.setBirthdate(Date.valueOf("2000-01-01"));

        Connection connection = Mockito.mock(Connection.class);
        String insertWithoutKey = "INSERT INTO eternia_person (firstName, birthdate) VALUES (?, ?)";
        String getLastId = "SELECT LAST_INSERT_ID()";
        Integer personId = 1;
        PreparedStatement insertStatement = Mockito.mock(PreparedStatement.class);
        PreparedStatement getStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        ResultSetMetaData resultSetMetaData = Mockito.mock(ResultSetMetaData.class);

        Mockito.when(sgbdInterface.insertWithoutKey(
                personEntity.tableName(),
                personEntity.getEntityDataDTOList()
        )).thenReturn(insertWithoutKey);
        Mockito.when(sgbdInterface.getLastInsertId(personEntity.tableName())).thenReturn(getLastId);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(insertWithoutKey)).thenReturn(insertStatement);
        Mockito.when(connection.prepareStatement(getLastId)).thenReturn(getStatement);
        Mockito.when(getStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(resultSetMetaData.getColumnName(1)).thenReturn("id");
        Mockito.when(resultSet.next()).thenReturn(true, false);
        Mockito.when(resultSet.getInt("id")).thenReturn(personId);

        boolean saved = database.save(Person.class, person);

        Mockito.verify(insertStatement, Mockito.times(1)).execute();
        Mockito.verify(getStatement, Mockito.times(1)).executeQuery();

        Assertions.assertEquals(personId, person.getId());
        Assertions.assertTrue(saved);
    }

    @Test
    void testSaveWillUpdate() throws SQLException {
        Person person = new Person();
        person.setId(1);
        person.setFirstName("John");
        person.setBirthdate(Date.valueOf("2000-01-01"));

        Connection connection = Mockito.mock(Connection.class);
        String updateQuery = "UPDATE eternia_person SET firstName = ?, birthdate = ? WHERE id = ?";
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);

        Mockito.when(sgbdInterface.update(
                personEntity.tableName(),
                personEntity.getEntityDataDTOList(),
                personEntity.getEntityPrimaryKeyDTO()
        )).thenReturn(updateQuery);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(updateQuery)).thenReturn(preparedStatement);

        boolean saved = database.save(Person.class, person);

        Mockito.verify(preparedStatement, Mockito.times(1)).execute();
        Assertions.assertFalse(saved);
    }

    @Test
    void testInsertPersonWithoutId() throws SQLException {
        Person person = new Person();
        person.setFirstName("John");
        person.setBirthdate(Date.valueOf("2000-01-01"));

        Connection connection = Mockito.mock(Connection.class);
        String insertWithoutKey = "INSERT INTO eternia_person (firstName, birthdate) VALUES (?, ?)";
        String getLastId = "SELECT LAST_INSERT_ID()";
        Integer personId = 1;
        PreparedStatement insertStatement = Mockito.mock(PreparedStatement.class);
        PreparedStatement getStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        ResultSetMetaData resultSetMetaData = Mockito.mock(ResultSetMetaData.class);

        Mockito.when(sgbdInterface.insertWithoutKey(
                personEntity.tableName(),
                personEntity.getEntityDataDTOList()
        )).thenReturn(insertWithoutKey);
        Mockito.when(sgbdInterface.getLastInsertId(personEntity.tableName())).thenReturn(getLastId);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(insertWithoutKey)).thenReturn(insertStatement);
        Mockito.when(connection.prepareStatement(getLastId)).thenReturn(getStatement);
        Mockito.when(getStatement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(resultSetMetaData.getColumnName(1)).thenReturn("id");
        Mockito.when(resultSet.next()).thenReturn(true, false);
        Mockito.when(resultSet.getInt("id")).thenReturn(personId);

        database.insert(Person.class, person);

        Mockito.verify(insertStatement, Mockito.times(1)).execute();
        Mockito.verify(getStatement, Mockito.times(1)).executeQuery();

        Assertions.assertEquals(personId, person.getId());
    }

    @Test
    void testUpdatePerson() throws SQLException {
        Person person = new Person();
        person.setId(1);
        person.setFirstName("John");
        person.setBirthdate(Date.valueOf("2000-01-01"));

        Connection connection = Mockito.mock(Connection.class);
        String updateQuery = "UPDATE eternia_person SET firstName = ?, birthdate = ? WHERE id = ?";
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);

        Mockito.when(sgbdInterface.update(
                personEntity.tableName(),
                personEntity.getEntityDataDTOList(),
                personEntity.getEntityPrimaryKeyDTO()
        )).thenReturn(updateQuery);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(updateQuery)).thenReturn(preparedStatement);

        database.update(Person.class, person);

        Mockito.verify(preparedStatement, Mockito.times(1)).execute();
    }

    @Test
    void testDeletePerson() throws SQLException {
        Person person = new Person();
        person.setId(1);
        person.setFirstName("John");
        person.setBirthdate(Date.valueOf("2000-01-01"));

        Connection connection = Mockito.mock(Connection.class);
        String deleteQuery = "DELETE FROM eternia_person WHERE id = ?";
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);

        Mockito.when(sgbdInterface.delete(
                personEntity.tableName(),
                personEntity.getEntityPrimaryKeyDTO()
        )).thenReturn(deleteQuery);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(deleteQuery)).thenReturn(preparedStatement);

        database.delete(Person.class, person.getId());

        Mockito.verify(preparedStatement, Mockito.times(1)).execute();
    }
}
