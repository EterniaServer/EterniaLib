package br.com.eterniaserver.eternialib.database.impl.sgbds;

import br.com.eterniaserver.eternialib.database.Entity;
import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.exceptions.EntityException;
import br.com.eterniaserver.eternialib.utils.EmptyTable;
import br.com.eterniaserver.eternialib.utils.Person;
import br.com.eterniaserver.eternialib.utils.PersonLink;
import br.com.eterniaserver.eternialib.utils.PersonNotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class TestSQLiteSGBD  {

    private static SQLiteSGBD sqLiteSGBD;
    private static Entity<Person> personEntity;
    private static Entity<PersonLink> personLinkEntity;
    private static Entity<EmptyTable> emptyTableEntity;
    private static Entity<PersonNotNull> personNotNullEntity;

    @BeforeAll
    public static void init() throws EntityException, NoSuchMethodException, IllegalAccessException {
        sqLiteSGBD = new SQLiteSGBD();
        personEntity = new Entity<>(Person.class);
        personLinkEntity = new Entity<>(PersonLink.class);
        emptyTableEntity = new Entity<>(EmptyTable.class);
        personNotNullEntity = new Entity<>(PersonNotNull.class);
    }

    @Test
    void testJdbcStr() {
        String result = sqLiteSGBD.jdbcStr();

        Assertions.assertNull(result);
    }

    @Test
    void testValidJdbcStr() {
        String result = sqLiteSGBD.jdbcStr("eternia");

        Assertions.assertEquals("jdbc:sqlite:eternia", result);
    }

    @Test
    void testSelectLike() {
        String tableName = personEntity.tableName();
        EntityDataDTO<Person> entityDataDTO = personEntity.getEntityDataDTOList().get(0);

        String expected = "SELECT * FROM eternia_person WHERE firstName LIKE ?;";
        String result = sqLiteSGBD.selectLike(tableName, entityDataDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSelectByQuery() {
        String tableName = personEntity.tableName();
        List<EntityDataDTO<?>> entityDataDTOs = new ArrayList<>();
        EntityDataDTO<Person> entityDataDTO = personEntity.getEntityDataDTOList().get(0);
        entityDataDTOs.add(entityDataDTO);

        String expected = "SELECT * FROM eternia_person WHERE firstName = ?;";
        String result = sqLiteSGBD.selectBy(tableName, entityDataDTOs);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSelectByMultipleQuery() {
        String tableName = personEntity.tableName();
        List<EntityDataDTO<?>> entityDataDTOs = new ArrayList<>();
        EntityDataDTO<Person> firstName = personEntity.getEntityDataDTOList().get(0);
        EntityDataDTO<Person> birthdate = personEntity.getEntityDataDTOList().get(1);
        entityDataDTOs.add(firstName);
        entityDataDTOs.add(birthdate);

        String expected = "SELECT * FROM eternia_person WHERE firstName = ? AND birthdate = ?;";
        String result = sqLiteSGBD.selectBy(tableName, entityDataDTOs);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSelectAllQuery() {
        String tableName = personEntity.tableName();

        String expected = "SELECT * FROM eternia_person;";
        String result = sqLiteSGBD.selectAll(tableName);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSelectByPrimaryIn() {
        EntityPrimaryKeyDTO<Person> primaryKeyDTO = personEntity.getEntityPrimaryKeyDTO();

        String expected = "SELECT * FROM eternia_person WHERE id IN (?);";
        String result = sqLiteSGBD.selectByPrimaryInList(personEntity.tableName(), primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testSelectByPrimary() {
        EntityPrimaryKeyDTO<Person> primaryKeyDTO = personEntity.getEntityPrimaryKeyDTO();

        String expected = "SELECT * FROM eternia_person WHERE id = ?;";
        String result = sqLiteSGBD.selectByPrimary(personEntity.tableName(), primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testUpdateQuery() {
        String tableName = personEntity.tableName();

        EntityPrimaryKeyDTO<Person> primaryKeyDTO = personEntity.getEntityPrimaryKeyDTO();
        List<EntityDataDTO<Person>> entityDataDTOS = personEntity.getEntityDataDTOList();

        String expected = "UPDATE eternia_person SET firstName = ?, birthdate = ? WHERE id = ?;";
        String result = sqLiteSGBD.update(tableName, entityDataDTOS, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testInsertQuery() {
        String tableName = personEntity.tableName();

        EntityPrimaryKeyDTO<Person> primaryKeyDTO = personEntity.getEntityPrimaryKeyDTO();
        List<EntityDataDTO<Person>> entityDataDTOS = personEntity.getEntityDataDTOList();

        String expected = "INSERT INTO eternia_person (id, firstName, birthdate) VALUES (?, ?, ?);";
        String result = sqLiteSGBD.insert(tableName, entityDataDTOS, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildReferenceColumn() {
        String expected = "FOREIGN KEY (firstPersonId) REFERENCES eternia_person (id) ON DELETE CASCADE";
        String result = sqLiteSGBD.buildReferenceColumn(personLinkEntity.getReferenceColumns().get(0));

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testInsertQueryDatelessEntity() {
        String tableName = emptyTableEntity.tableName();

        EntityPrimaryKeyDTO<EmptyTable> primaryKeyDTO = emptyTableEntity.getEntityPrimaryKeyDTO();
        List<EntityDataDTO<EmptyTable>> entityDataDTOS = emptyTableEntity.getEntityDataDTOList();

        String expected = "INSERT INTO eternia_empty_table (id) VALUES (?);";
        String result = sqLiteSGBD.insert(tableName, entityDataDTOS, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testDeleteQuery() {
        String tableName = personEntity.tableName();
        EntityPrimaryKeyDTO<Person> primaryKeyDTO = personEntity.getEntityPrimaryKeyDTO();

        String expected = "DELETE FROM eternia_person WHERE id = ?;";
        String result = sqLiteSGBD.delete(tableName, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testInsertWithoutKeyQuery() {
        String tableName = personEntity.tableName();

        List<EntityDataDTO<Person>> entityDataDTOS = personEntity.getEntityDataDTOList();

        String expected = "INSERT INTO eternia_person (firstName, birthdate) VALUES (?, ?);";
        String result = sqLiteSGBD.insertWithoutKey(tableName, entityDataDTOS);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetLastInsertId() {
        String tableName = personEntity.tableName();

        String expected = "SELECT LAST_INSERT_ROWID();";
        String result = sqLiteSGBD.getLastInsertId(tableName);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildPrimaryColumn() {
        EntityPrimaryKeyDTO<Person> primaryKeyDTO = personEntity.getEntityPrimaryKeyDTO();

        String expected = "id BIGINT AUTO_INCREMENT PRIMARY KEY";
        String result = sqLiteSGBD.buildPrimaryColumn(primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildPrimaryColumnWithoutAutoIncrement() {
        EntityPrimaryKeyDTO<PersonNotNull> primaryKeyDTO = personNotNullEntity.getEntityPrimaryKeyDTO();

        String expected = "id BIGINT PRIMARY KEY NOT NULL";
        String result = sqLiteSGBD.buildPrimaryColumn(primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildDataColumn() {
        EntityDataDTO<Person> dataDTO = personEntity.getEntityDataDTOList().get(0);

        String expected = "firstName VARCHAR(256)";
        String result = sqLiteSGBD.buildDataColumn(dataDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildDataColumnNotNull() {
        EntityDataDTO<PersonNotNull> dataDTO = personNotNullEntity.getEntityDataDTOList().get(0);

        String expected = "firstName VARCHAR(256) NOT NULL";
        String result = sqLiteSGBD.buildDataColumn(dataDTO);

        Assertions.assertEquals(expected, result);
    }

}
