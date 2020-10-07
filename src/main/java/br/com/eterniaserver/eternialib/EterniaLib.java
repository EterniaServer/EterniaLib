package br.com.eterniaserver.eternialib;

import co.aikar.commands.PaperCommandManager;

import org.bstats.bukkit.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import br.com.eterniaserver.eternialib.sql.Connections;

public class EterniaLib extends JavaPlugin {

    private static PaperCommandManager manager;
    private static Connections connections;

    private static boolean mysql;

    @Override
    public void onEnable() {
        new Metrics(this, 8442);

        setManager(new PaperCommandManager(this));

        final String acf = "acf_messages.yml";
        final File files = new File(getDataFolder(), acf);
        if (!files.exists()) saveResource(acf, false);
        try {
            manager.getLocales().loadYamlLanguageFile(acf, Locale.ENGLISH);
            manager.getLocales().setDefaultLocale(Locale.ENGLISH);
            setConnections(new Connections(this));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        EQueries.executeQuery("CREATE TABLE IF NOT EXISTS el_cache (uuid varchar(36), player_name varchar(16));", false);

        final Map<String, String> temp = EQueries.getMapString("SELECT * FROM el_cache;", "uuid", "player_name");
        temp.forEach((k, v) -> {
            UUID uuid = UUID.fromString(k);
            UUIDFetcher.lookupCache.put(v, uuid);
            UUIDFetcher.lookupNameCache.put(uuid, v);
            UUIDFetcher.firstLookupCache.put(uuid, v);
        });
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', connections.getMsgLoad().replace("%size%", String.valueOf(temp.size()))));

        this.getServer().getPluginManager().registerEvents(new AsyncPlayerPreLogin(), this);


        manager.enableUnstableAPI("help");

    }

    @Override
    public void onDisable() {
        connections.close();
    }

    private static void setConnections(Connections cnct) {
        connections = cnct;
    }

    private static void setManager(PaperCommandManager paperCommandManager) {
        manager = paperCommandManager;
    }

    public static void setMysql(boolean istrue) {
        mysql = istrue;
    }

    public static Connections getConnections() {
        return connections;
    }

    public static boolean getMySQL() {
        return mysql;
    }

    public static PaperCommandManager getManager() {
        return manager;
    }

}