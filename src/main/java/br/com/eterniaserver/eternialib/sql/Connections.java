package br.com.eterniaserver.eternialib.sql;

import java.sql.SQLException;
import java.sql.DriverManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.io.File;

import br.com.eterniaserver.eternialib.EterniaLib;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;

public class Connections
{
    boolean mysql;
    private HikariDataSource hikari;
    public static Connection connection;
    private final FileConfiguration file;

    public Connections() throws IOException, InvalidConfigurationException {
        this.file = new YamlConfiguration();
        final File files = new File(EterniaLib.getPlugin().getDataFolder(), "configs.yml");
        if (!files.exists()) {
            EterniaLib.getPlugin().saveResource("configs.yml", false);
        }
        this.file.load(files);
        this.Connect();
    }

    public void Connect() {
        this.mysql = this.file.getBoolean("sql.mysql");
        EterniaLib.mysql = this.mysql;
        if (this.mysql) {
            final HikariConfig config = new HikariConfig();
            config.setPoolName("EterniaServer MySQL Pool");
            config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            config.addDataSourceProperty("serverName", this.file.getString("sql.host"));
            config.addDataSourceProperty("port", this.file.getString("sql.port"));
            config.addDataSourceProperty("databaseName", this.file.getString("sql.database"));
            config.addDataSourceProperty("user", this.file.getString("sql.user"));
            config.addDataSourceProperty("password", this.file.getString("sql.password"));
            config.setConnectionTestQuery("SELECT 1");
            config.setMaxLifetime(60000L);
            config.setIdleTimeout(45000L);
            config.setMaximumPoolSize(20);
            this.hikari = new HikariDataSource(config);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', this.file.getString("messages.mysql-ok")));
        }
        else {
            final File dataFolder = new File(EterniaLib.getPlugin().getDataFolder(), "eternia.db");
            if (!dataFolder.exists()) {
                try {
                    dataFolder.createNewFile();
                }
                catch (IOException ignored) {}
            }
            try {
                Class.forName("org.sqlite.JDBC");
                Connections.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', this.file.getString("messages.sql-ok")));
            }
            catch (SQLException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public boolean isClosed() {
        return this.hikari.isClosed();
    }

    public void Close() {
        if (this.mysql) {
            this.hikari.close();
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', this.file.getString("messages.mysql-finish")));
        }
        else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', this.file.getString("messages.sql-finish")));
        }
    }

    public void executeSQLQuery(final SQLCallback callback) {
        this.executeSQLQuery(callback, false);
    }

    public void executeSQLQuery(final SQLCallback callback, final boolean async) {
        final SQLTask task = new SQLTask(this, callback);
        if (async) {
            task.executeAsync();
        }
        else {
            task.run();
        }
    }

    public Connection getConnection() throws SQLException {
        return (this.hikari != null) ? this.hikari.getConnection() : null;
    }
}