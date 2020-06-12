package br.com.eterniaserver.eternialib.sql;

import br.com.eterniaserver.eternialib.EterniaLib;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Connections {

    boolean mysql;
    private HikariDataSource hikari;
    private final FileConfiguration file = new YamlConfiguration();

    public Connections() throws IOException, InvalidConfigurationException {
        final File files = new File(EterniaLib.getPlugin().getDataFolder(), "configs.yml");
        if (!files.exists()) EterniaLib.getPlugin().saveResource("configs.yml", false);
        file.load(files);
        this.Connect();
    }

    public void Connect() {
        mysql = file.getBoolean("sql.mysql");
        hikari = new HikariDataSource();
        if (mysql) {
            System.out.println("oi");
            hikari.setPoolName("EterniaServer MySQL Pool");
            hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            hikari.addDataSourceProperty("serverName", file.getString("sql.host"));
            hikari.addDataSourceProperty("port", file.getString("sql.port"));
            hikari.addDataSourceProperty("databaseName", file.getString("sql.database"));
            hikari.addDataSourceProperty("user", file.getString("sql.user"));
            hikari.addDataSourceProperty("password", file.getString("sql.password"));
            hikari.setMaximumPoolSize(50);
            Bukkit.getConsoleSender().sendMessage(file.getString("messages.mysql-ok"));
        } else {
            hikari.setPoolName("EterniaServer SQLite Pool");
            hikari.setDriverClassName("org.sqlite.JDBC");
            hikari.setJdbcUrl("jdbc:sqlite:" + new File(EterniaLib.getPlugin().getDataFolder(), "eternia.db"));
            hikari.setMaximumPoolSize(50);
            Bukkit.getConsoleSender().sendMessage(file.getString("messages.sql-ok"));
        }
    }

    public boolean isClosed() {
        return hikari.isClosed();
    }

    public void Close() {
        hikari.close();
        if (mysql) {
            Bukkit.getConsoleSender().sendMessage(file.getString("messages.mysql-finish"));
        } else {
            Bukkit.getConsoleSender().sendMessage(file.getString("messages.sql-finish"));
        }
    }

    public void executeSQLQuery(SQLCallback callback) {
        executeSQLQuery(callback, false);
    }

    public void executeSQLQuery(SQLCallback callback, boolean async) {
        SQLTask task = new SQLTask(this, callback);
        if(async) {
            task.executeAsync();
        } else {
            task.run();
        }
    }

    public Connection getConnection() throws SQLException {
        return hikari != null ? hikari.getConnection() : null;
    }

}
