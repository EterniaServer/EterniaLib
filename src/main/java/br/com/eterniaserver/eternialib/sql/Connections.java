package br.com.eterniaserver.eternialib.sql;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.EQueries;
import br.com.eterniaserver.eternialib.UUIDFetcher;

import com.zaxxer.hikari.HikariConfig;
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
import java.util.HashMap;
import java.util.UUID;

public class Connections {

    boolean mysql;
    private HikariDataSource hikari;
    private final FileConfiguration file;

    public static Connection connection;
    private final String MSG_LOAD;

    private final String MSG_MYSQL_OK;
    private final String MSG_MYSQL_FINISH;

    private final String MSG_SQL_OK;
    private final String MSG_SQL_FINISH;

    public Connections() throws IOException, InvalidConfigurationException {
        file = new YamlConfiguration();
        final File files = new File(EterniaLib.getPlugin().getDataFolder(), "configs.yml");
        if (!files.exists()) EterniaLib.getPlugin().saveResource("configs.yml", false);
        file.load(files);
        MSG_MYSQL_OK = file.getString("messages.mysql-ok");
        MSG_SQL_OK = file.getString("messages.sql-ok");
        MSG_MYSQL_FINISH = file.getString("messages.mysql-finish");
        MSG_SQL_FINISH = file.getString("messages.sql-finish");
        MSG_LOAD = file.getString("messages.load");
        Connect();
    }

    public void Connect() {
        mysql = file.getBoolean("sql.mysql");
        EterniaLib.mysql = mysql;
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
            hikari = new HikariDataSource(config);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', MSG_MYSQL_OK));
        } else {
            final File dataFolder = new File(EterniaLib.getPlugin().getDataFolder(), "eternia.db");
            if (!dataFolder.exists()) {
                try { dataFolder.createNewFile(); }
                catch (IOException ignored) {}
            }
            try {
                Class.forName("org.sqlite.JDBC");
                Connections.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', MSG_SQL_OK));
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        EQueries.executeQuery("CREATE TABLE IF NOT EXISTS el_cache (uuid varchar(36), player_name varchar(16));", false);

        final HashMap<String, String> temp = EQueries.getMapString("SELECT * FROM el_cache;", "uuid", "player_name");
        temp.forEach((k, v) -> {
            UUID uuid = UUID.fromString(k);
            UUIDFetcher.lookupCache.put(v, uuid);
            UUIDFetcher.lookupNameCache.put(uuid, v);
            UUIDFetcher.firstLookupCache.put(uuid, v);
        });
        Bukkit.getServer().getConsoleSender().sendMessage(MSG_LOAD.replace("%size%", String.valueOf(temp.size())));
    }

    public boolean isClosed() {
        return hikari.isClosed();
    }

    public void Close() {
        if (mysql) {
            hikari.close();
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', MSG_MYSQL_FINISH));
        }
        else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', MSG_SQL_FINISH));
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
        return (hikari != null) ? hikari.getConnection() : null;
    }
}