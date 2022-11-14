package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.commands.CommandManagerInterface;
import br.com.eterniaserver.eternialib.commands.impl.CommandManager;
import br.com.eterniaserver.eternialib.configuration.ReloadableConfiguration;
import br.com.eterniaserver.eternialib.core.CoreCfg;
import br.com.eterniaserver.eternialib.core.Manager;
import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.database.DatabaseInterface;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EterniaLib extends JavaPlugin {

    private static final String version = "4.0.0";
    private static final Map<String, ReloadableConfiguration> configurations = new HashMap<>();
    private static final ConcurrentMap<String, UUID> fetchByNameMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<UUID, String> fetchByUUIDMap = new ConcurrentHashMap<>();

    private static DatabaseInterface database;
    private static CommandManagerInterface commandManager;

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
        this.loadCommandManager();
        this.loadCoreManager();
    }

    @Override
    public void onDisable() {
        EterniaLib.getDatabase().closeAllConnections();
    }

    public static UUID getUUIDOf(String playerName) {
        return fetchByNameMap.get(playerName);
    }

    public static void registerNewUUID(String playerName, UUID uuid) {
        String possibleOldName = fetchByUUIDMap.get(uuid);
        if (possibleOldName != null) {
            fetchByNameMap.remove(possibleOldName);
        }

        fetchByNameMap.put(playerName, uuid);
        fetchByUUIDMap.put(uuid, playerName);
    }

    public static CommandManagerInterface getCmdManager() {
        return commandManager;
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

    public int getInteger(final Integers entry) {
        return integers[entry.ordinal()];
    }

    public boolean getBoolean(final Booleans entry) {
        return booleans[entry.ordinal()];
    }

    public String getString(final Strings entry) {
        return strings[entry.ordinal()];
    }

    public void setDatabase(DatabaseInterface databaseImpl) {
        setDatabaseInterface(databaseImpl);
    }

    private static void setDatabaseInterface(DatabaseInterface databaseImpl) {
        database = databaseImpl;
    }

    private static void setCommandManagerInterface(CommandManagerInterface commandManagerImpl) {
        commandManager = commandManagerImpl;
    }

    private void loadConfigurations() {
        CoreCfg coreCfg = new CoreCfg(this, strings, integers, booleans);

        EterniaLib.registerConfiguration("eternialib", "core", coreCfg);

        coreCfg.executeConfig();
        coreCfg.executeCritical();
        coreCfg.saveConfiguration(true);
    }

    private void loadCommandManager() {
        try {
            setCommandManagerInterface(new CommandManager(this));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCoreManager() {
        new Manager(this);
    }

}
