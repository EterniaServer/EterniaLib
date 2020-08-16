package br.com.eterniaserver.eternialib;

import co.aikar.commands.PaperCommandManager;

import com.google.common.collect.ImmutableList;

import org.bstats.bukkit.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

import br.com.eterniaserver.eternialib.sql.Connections;

public class EterniaLib extends JavaPlugin {

    private final List<String> entityList = ImmutableList.of("BEE", "BLAZE", "CAT", "CAVE_SPIDER", "CHICKEN", "COD",
            "COW", "CREEPER", "DOLPHIN", "DONKEY", "DROWNED", "ELDER_GUARDIAN", "ENDER_DRAGON", "ENDERMAN", "ENDERMITE",
            "EVOKER", "FOX", "GHAST", "GIANT", "GUARDIAN", "HOGLIN", "HORSE", "HUSK", "ILLUSIONER", "IRON_GOLEM",
            "MAGMA_CUBE", "MULE", "PANDA", "PARROT", "PHANTOM", "PIG", "PIGLIN", "PILLAGER", "POLAR_BEAR", "PUFFERFISH",
            "RABBIT", "RAVAGER", "SALMON", "SHEEP", "SILVERFISH", "SKELETON", "SKELETON_HORSE", "SLIME", "SNOW_GOLEM",
            "SPIDER", "SQUID", "STRAY", "STRIDER", "TURTLE", "VEX", "VILLAGER", "VINDICATOR", "WITCH", "WITHER",
            "WITHER_SKELETON", "WOLF", "ZOGLIN", "ZOMBIE", "ZOMBIE_HORSE", "ZOMBIFIED_PIGLIN", "ZOMBIE_VILLAGER");

    private static PaperCommandManager manager;
    private static EterniaLib plugin;
    private static boolean mysql;
    public Connections connections;

    @Override
    public void onEnable() {
        setPlugin(this);

        new Metrics(this, 8442);

        setManager(new PaperCommandManager(this));

        final String acf = "acf_messages.yml";
        final File files = new File(getDataFolder(), acf);
        if (!files.exists()) saveResource(acf, false);
        try {
            manager.getLocales().loadYamlLanguageFile(acf, Locale.ENGLISH);
            manager.getLocales().setDefaultLocale(Locale.ENGLISH);
            this.connections = new Connections();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        manager.getCommandCompletions().registerCompletion("colors", c -> ImmutableList.of("black", "darkblue",
                "darkgreen", "darkaqua", "darkred", "darkpurple", "gold", "lightgray", "darkgray", "blue", "green",
                "aqua", "red", "purple", "yellow", "white"));

        manager.getCommandCompletions().registerCompletion("entidades", c -> entityList);


        this.getServer().getPluginManager().registerEvents(new AsyncPlayerPreLogin(), this);

        EQueries.executeQuery("CREATE TABLE IF NOT EXISTS el_cache (uuid varchar(36), player_name varchar(16));", false);

        final Map<String, String> temp = EQueries.getMapString("SELECT * FROM el_cache;", "uuid", "player_name");
        temp.forEach((k, v) -> {
            UUID uuid = UUID.fromString(k);
            UUIDFetcher.lookupCache.put(v, uuid);
            UUIDFetcher.lookupNameCache.put(uuid, v);
            UUIDFetcher.firstLookupCache.put(uuid, v);
        });
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', Connections.MSG_LOAD.replace("%size%", String.valueOf(temp.size()))));

    }

    @Override
    public void onDisable() {
        this.connections.Close();
    }

    private static void setManager(PaperCommandManager paperCommandManager) {
        manager = paperCommandManager;
    }

    private static void setPlugin(EterniaLib eterniaLib) {
        plugin = eterniaLib;
    }

    public static void setMysql(boolean istrue) {
        mysql = istrue;
    }

    public static EterniaLib getPlugin() {
        return EterniaLib.plugin;
    }

    public static boolean getMySQL() {
        return EterniaLib.mysql;
    }

    public static PaperCommandManager getManager() {
        return manager;
    }

}