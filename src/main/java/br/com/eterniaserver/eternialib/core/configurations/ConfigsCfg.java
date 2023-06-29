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


public class ConfigsCfg implements ReloadableConfiguration {

    private final FileConfiguration config;

    private final String[] strings;
    private final int[] integers;
    private final boolean[] booleans;
    private final Runnable criticalRunnable;

    public ConfigsCfg(final String[] strings,
                      final int[] integers,
                      final boolean[] booleans,
                      final Runnable criticalRunnable) {
        this.strings = strings;
        this.integers = integers;
        this.booleans = booleans;
        this.criticalRunnable = criticalRunnable;

        this.config = YamlConfiguration.loadConfiguration(new File(Constants.CONFIG_FILE_PATH));
    }

    @Override
    public ConfigurationCategory category() {
        return ConfigurationCategory.WARNING_ADVICE;
    }

    @Override
    public void executeConfig() throws IOException {
        // Temporary constants
        final String admin = "admin";

        strings[Strings.SERVER_PREFIX.ordinal()] = config.getString("server.prefix", "$8[$aE$9L$8]$7 ").replace('$', (char) 0x00A7);
        strings[Strings.SQL_HOST.ordinal()] = config.getString("sql.host", "127.0.0.1");
        strings[Strings.SQL_PORT.ordinal()] = config.getString("sql.port", "3306");
        strings[Strings.SQL_DATABASE.ordinal()] = config.getString("sql.database", admin);
        strings[Strings.SQL_USER.ordinal()] = config.getString("sql.user", admin);
        strings[Strings.SQL_PASSWORD.ordinal()] = config.getString("sql.password", admin);
        strings[Strings.SQL_TABLE.ordinal()] = config.getString("sql.table-cache", "el_cache");

        booleans[Booleans.MYSQL.ordinal()] = config.getBoolean("sql.mysql", false);
        booleans[Booleans.LOBBY_SYSTEM.ordinal()] = config.getBoolean("lobby.enabled", false);

        integers[Integers.SQL_POOL_SIZE.ordinal()] = config.getInt("sql.pool-size", 10);


        // Save the configurations
        final FileConfiguration outConfig = new YamlConfiguration();

        outConfig.set("server.prefix", strings[Strings.SERVER_PREFIX.ordinal()]);
        outConfig.set("sql.host", strings[Strings.SQL_HOST.ordinal()]);
        outConfig.set("sql.port", strings[Strings.SQL_PORT.ordinal()]);
        outConfig.set("sql.database", strings[Strings.SQL_DATABASE.ordinal()]);
        outConfig.set("sql.user", strings[Strings.SQL_USER.ordinal()]);
        outConfig.set("sql.password", strings[Strings.SQL_PASSWORD.ordinal()]);
        outConfig.set("sql.table-cache", strings[Strings.SQL_TABLE.ordinal()]);

        outConfig.set("sql.mysql", booleans[Booleans.MYSQL.ordinal()]);
        outConfig.set("lobby.enabled", booleans[Booleans.LOBBY_SYSTEM.ordinal()]);

        outConfig.set("sql.pool-size", integers[Integers.SQL_POOL_SIZE.ordinal()]);

        outConfig.save(Constants.CONFIG_FILE_PATH);
    }

    @Override
    public void executeCritical() {
        criticalRunnable.run();
    }

}
