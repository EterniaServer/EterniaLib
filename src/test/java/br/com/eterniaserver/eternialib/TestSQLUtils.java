package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;

import br.com.eterniaserver.eternialib.core.queries.CreateTable;
import br.com.eterniaserver.eternialib.core.queries.Delete;
import br.com.eterniaserver.eternialib.core.queries.Insert;
import br.com.eterniaserver.eternialib.core.queries.Select;
import br.com.eterniaserver.eternialib.core.queries.Update;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;

import java.sql.SQLException;

class TestSQLUtils {

    private final static String TABLE_TEST = "table_test";

    @BeforeAll
    public static void setUp() {
         MockBukkit.mock();
         MockBukkit.load(EterniaLib.class);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test the SQL connection")
    void testConnection() {
        Assertions.assertDoesNotThrow(SQL::getConnection);
    }

    @Test
    @DisplayName("Test the SQL queries")
    void testQueries() {
        final var createTable = new CreateTable(TABLE_TEST);
        createTable.columns.set("test varchar(16)");
        SQL.execute(createTable);

        final var insert = new Insert(TABLE_TEST);
        insert.columns.set("test");
        insert.values.set("result_test");
        SQL.execute(insert);

        Assertions.assertDoesNotThrow(() -> selectQuery("result_test"));

        final var update = new Update(TABLE_TEST);
        update.where.set("test", "result_test");
        update.set.set("test", "test_result");
        SQL.execute(update);

        Assertions.assertDoesNotThrow(() -> selectQuery("test_result"));

        final var delete = new Delete(TABLE_TEST);
        delete.where.set("test", "test_result");
        SQL.executeAsync(delete);

        Assertions.assertThrows(SQLException.class, () -> selectQuery("test_result"));
    }

    private void selectQuery(String value) throws SQLException {
        final var select = new Select(TABLE_TEST);

        try (final var con = SQL.getConnection();
             final var preStat = con.prepareStatement(select.queryString());
             final var resSet = preStat.executeQuery()) {
            Assertions.assertEquals(value, resSet.getString("test"));
        }
    }

}
