package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;

import br.com.eterniaserver.eternialib.core.queries.CreateTable;
import br.com.eterniaserver.eternialib.core.queries.Delete;
import br.com.eterniaserver.eternialib.core.queries.Insert;
import br.com.eterniaserver.eternialib.core.queries.Select;
import br.com.eterniaserver.eternialib.core.queries.Update;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class TestMariaDBUtils {

    private final static String TABLE_TEST = "table_test";
    private static DB db;

    @BeforeAll
    public static void setUp() throws IOException, ManagedProcessException, SQLException {
        final DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
        configBuilder.setPort(3789);
        db = DB.newEmbeddedDB(configBuilder.build());
        db.start();

        final Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3789/?user=root");
        final PreparedStatement preparedStatement = connection.prepareStatement("CREATE DATABASE root");
        preparedStatement.execute();

        MockBukkit.mock();
        final FileConfiguration file = YamlConfiguration.loadConfiguration(new File(Constants.CONFIG_FILE_PATH));
        file.set("sql.mysql", true);
        file.set("sql.port", 3789);
        file.set("sql.host", "127.0.0.1");
        file.set("sql.database", "root");
        file.set("sql.user", "root");
        file.set("sql.password", "");
        file.save(Constants.CONFIG_FILE_PATH);

        MockBukkit.load(EterniaLib.class);
    }

    @AfterAll
    public static void tearDown() throws IOException, ManagedProcessException {
        final FileConfiguration file = YamlConfiguration.loadConfiguration(new File(Constants.CONFIG_FILE_PATH));
        file.set("sql.mysql", false);
        file.set("sql.port", 3306);
        file.save(Constants.CONFIG_FILE_PATH);

        MockBukkit.unmock();
        db.stop();
    }

    @Test
    @DisplayName("Test the Mariadb connection")
    void testConnection() {
        Assertions.assertDoesNotThrow(SQL::getConnection);
    }

    @Test
    @DisplayName("Test the Mariadb queries")
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
            resSet.first();
            Assertions.assertEquals(value, resSet.getString("test"));
        }
    }

}
