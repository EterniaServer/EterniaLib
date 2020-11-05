package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.sql.queries.CreateTable;
import br.com.eterniaserver.eternialib.sql.queries.Select;

import co.aikar.commands.PaperCommandManager;

import org.bstats.bukkit.Metrics;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

public class EterniaLib extends JavaPlugin {

    protected static PaperCommandManager manager;
    protected static Boolean mysql = Boolean.FALSE;

    @Override
    public void onEnable() {
        new Metrics(this, 8442);

        manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");

        final String acf = "acf_messages.yml";
        final File files = new File(getDataFolder(), acf);
        if (!files.exists()) saveResource(acf, false);

        try {
            manager.getLocales().loadYamlLanguageFile(acf, Locale.ENGLISH);
            manager.getLocales().setDefaultLocale(Locale.ENGLISH);
            new Connections(this);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }


        CreateTable createTable = new CreateTable("el_cache");
        createTable.columns.set("uuid varchar(36)", "player_name varchar(16)");
        SQL.execute(createTable);

        this.getServer().getPluginManager().registerEvents(new AsyncPlayerPreLogin(), this);

        try {
            PreparedStatement statement = SQL.getConnection().prepareStatement(new Select("el_cache").queryString());
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String playerName = resultSet.getString("player_name");
                UUIDFetcher.lookupCache.put(playerName, uuid);
                UUIDFetcher.lookupNameCache.put(uuid, playerName);
                UUIDFetcher.firstLookupCache.put(uuid, playerName);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("lookup = " + UUIDFetcher.firstLookupCache.size());

    }

    public static boolean getMySQL() {
        return mysql;
    }

}