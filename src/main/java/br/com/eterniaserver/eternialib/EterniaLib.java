package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.sql.queries.CreateTable;

import co.aikar.commands.PaperCommandManager;

import com.zaxxer.hikari.HikariDataSource;
import org.bstats.bukkit.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

public class EterniaLib extends JavaPlugin {

    protected static final ConfigsCfg configs = new ConfigsCfg();

    protected static final HikariDataSource hikari = new HikariDataSource();
    protected static Connection connection;

    protected static PaperCommandManager manager;

    @Override
    public void onEnable() {

        new Metrics(this, 8442);

        getManager();
        getConnection();

        CreateTable createTable = new CreateTable("el_cache");
        createTable.columns.set("uuid varchar(36)", "player_name varchar(16)");
        SQL.execute(createTable);

        this.getServer().getPluginManager().registerEvents(new AsyncPlayerPreLogin(), this);

    }

    private void getManager() {

        manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");


        try {

            final String acf = "acf_messages.yml";
            final File files = new File(getDataFolder(), acf);

            if (!files.exists()) {
                saveResource(acf, false);
            }

            manager.getLocales().loadYamlLanguageFile(acf, Locale.ENGLISH);
            manager.getLocales().setDefaultLocale(Locale.ENGLISH);

        } catch (IOException | InvalidConfigurationException e) {

            e.printStackTrace();

        }

    }

    private void getConnection() {

        if (EterniaLib.getMySQL()) {
            hikari.setPoolName("EterniaServer MySQL Pool");
            hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            hikari.addDataSourceProperty("serverName", EterniaLib.configs.host);
            hikari.addDataSourceProperty("port", EterniaLib.configs.port);
            hikari.addDataSourceProperty("databaseName", EterniaLib.configs.database);
            hikari.addDataSourceProperty("user", EterniaLib.configs.user);
            hikari.addDataSourceProperty("password", EterniaLib.configs.password);
            hikari.setMaximumPoolSize(EterniaLib.configs.poolSize);
            hikari.setMinimumIdle(EterniaLib.configs.poolSize);
            hikari.setMaxLifetime(60000L);
            hikari.setIdleTimeout(45000L);
            hikari.setConnectionTestQuery("SELECT 1;");
            Bukkit.getConsoleSender().sendMessage(EterniaLib.configs.msgUsingMySQL);
            return;
        }

        try {

            File dataFolder = new File(ConfigsCfg.DATABASE_FILE_PATH);
            if (!dataFolder.exists() && dataFolder.createNewFile()) {
                Bukkit.getConsoleSender().sendMessage(EterniaLib.configs.msgCreateFile);
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            Bukkit.getConsoleSender().sendMessage(EterniaLib.configs.msgUsingSQLite);

        } catch (IOException | SQLException | ClassNotFoundException e) {

            Bukkit.getConsoleSender().sendMessage(EterniaLib.configs.msgError);
            this.getServer().getPluginManager().disablePlugin(this);

        }

    }

    public static boolean getMySQL() {
        return configs.mysql;
    }

}