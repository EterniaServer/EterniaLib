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
import java.util.Locale;

public class EterniaLib extends JavaPlugin {

    protected static final ConfigsCfg configs = new ConfigsCfg();

    protected static final HikariDataSource hikari = new HikariDataSource();

    protected static File dataFolder;

    protected static PaperCommandManager manager;

    protected static EterniaLib plugin;

    @Override
    public void onEnable() {

        plugin = this;

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
            hikari.setJdbcUrl("jdbc:mysql://" + EterniaLib.configs.host + ":" + EterniaLib.configs.port + "/" + EterniaLib.configs.database);
            hikari.addDataSourceProperty("user", EterniaLib.configs.user);
            hikari.addDataSourceProperty("password", EterniaLib.configs.password);
            hikari.setMaximumPoolSize(EterniaLib.configs.poolSize);
            hikari.setMinimumIdle(EterniaLib.configs.poolSize);
            hikari.setConnectionTestQuery("SELECT 1");
            hikari.setMaxLifetime(850000);
            hikari.setConnectionTimeout(300000);
            hikari.setIdleTimeout(120000);
            hikari.setLeakDetectionThreshold(300000);
            Bukkit.getConsoleSender().sendMessage(EterniaLib.configs.msgUsingMySQL);
            return;
        }

        try {

            dataFolder = new File(ConfigsCfg.DATABASE_FILE_PATH);
            if (!dataFolder.exists() && dataFolder.createNewFile()) {
                Bukkit.getConsoleSender().sendMessage(EterniaLib.configs.msgCreateFile);
            }

            Bukkit.getConsoleSender().sendMessage(EterniaLib.configs.msgUsingSQLite);

        } catch (IOException e) {

            Bukkit.getConsoleSender().sendMessage(EterniaLib.configs.msgError);
            this.getServer().getPluginManager().disablePlugin(this);

        }

    }

    protected static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static boolean getMySQL() {
        return configs.mysql;
    }

}