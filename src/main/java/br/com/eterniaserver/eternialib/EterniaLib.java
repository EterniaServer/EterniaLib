package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.configuration.ReloadableConfiguration;
import br.com.eterniaserver.eternialib.core.CoreCfg;
import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.database.DatabaseInterface;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EterniaLib extends JavaPlugin {

    private static final String version = "4.0.0";
    private static final Map<String, ReloadableConfiguration> configurations = new HashMap<>();

    private static DatabaseInterface database;

    private final boolean[] booleans = new boolean[Booleans.values().length];
    private final int[] integers = new int[Integers.values().length];
    private final String[] strings = new String[Strings.values().length];

    public EterniaLib() {
        super();
    }

    protected EterniaLib(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        this.loadConfigurations();
    }

    @Override
    public void onDisable() {
        EterniaLib.getDatabase().closeAllConnections();
    }

    public static DatabaseInterface getDatabase() {
        return database;
    }

    public static String getVersion() {
        return version;
    }

    public static ReloadableConfiguration getConfiguration(String entry) {
        return configurations.get(entry);
    }

    public static void registerConfiguration(String plugin, String config, ReloadableConfiguration configuration) {
        String entry = plugin + "_" + config;
        configurations.put(entry, configuration);
    }

    public static void setDatabase(DatabaseInterface databaseImpl) {
        database = databaseImpl;
    }

    private void loadConfigurations() {
        CoreCfg coreCfg = new CoreCfg(this, strings, integers, booleans);

        EterniaLib.registerConfiguration("eternialib", "core", coreCfg);

        coreCfg.executeConfig();
        coreCfg.executeCritical();
        coreCfg.saveConfiguration(true);
    }

    public int getInteger(final Integers entry) {
        return integers[entry.ordinal()];
    }

    public boolean getBoolean(final Booleans entry) {
        return booleans[entry.ordinal()];
    }

    public String getString(final Strings entry) {
        return strings[entry.ordinal()];
    }

}
