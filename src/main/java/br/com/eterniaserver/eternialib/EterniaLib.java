package br.com.eterniaserver.eternialib;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.eterniaserver.eternialib.sql.Connections;

import org.bukkit.plugin.java.JavaPlugin;

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
    public Connections connections;
    public static boolean mysql;

    @Override
    public void onEnable() {
        new Metrics(this, 8442);

        manager = new PaperCommandManager(this);

        EterniaLib.plugin = this;
        final File files = new File(getDataFolder(), "acf_messages.yml");
        if (!files.exists()) saveResource("acf_messages.yml", false);
        try {
            manager.getLocales().loadYamlLanguageFile("acf_messages.yml", Locale.ENGLISH);
            this.connections = new Connections();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        manager.getCommandCompletions().registerCompletion("colors", c -> ImmutableList.of("black", "darkblue",
                "darkgreen", "darkaqua", "darkred", "darkpurple", "gold", "lightgray", "darkgray", "blue", "green",
                "aqua", "red", "purple", "yellow", "white"));

        manager.getCommandCompletions().registerCompletion("entidades", c -> entityList);


        getServer().getPluginManager().registerEvents(new AsyncPlayerPreLogin(), this);

    }

    @Override
    public void onDisable() {
        this.connections.Close();
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