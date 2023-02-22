package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.commands.AdvancedCommandManager;
import br.com.eterniaserver.eternialib.commands.CommandManager;
import br.com.eterniaserver.eternialib.commands.impl.AdvancedCommandManagerImpl;
import br.com.eterniaserver.eternialib.commands.impl.CommandManagerImpl;
import br.com.eterniaserver.eternialib.configuration.ReloadableConfiguration;
import br.com.eterniaserver.eternialib.core.configs.CoreCfg;
import br.com.eterniaserver.eternialib.core.Manager;
import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.database.DatabaseInterface;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

public class EterniaLib extends JavaPlugin {

    private static final String VERSION = "4.0.8";
    private static final Map<String, ReloadableConfiguration> configurations = new HashMap<>();
    private static final List<String> configurationsList = new ArrayList<>();
    private static final List<String> errorsCode = new LinkedList<>();
    private static final ConcurrentMap<String, UUID> fetchByNameMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<UUID, String> fetchByUUIDMap = new ConcurrentHashMap<>();

    private static DatabaseInterface database;
    private static CommandManager commandManager;
    private static AdvancedCommandManager advancedCommandManager;

    private final boolean[] booleans = new boolean[Booleans.values().length];
    private final int[] integers = new int[Integers.values().length];
    private final String[] strings = new String[Strings.values().length];
    private final String[] messages = new String[Messages.values().length];

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        this.loadCommandManager();
        this.loadConfigurations();
        this.loadAdvancedCommandManager();
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

    public static void registerLog(String error) {
        errorsCode.add(error);
    }

    public static CommandManager getCmdManager() {
        return commandManager;
    }

    public static AdvancedCommandManager getAdvancedCmdManager() {
        return advancedCommandManager;
    }

    public static DatabaseInterface getDatabase() {
        return database;
    }

    public static String getVersion() {
        return VERSION;
    }

    public List<String> getConfigurations() {
        return configurationsList;
    }

    public ReloadableConfiguration getConfiguration(String entry) {
        return configurations.get(entry);
    }

    public static void registerConfiguration(String plugin, String config, ReloadableConfiguration configuration) {
        String entry = plugin + "_" + config;

        configurationsList.add(entry);
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

    private static void setCommandManagerInterface(CommandManager commandManagerImpl) {
        commandManager = commandManagerImpl;
    }

    private static void setAdvancedCommandManagerInterface(AdvancedCommandManager advancedCommandManagerImpl) {
        advancedCommandManager = advancedCommandManagerImpl;
    }

    public List<String> getErrors() {
        return errorsCode;
    }

    private void loadConfigurations() {
        CoreCfg coreCfg = new CoreCfg(this, messages, strings, integers, booleans);

        EterniaLib.registerConfiguration("eternialib", "core", coreCfg);
        getLogger().log(Level.INFO, "Registered configuration: eternialib_core, category {0}", coreCfg.category());

        coreCfg.executeConfig();
        coreCfg.executeCritical();
        coreCfg.saveConfiguration(true);

    }

    private void loadCommandManager() {
        try {
            CommandManager impl = new CommandManagerImpl(this);
            setCommandManagerInterface(impl);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error when creating or loading YML configuration file.");
        } catch (InvalidConfigurationException e) {
            getLogger().log(Level.SEVERE, "YML configuration file is invalid.");
        }
    }

    private void loadAdvancedCommandManager() {
        int ticksPerSecond = this.getInteger(Integers.COMMANDS_TICKS_PER_SECOND);
        AdvancedCommandManager impl = new AdvancedCommandManagerImpl(this, ticksPerSecond);
        setAdvancedCommandManagerInterface(impl);
    }

    private void loadCoreManager() {
        new Manager(this);
    }

    public Component getComponentMessage(Messages messagesId, boolean prefix, String... args) {
        return miniMessage.deserialize(getMessage(messagesId, prefix, args));
    }

    public String getMessage(Messages messagesId, boolean prefix, String... args) {
        String message = messages[messagesId.ordinal()];

        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", args[i]);
        }

        if (prefix) {
            return getString(Strings.PLUGIN_PREFIX) + message;
        }

        return message;
    }

    public Component parseColor(String string) {
        return miniMessage.deserialize(string);
    }

}
