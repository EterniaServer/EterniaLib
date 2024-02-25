package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.chat.ChatCommons;
import br.com.eterniaserver.eternialib.commands.AdvancedCommandManager;
import br.com.eterniaserver.eternialib.commands.CommandManager;
import br.com.eterniaserver.eternialib.commands.impl.AdvancedCommandManagerImpl;
import br.com.eterniaserver.eternialib.commands.impl.CommandManagerImpl;
import br.com.eterniaserver.eternialib.configuration.ConfigurationManager;
import br.com.eterniaserver.eternialib.configuration.impl.ConfigurationManagerImpl;
import br.com.eterniaserver.eternialib.core.configs.CoreCfg;
import br.com.eterniaserver.eternialib.core.Manager;
import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.database.Database;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EterniaLib extends JavaPlugin {

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static AdvancedCommandManager advancedCmdManager;

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static ChatCommons chatCommons;

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static CommandManager cmdManager;

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static ConfigurationManager cfgManager;

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static Database database;

    private static final Map<String, String> tableNames = new HashMap<>();
    private static final List<String> errorsCode = new LinkedList<>();
    private static final ConcurrentMap<String, UUID> fetchByNameMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<UUID, String> fetchByUUIDMap = new ConcurrentHashMap<>();

    @Getter
    private final EnumMap<Strings, String> strings = new EnumMap<>(Strings.class);
    @Getter
    private final EnumMap<Integers, Integer> integers = new EnumMap<>(Integers.class);
    @Getter
    private final EnumMap<Booleans, Boolean> booleans = new EnumMap<>(Booleans.class);

    @Override
    public void onEnable() {
        EterniaLib.setCfgManager(new ConfigurationManagerImpl());
        EterniaLib.setCmdManager(new CommandManagerImpl(this));

        EterniaLib.getCfgManager().registerConfiguration(
                "eternialib",
                "core",
                true,
                new CoreCfg(EterniaLib::setChatCommons, EterniaLib::setDatabase, this)
        );

        AdvancedCommandManager impl = new AdvancedCommandManagerImpl(this, integers.get(Integers.TICK_DELAY));
        getServer().getScheduler().runTaskTimer(this, impl, 20L, integers.get(Integers.TICK_DELAY));

        EterniaLib.setAdvancedCmdManager(impl);

        new Manager(this);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        EterniaLib.getDatabase().closeAllConnections();
    }

    public static UUID getUUIDOf(String playerName) {
        return fetchByNameMap.get(playerName);
    }

    public static String getTableName(String label) {
        return tableNames.get(label);
    }

    public static void addTableName(String label, String tableName) {
        tableNames.put(label, tableName);
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


    public List<String> getErrors() {
        return errorsCode;
    }

}
