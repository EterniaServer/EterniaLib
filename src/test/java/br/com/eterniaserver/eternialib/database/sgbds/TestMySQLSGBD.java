package br.com.eterniaserver.eternialib.database.sgbds;

import br.com.eterniaserver.eternialib.database.Entity;
import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.exceptions.EntityException;
import br.com.eterniaserver.eternialib.database.impl.sgbds.MySQLSGBD;
import br.com.eterniaserver.eternialib.utils.entities.Person;
import br.com.eterniaserver.eternialib.utils.entities.PersonNotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestMySQLSGBD {

    private static MySQLSGBD mySQLSGBD;
    private static Entity<Person> personEntity;
    private static Entity<PersonNotNull> personNotNullEntity;

    @BeforeAll
    public static void init() throws EntityException {
        mySQLSGBD = new MySQLSGBD();
        personEntity = new Entity<>(Person.class);
        personNotNullEntity = new Entity<>(PersonNotNull.class);
    }

    @Test
    void testJdbcStr() {
        String expected = "mysql://";
        String result = mySQLSGBD.jdbcStr();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSelectAllQuery() {
        String tableName = personEntity.tableName();

        String expected = "SELECT * FROM eternia_person;";
        String result = mySQLSGBD.selectAll(tableName);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSelectByPrimary() {
        EntityPrimaryKeyDTO primaryKeyDTO = personEntity.getPrimaryKey();
        int primaryKey = 1;

        String expected = "SELECT * FROM eternia_person WHERE id = 1;";
        String result = mySQLSGBD.selectByPrimary(personEntity.tableName(), primaryKeyDTO, primaryKey);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testUpdateQuery() {
        String tableName = personEntity.tableName();

        EntityPrimaryKeyDTO primaryKeyDTO = personEntity.getPrimaryKey();
        List<EntityDataDTO> entityDataDTOS = personEntity.getDataColumns();

        String expected = "UPDATE eternia_person SET firstName = ?, birthdate = ? WHERE id = ?;";
        String result = mySQLSGBD.update(tableName, entityDataDTOS, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testInsertQuery() {
        String tableName = personEntity.tableName();

        EntityPrimaryKeyDTO primaryKeyDTO = personEntity.getPrimaryKey();
        List<EntityDataDTO> entityDataDTOS = personEntity.getDataColumns();

        String expected = "INSERT INTO eternia_person (id, firstName, birthdate) VALUES (?, ?, ?);";
        String result = mySQLSGBD.insert(tableName, entityDataDTOS, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testDeleteQuery() {
        String tableName = personEntity.tableName();

        EntityPrimaryKeyDTO primaryKeyDTO = personEntity.getPrimaryKey();
        int primaryKey = 1;

        String expected = "DELETE FROM eternia_person WHERE id = 1;";
        String result = mySQLSGBD.delete(tableName, primaryKeyDTO, primaryKey);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testInsertWithoutKeyQuery() {
        String tableName = personEntity.tableName();

        List<EntityDataDTO> entityDataDTOS = personEntity.getDataColumns();

        String expected = "INSERT INTO eternia_person (firstName, birthdate) VALUES (?, ?);";
        String result = mySQLSGBD.insertWithoutKey(tableName, entityDataDTOS);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetLastInsertId() {
        String tableName = personEntity.tableName();

        String expected = "SELECT LAST_INSERT_ID();";
        String result = mySQLSGBD.getLastInsertId(tableName);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildPrimaryColumn() {
        EntityPrimaryKeyDTO primaryKeyDTO = personEntity.getPrimaryKey();

        String expected = "id BIGINT AUTO_INCREMENT PRIMARY KEY";
        String result = mySQLSGBD.buildPrimaryColumn(primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildPrimaryColumnWithoutAutoIncrement() {
        EntityPrimaryKeyDTO primaryKeyDTO = personNotNullEntity.getPrimaryKey();

        String expected = "id BIGINT PRIMARY KEY NOT NULL";
        String result = mySQLSGBD.buildPrimaryColumn(primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildDataColumn() {
        EntityDataDTO dataDTO = personEntity.getDataColumns().get(0);

        String expected = "firstName VARCHAR(256)";
        String result = mySQLSGBD.buildDataColumn(dataDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildDataColumnNotNull() {
        EntityDataDTO dataDTO = personNotNullEntity.getDataColumns().get(0);

        String expected = "firstName VARCHAR(256) NOT NULL";
        String result = mySQLSGBD.buildDataColumn(dataDTO);

        Assertions.assertEquals(expected, result);
    }

}
