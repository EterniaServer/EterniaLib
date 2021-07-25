package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;

import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.queries.CreateTable;
import br.com.eterniaserver.eternialib.core.queries.Delete;
import br.com.eterniaserver.eternialib.core.queries.Insert;
import br.com.eterniaserver.eternialib.core.queries.Select;
import br.com.eterniaserver.eternialib.core.queries.Update;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class TestSQLiteUtils {

    private final static String TABLE_TEST = "table_test";

    private static EterniaLib plugin;

    @BeforeAll
    public static void setUp() throws IOException {
        MockBukkit.mock();
        final FileConfiguration file = YamlConfiguration.loadConfiguration(new File(Constants.CONFIG_FILE_PATH));
        file.set("sql.mysql", false);
        file.set("lobby.enabled", false);
        file.save(Constants.CONFIG_FILE_PATH);

        plugin = MockBukkit.load(EterniaLib.class);
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

    @Test
    @DisplayName("Test method")
    void testSQLite() {
        Assertions.assertFalse(plugin.getBool(Booleans.MYSQL));
        Assertions.assertFalse(EterniaLib.getMySQL());
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
