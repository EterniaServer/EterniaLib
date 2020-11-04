package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.sql.queries.CreateTable;
import br.com.eterniaserver.eternialib.sql.queries.Select;

import co.aikar.commands.PaperCommandManager;

import org.bstats.bukkit.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

public class EterniaLib extends JavaPlugin {

    protected static PaperCommandManager manager;
    protected static Connections connections;
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
            connections = new Connections(this);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        CreateTable createTable = new CreateTable("el_cache");
        createTable.columns.set("uuid varchar(36)", "player_name varchar(16)");
        SQL.execute(createTable);

        try (CachedRowSet cachedRowSet = SQL.getRowSet(new Select("el_cache"))) {
            while (cachedRowSet.next()) {
                UUID uuid = UUID.fromString(cachedRowSet.getString("uuid"));
                String playerName = cachedRowSet.getString("player_name");
                UUIDFetcher.lookupCache.put(playerName, uuid);
                UUIDFetcher.lookupNameCache.put(uuid, playerName);
                UUIDFetcher.firstLookupCache.put(uuid, playerName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', connections.getMsgLoad().replace("%size%", String.valueOf(UUIDFetcher.firstLookupCache.size()))));

        this.getServer().getPluginManager().registerEvents(new AsyncPlayerPreLogin(), this);

    }

    @Override
    public void onDisable() {
        connections.close();
    }
}