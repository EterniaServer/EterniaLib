package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.EterniaLib;

import com.zaxxer.hikari.HikariDataSource;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;

import java.sql.SQLException;
import java.sql.DriverManager;
import java.io.IOException;
import java.io.File;
import java.sql.Connection;

public class Connections {

    private final HikariDataSource hikari = new HikariDataSource();
    private final EterniaLib plugin;

    private final String msgLoad;
    private final String msgMySQLFinish;
    private final String msgError;
    private final String msgSQLOk;
    private final String messageSQLFinish;

    private Connection connection;

    public Connections(EterniaLib plugin) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;

        FileConfiguration file = new YamlConfiguration();
        EterniaLib.mysql = file.getBoolean("sql.mysql", false);

        File files = new File(plugin.getDataFolder(), "configs.yml");
        if (!files.exists()) plugin.saveResource("configs.yml", false);
        file.load(files);

        String msgMySQLOk = file.getString("messages.mysql-ok");
        msgSQLOk = file.getString("messages.sql-ok");
        msgMySQLFinish = file.getString("messages.mysql-finish");
        messageSQLFinish = file.getString("messages.sql-finish");
        msgError = file.getString("messages.error");
        msgLoad = file.getString("messages.load");

        if (EterniaLib.mysql) {
            hikari.setPoolName("EterniaServer MySQL Pool");
            hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            hikari.addDataSourceProperty("serverName", file.getString("sql.host"));
            hikari.addDataSourceProperty("port", file.getString("sql.port"));
            hikari.addDataSourceProperty("databaseName", file.getString("sql.database"));
            hikari.addDataSourceProperty("user", file.getString("sql.user"));
            hikari.addDataSourceProperty("password", file.getString("sql.password"));
            hikari.setConnectionTestQuery("SELECT 1");
            hikari.setMaxLifetime(60000L);
            hikari.setIdleTimeout(45000L);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msgMySQLOk));
            return;
        }

        File dataFolder = new File(plugin.getDataFolder(), "eternia.db");
        if (!dataFolder.exists()) {
            try {
                if (dataFolder.createNewFile()) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&aE&9L&8] &7Criando arquivo SQLite&8."));
                }
            } catch (IOException ignored) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msgError));
                Bukkit.getPluginManager().disablePlugin(plugin);
                return;
            }
        }
        loadSQLite(dataFolder);

    }

    public void loadSQLite(File dataFolder) {
        try {
            Class.forName("org.sqlite.JDBC");
            setConnection(DriverManager.getConnection("jdbc:sqlite:" + dataFolder));
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msgSQLOk));
        } catch (SQLException | ClassNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msgError));
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public String getMsgLoad() {
        return msgLoad;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void close() {
        try {
            if (EterniaLib.mysql) {
                hikari.close();
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msgMySQLFinish));
                return;
            }
            connection.close();
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', messageSQLFinish));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        if (EterniaLib.mysql) {
            return hikari.getConnection();
        }
        return connection;
    }
}