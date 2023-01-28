package br.com.eterniaserver.eternialib.database.impl.sgbds;

import br.com.eterniaserver.eternialib.database.Entity;
import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.exceptions.EntityException;
import br.com.eterniaserver.eternialib.database.impl.sgbds.MySQLSGBD;
import br.com.eterniaserver.eternialib.utils.entities.EmptyTable;
import br.com.eterniaserver.eternialib.utils.entities.Person;
import br.com.eterniaserver.eternialib.utils.entities.PersonLink;
import br.com.eterniaserver.eternialib.utils.entities.PersonNotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

class TestMySQLSGBD {

    private static MySQLSGBD mySQLSGBD;
    private static Entity<Person> personEntity;
    private static Entity<PersonLink> personLinkEntity;
    private static Entity<EmptyTable> emptyTableEntity;
    private static Entity<PersonNotNull> personNotNullEntity;

    @BeforeAll
    public static void init() throws EntityException, NoSuchMethodException, IllegalAccessException {
        mySQLSGBD = new MySQLSGBD();
        personEntity = new Entity<>(Person.class);
        personLinkEntity = new Entity<>(PersonLink.class);
        emptyTableEntity = new Entity<>(EmptyTable.class);
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
        EntityPrimaryKeyDTO<Person> primaryKeyDTO = personEntity.getEntityPrimaryKeyDTO();

        String expected = "SELECT * FROM eternia_person WHERE id = ?;";
        String result = mySQLSGBD.selectByPrimary(personEntity.tableName(), primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testUpdateQuery() {
        String tableName = personEntity.tableName();

        EntityPrimaryKeyDTO<Person> primaryKeyDTO = personEntity.getEntityPrimaryKeyDTO();
        List<EntityDataDTO<Person>> entityDataDTOS = personEntity.getEntityDataDTOList();

        String expected = "UPDATE eternia_person SET firstName = ?, birthdate = ? WHERE id = ?;";
        String result = mySQLSGBD.update(tableName, entityDataDTOS, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testInsertQuery() {
        String tableName = personEntity.tableName();

        EntityPrimaryKeyDTO<Person> primaryKeyDTO = personEntity.getEntityPrimaryKeyDTO();
        List<EntityDataDTO<Person>> entityDataDTOS = personEntity.getEntityDataDTOList();

        String expected = "INSERT INTO eternia_person (id, firstName, birthdate) VALUES (?, ?, ?);";
        String result = mySQLSGBD.insert(tableName, entityDataDTOS, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildReferenceColumn() {
        String expected = "FOREIGN KEY (firstPersonId) REFERENCES eternia_person (id) ON DELETE CASCADE";
        String result = mySQLSGBD.buildReferenceColumn(personLinkEntity.getReferenceColumns().get(0));

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testInsertQueryDatelessEntity() {
        String tableName = emptyTableEntity.tableName();

        EntityPrimaryKeyDTO<EmptyTable> primaryKeyDTO = emptyTableEntity.getEntityPrimaryKeyDTO();
        List<EntityDataDTO<EmptyTable>> entityDataDTOS = emptyTableEntity.getEntityDataDTOList();

        String expected = "INSERT INTO eternia_empty_table (id) VALUES (?);";
        String result = mySQLSGBD.insert(tableName, entityDataDTOS, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testDeleteQuery() {
        String tableName = personEntity.tableName();
        EntityPrimaryKeyDTO<Person> primaryKeyDTO = personEntity.getEntityPrimaryKeyDTO();

        String expected = "DELETE FROM eternia_person WHERE id = ?;";
        String result = mySQLSGBD.delete(tableName, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testInsertWithoutKeyQuery() {
        String tableName = personEntity.tableName();

        List<EntityDataDTO<Person>> entityDataDTOS = personEntity.getEntityDataDTOList();

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
        EntityPrimaryKeyDTO<Person> primaryKeyDTO = personEntity.getEntityPrimaryKeyDTO();

        String expected = "id BIGINT AUTO_INCREMENT PRIMARY KEY";
        String result = mySQLSGBD.buildPrimaryColumn(primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildPrimaryColumnWithoutAutoIncrement() {
        EntityPrimaryKeyDTO<PersonNotNull> primaryKeyDTO = personNotNullEntity.getEntityPrimaryKeyDTO();

        String expected = "id BIGINT PRIMARY KEY NOT NULL";
        String result = mySQLSGBD.buildPrimaryColumn(primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildDataColumn() {
        EntityDataDTO<Person> dataDTO = personEntity.getEntityDataDTOList().get(0);

        String expected = "firstName VARCHAR(256)";
        String result = mySQLSGBD.buildDataColumn(dataDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildDataColumnNotNull() {
        EntityDataDTO<PersonNotNull> dataDTO = personNotNullEntity.getEntityDataDTOList().get(0);

        String expected = "firstName VARCHAR(256) NOT NULL";
        String result = mySQLSGBD.buildDataColumn(dataDTO);

        Assertions.assertEquals(expected, result);
    }

}
