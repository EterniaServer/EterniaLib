package br.com.eterniaserver.eternialib.core.configurations;

import br.com.eterniaserver.eternialib.Constants;
import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.core.interfaces.ReloadableConfiguration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigsCfg implements ReloadableConfiguration {

    private final Runnable criticalRunnable;
    private final String[] strings;
    private final int[] integers;
    private final boolean[] booleans;
    private final List<String> protocolVersions;

    public ConfigsCfg(final String[] strings, final int[] integers, final boolean[] booleans, final List<String> protocolVersions, final Runnable criticalRunnable) {
        this.strings = strings;
        this.integers = integers;
        this.booleans = booleans;
        this.protocolVersions = protocolVersions;
        this.criticalRunnable = criticalRunnable;
    }

    @Override
    public ConfigurationCategory category() {
        return ConfigurationCategory.WARNING_ADVICE;
    }

    @Override
    public void executeConfig() {

        protocolVersions.clear();

        // Load the configurations
        final FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Constants.CONFIG_FILE_PATH));

        strings[Strings.SERVER_PREFIX.ordinal()] = config.getString("server.prefix", "$8[$aE$9L$8]$7 ").replace('$', (char) 0x00A7);
        strings[Strings.SQL_HOST.ordinal()] = config.getString("sql.host", "127.0.0.1");
        strings[Strings.SQL_PORT.ordinal()] = config.getString("sql.port", "3306");
        strings[Strings.SQL_DATABASE.ordinal()] = config.getString("sql.database", "admin");
        strings[Strings.SQL_USER.ordinal()] = config.getString("sql.user", "admin");
        strings[Strings.SQL_PASSWORD.ordinal()] = config.getString("sql.password", "admin");
        strings[Strings.SQL_TABLE.ordinal()] = config.getString("sql.table-cache", "el_cache");

        booleans[Booleans.MYSQL.ordinal()] = config.getBoolean("sql.mysql", false);
        booleans[Booleans.LOBBY_SYSTEM.ordinal()] = config.getBoolean("lobby.enabled", false);
        booleans[Booleans.PROTOCOL_SUPPORT.ordinal()] = config.getBoolean("server.protocol-support.enabled", false);

        integers[Integers.SQL_POOL_SIZE.ordinal()] = config.getInt("sql.pool-size");

        final List<String> versionsList = config.getStringList("server.protocol-support.versions-enabled");
        if (versionsList.size() == 0) {
            versionsList.add("MINECRAFT_1_13_1");
            versionsList.add("MINECRAFT_1_13_2");
            versionsList.add("MINECRAFT_1_14_1");
            versionsList.add("MINECRAFT_1_14_2");
            versionsList.add("MINECRAFT_1_14_3");
            versionsList.add("MINECRAFT_1_14_4");
            versionsList.add("MINECRAFT_1_15");
            versionsList.add("MINECRAFT_1_15_1");
            versionsList.add("MINECRAFT_1_15_2");
            versionsList.add("MINECRAFT_1_16");
            versionsList.add("MINECRAFT_1_16_1");
            versionsList.add("MINECRAFT_1_16_2");
            versionsList.add("MINECRAFT_1_16_3");
        }

        protocolVersions.addAll(versionsList);

        // Save the configurations
        final FileConfiguration outConfig = new YamlConfiguration();
        outConfig.options().header("List of versions to ProtocolSupport: MINECRAFT_1_4_7 MINECRAFT_1_5_1 MINECRAFT_1_5_2\n" +
                "MINECRAFT_1_6_1 MINECRAFT_1_6_2 MINECRAFT_1_6_4 MINECRAFT_1_7_5 MINECRAFT_1_7_10 MINECRAFT_1_8\n" +
                "MINECRAFT_1_9 MINECRAFT_1_9_1 MINECRAFT_1_9_2 MINECRAFT_1_9_4 MINECRAFT_1_10 MINECRAFT_1_11\n" +
                "MINECRAFT_1_11_1 MINECRAFT_1_12 MINECRAFT_1_12_1 MINECRAFT_1_12_2 MINECRAFT_1_13_1 MINECRAFT_1_13_2\n" +
                "MINECRAFT_1_14_1 MINECRAFT_1_14_2 MINECRAFT_1_14_3 MINECRAFT_1_14_4 MINECRAFT_1_15 MINECRAFT_1_15_1\n" +
                "MINECRAFT_1_15_2 MINECRAFT_1_16 MINECRAFT_1_16_1 MINECRAFT_1_16_1 MINECRAFT_1_16_2 MINECRAFT_1_16_3");

        outConfig.set("server.prefix", strings[Strings.SERVER_PREFIX.ordinal()]);
        outConfig.set("sql.host", strings[Strings.SQL_HOST.ordinal()]);
        outConfig.set("sql.port", strings[Strings.SQL_PORT.ordinal()]);
        outConfig.set("sql.database", strings[Strings.SQL_DATABASE.ordinal()]);
        outConfig.set("sql.user", strings[Strings.SQL_USER.ordinal()]);
        outConfig.set("sql.password", strings[Strings.SQL_PASSWORD.ordinal()]);
        outConfig.set("sql.table-cache", strings[Strings.SQL_TABLE.ordinal()]);

        outConfig.set("sql.mysql", booleans[Booleans.MYSQL.ordinal()]);
        outConfig.set("lobby.enabled", booleans[Booleans.LOBBY_SYSTEM.ordinal()]);
        outConfig.set("server.protocol-support.enabled", booleans[Booleans.PROTOCOL_SUPPORT.ordinal()]);

        outConfig.set("server.protocol-support.versions-enabled", protocolVersions);

        outConfig.set("sql.pool-size", integers[Integers.SQL_POOL_SIZE.ordinal()]);

        try {
            outConfig.save(Constants.CONFIG_FILE_PATH);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    @Override
    public void executeCritical() {
        criticalRunnable.run();
    }

}
