package br.com.eterniaserver.eternialib.sql;

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
    private final FileConfiguration file = new YamlConfiguration();
    private final EterniaLib plugin;

    private final String msgLoad;
    private final String msgMySQLOk;
    private final String msgMySQLFinish;
    private final String msgError;
    private final String msgSQLOk;
    private final String messageSQLFinish;

    private static Connection connection;

    public Connections(EterniaLib plugin) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;

        final File files = new File(plugin.getDataFolder(), "configs.yml");
        if (!files.exists()) plugin.saveResource("configs.yml", false);
        file.load(files);

        msgMySQLOk = file.getString("messages.mysql-ok");
        msgSQLOk = file.getString("messages.sql-ok");
        msgMySQLFinish = file.getString("messages.mysql-finish");
        messageSQLFinish = file.getString("messages.sql-finish");
        msgError = file.getString("messages.error");
        msgLoad = file.getString("messages.load");

        connect();
    }

    public void connect() {
        EterniaLib.setMysql(file.getBoolean("sql.mysql"));
        if (EterniaLib.getMySQL()) {
            hikari.setPoolName("EterniaServer MySQL Pool");
            hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            hikari.addDataSourceProperty("serverName", this.file.getString("sql.host"));
            hikari.addDataSourceProperty("port", this.file.getString("sql.port"));
            hikari.addDataSourceProperty("databaseName", this.file.getString("sql.database"));
            hikari.addDataSourceProperty("user", this.file.getString("sql.user"));
            hikari.addDataSourceProperty("password", this.file.getString("sql.password"));
            hikari.setConnectionTestQuery("SELECT 1");
            hikari.setMaxLifetime(60000L);
            hikari.setIdleTimeout(45000L);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msgMySQLOk));
        } else {
            final File dataFolder = new File(plugin.getDataFolder(), "eternia.db");
            if (!dataFolder.exists()) {
                try {
                    if (!dataFolder.createNewFile()) loadSQLite(dataFolder);
                } catch (IOException ignored) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msgError));
                    Bukkit.getPluginManager().disablePlugin(plugin);
                }
                return;
            }
            loadSQLite(dataFolder);
        }
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

    public boolean isClosed() {
        return hikari.isClosed();
    }

    public void close() {
        if (EterniaLib.getMySQL()) {
            hikari.close();
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msgMySQLFinish));
        }
        else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', messageSQLFinish));
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
        return hikari.getConnection();
    }

    public String getMsgLoad() {
        return msgLoad;
    }

    private static void setConnection(Connection cnct) {
        connection = cnct;
    }

    public static Connection getSQLite() {
        return connection;
    }

}