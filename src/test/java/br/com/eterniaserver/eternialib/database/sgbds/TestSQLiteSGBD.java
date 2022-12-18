package br.com.eterniaserver.eternialib.database.sgbds;

import br.com.eterniaserver.eternialib.database.Entity;
import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.exceptions.EntityException;
import br.com.eterniaserver.eternialib.database.impl.sgbds.SQLiteSGBD;
import br.com.eterniaserver.eternialib.utils.entities.EmptyTable;
import br.com.eterniaserver.eternialib.utils.entities.Person;
import br.com.eterniaserver.eternialib.utils.entities.PersonLink;
import br.com.eterniaserver.eternialib.utils.entities.PersonNotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestSQLiteSGBD  {

    private static SQLiteSGBD sqLiteSGBD;
    private static Entity<Person> personEntity;
    private static Entity<PersonLink> personLinkEntity;
    private static Entity<EmptyTable> emptyTableEntity;
    private static Entity<PersonNotNull> personNotNullEntity;

    @BeforeAll
    public static void init() throws EntityException {
        sqLiteSGBD = new SQLiteSGBD();
        personEntity = new Entity<>(Person.class);
        personLinkEntity = new Entity<>(PersonLink.class);
        emptyTableEntity = new Entity<>(EmptyTable.class);
        personNotNullEntity = new Entity<>(PersonNotNull.class);
    }

    @Test
    void testJdbcStr() {
        String expected = "sqlite:";
        String result = sqLiteSGBD.jdbcStr();

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
    void testSelectByPrimary() {
        EntityPrimaryKeyDTO primaryKeyDTO = personEntity.getPrimaryKey();

        String expected = "SELECT * FROM eternia_person WHERE id = ?;";
        String result = sqLiteSGBD.selectByPrimary(personEntity.tableName(), primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testUpdateQuery() {
        String tableName = personEntity.tableName();

        EntityPrimaryKeyDTO primaryKeyDTO = personEntity.getPrimaryKey();
        List<EntityDataDTO> entityDataDTOS = personEntity.getDataColumns();

        String expected = "UPDATE eternia_person SET firstName = ?, birthdate = ? WHERE id = ?;";
        String result = sqLiteSGBD.update(tableName, entityDataDTOS, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testInsertQuery() {
        String tableName = personEntity.tableName();

        EntityPrimaryKeyDTO primaryKeyDTO = personEntity.getPrimaryKey();
        List<EntityDataDTO> entityDataDTOS = personEntity.getDataColumns();

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

        EntityPrimaryKeyDTO primaryKeyDTO = emptyTableEntity.getPrimaryKey();
        List<EntityDataDTO> entityDataDTOS = emptyTableEntity.getDataColumns();

        String expected = "INSERT INTO eternia_empty_table (id) VALUES (?);";
        String result = sqLiteSGBD.insert(tableName, entityDataDTOS, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testDeleteQuery() {
        String tableName = personEntity.tableName();
        EntityPrimaryKeyDTO primaryKeyDTO = personEntity.getPrimaryKey();

        String expected = "DELETE FROM eternia_person WHERE id = ?;";
        String result = sqLiteSGBD.delete(tableName, primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testInsertWithoutKeyQuery() {
        String tableName = personEntity.tableName();

        List<EntityDataDTO> entityDataDTOS = personEntity.getDataColumns();

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
        EntityPrimaryKeyDTO primaryKeyDTO = personEntity.getPrimaryKey();

        String expected = "id BIGINT AUTO_INCREMENT PRIMARY KEY";
        String result = sqLiteSGBD.buildPrimaryColumn(primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildPrimaryColumnWithoutAutoIncrement() {
        EntityPrimaryKeyDTO primaryKeyDTO = personNotNullEntity.getPrimaryKey();

        String expected = "id BIGINT PRIMARY KEY NOT NULL";
        String result = sqLiteSGBD.buildPrimaryColumn(primaryKeyDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildDataColumn() {
        EntityDataDTO dataDTO = personEntity.getDataColumns().get(0);

        String expected = "firstName VARCHAR(256)";
        String result = sqLiteSGBD.buildDataColumn(dataDTO);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testBuildDataColumnNotNull() {
        EntityDataDTO dataDTO = personNotNullEntity.getDataColumns().get(0);

        String expected = "firstName VARCHAR(256) NOT NULL";
        String result = sqLiteSGBD.buildDataColumn(dataDTO);

        Assertions.assertEquals(expected, result);
    }

}
