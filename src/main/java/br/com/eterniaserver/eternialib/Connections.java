package br.com.eterniaserver.eternialib;

import com.zaxxer.hikari.HikariDataSource;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.io.IOException;
import java.io.File;

public class Connections {

    protected static HikariDataSource hikari = new HikariDataSource();
    protected static Connection connection;

    private final EterniaLib plugin;
    private final String msgError;
    private final String msgSQLOk;

    public Connections(EterniaLib plugin) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;

        FileConfiguration file = new YamlConfiguration();

        File files = new File(plugin.getDataFolder(), "configs.yml");
        if (!files.exists()) plugin.saveResource("configs.yml", false);
        file.load(files);

        EterniaLib.mysql = file.getBoolean("sql.mysql", false);

        String msgMySQLOk = file.getString("messages.mysql-ok");
        msgSQLOk = file.getString("messages.sql-ok");
        msgError = file.getString("messages.error");

        if (EterniaLib.mysql) {
            hikari.setPoolName("EterniaServer MySQL Pool");
            hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            hikari.addDataSourceProperty("serverName", file.getString("sql.host"));
            hikari.addDataSourceProperty("port", file.getString("sql.port"));
            hikari.addDataSourceProperty("databaseName", file.getString("sql.database"));
            hikari.addDataSourceProperty("user", file.getString("sql.user"));
            hikari.addDataSourceProperty("password", file.getString("sql.password"));
            hikari.setMaxLifetime(60000L);
            hikari.setIdleTimeout(45000L);
            hikari.setConnectionTestQuery("SELECT 1");
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msgMySQLOk));
        } else {
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

    }
    public void loadSQLite(File dataFolder) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msgSQLOk));
        } catch (SQLException | ClassNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msgError));
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

}