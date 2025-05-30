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

import br.com.eterniaserver.eternialib.uuidfetcher.UUIDFetcher;
import br.com.eterniaserver.eternialib.uuidfetcher.impl.UUIDFetcherImpl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.http.HttpClient;
import java.util.EnumMap;
import java.util.Map;

@Getter
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

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static UUIDFetcher uuidFetcher;

    private final Map<Strings, String> strings = new EnumMap<>(Strings.class);
    private final Map<Integers, Integer> integers = new EnumMap<>(Integers.class);
    private final Map<Booleans, Boolean> booleans = new EnumMap<>(Booleans.class);

    @Override
    public void onEnable() {
        new Metrics(this, 8442);

        saveResource("command_messages_pt.yml", false);

        EterniaLib.setCfgManager(new ConfigurationManagerImpl());
        EterniaLib.setCmdManager(new CommandManagerImpl(this));

        EterniaLib.getCfgManager().registerConfiguration(
                "eternialib",
                "core",
                true,
                new CoreCfg(EterniaLib::setChatCommons, EterniaLib::setDatabase, strings, integers, booleans)
        );

        AdvancedCommandManager impl = new AdvancedCommandManagerImpl(this, integers.get(Integers.TICK_DELAY));
        getServer().getScheduler().runTaskTimer(this, impl, 20L, integers.get(Integers.TICK_DELAY));

        EterniaLib.setAdvancedCmdManager(impl);
        EterniaLib.setUuidFetcher(new UUIDFetcherImpl(HttpClient.newHttpClient()));

        new Manager(this);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        EterniaLib.getDatabase().closeAllConnections();
    }

}
