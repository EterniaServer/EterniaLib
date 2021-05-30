package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;

import br.com.eterniaserver.eternialib.core.queries.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestSQLUtils {

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
        final CreateTable createTable = new CreateTable(TABLE_TEST);
        createTable.columns.set("test varchar(16)");
        SQL.execute(createTable);

        final Insert insert = new Insert(TABLE_TEST);
        insert.columns.set("test");
        insert.values.set("result_test");
        SQL.execute(insert);

        Assertions.assertDoesNotThrow(() -> selectQuery("result_test"));

        final Update update = new Update(TABLE_TEST);
        update.where.set("test", "result_test");
        update.set.set("test", "test_result");
        SQL.execute(update);

        Assertions.assertDoesNotThrow(() -> selectQuery("test_result"));

        final Delete delete = new Delete(TABLE_TEST);
        delete.where.set("test", "test_result");
        SQL.executeAsync(delete);

        Assertions.assertThrows(SQLException.class, () -> selectQuery("test_result"));
    }

    private void selectQuery(String value) throws SQLException {
        final Select select = new Select(TABLE_TEST);

        try (final Connection con = SQL.getConnection();
             final PreparedStatement preStat = con.prepareStatement(select.queryString());
             final ResultSet resSet = preStat.executeQuery()) {
            Assertions.assertEquals(value, resSet.getString("test"));
        }
    }

}
