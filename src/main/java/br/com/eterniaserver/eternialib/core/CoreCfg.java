package br.com.eterniaserver.eternialib.core;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.configuration.ReloadableConfiguration;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.database.exceptions.DatabaseException;
import br.com.eterniaserver.eternialib.database.impl.DatabaseImpl;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class CoreCfg implements ReloadableConfiguration {

    private static final String PLUGIN_PATH = "plugins" + File.separator + "EterniaLib";
    private static final String FOLDER_PATH = PLUGIN_PATH + File.separator + "config";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "core.yml";

    private final EterniaLib plugin;

    private final FileConfiguration inConfig;
    private final FileConfiguration outConfig;
    private final String[] strings;
    private final int[] integers;
    private final boolean[] booleans;

    public CoreCfg(EterniaLib plugin, String[] strings, int[] integers, boolean[] booleans) {
        this.inConfig = YamlConfiguration.loadConfiguration(new File(getFilePath()));
        this.outConfig = new YamlConfiguration();
        this.plugin = plugin;
        this.strings = strings;
        this.integers = integers;
        this.booleans = booleans;
    }

    @Override
    public FileConfiguration inFileConfiguration() {
        return inConfig;
    }

    @Override
    public FileConfiguration outFileConfiguration() {
        return outConfig;
    }

    @Override
    public String getFolderPath() {
        return FOLDER_PATH;
    }

    @Override
    public String getFilePath() {
        return FILE_PATH;
    }

    @Override
    public String[] messages() {
        return null;
    }

    @Override
    public ConfigurationCategory category() {
        return ConfigurationCategory.BLOCKED;
    }

    @Override
    public void executeConfig() {
        strings[Strings.PLUGIN_PREFIX.ordinal()] = inConfig.getString("server.plugin-prefix", "<color:#aaaaaa>[EL] ");
        strings[Strings.DATABASE_TYPE.ordinal()] = inConfig.getString("database.type", "MARIADB");
        strings[Strings.DATABASE_HOST.ordinal()] = inConfig.getString("database.host", "el-mariadb");
        strings[Strings.DATABASE_PORT.ordinal()] = inConfig.getString("database.port", "3306");
        strings[Strings.DATABASE_DATABASE.ordinal()] = inConfig.getString("database.database", "el_database");
        strings[Strings.DATABASE_USER.ordinal()] = inConfig.getString("database.user", "user");
        strings[Strings.DATABASE_PASSWORD.ordinal()] = inConfig.getString("database.password", "password");
        strings[Strings.DATABASE_TABLE.ordinal()] = inConfig.getString("database.table-cache", "el_cache");
        integers[Integers.DATABASE_POOL_SIZE.ordinal()] = inConfig.getInt("sql.pool-size", 10);

        outConfig.options().setHeader(List.of(
                "Tipos disponíveis de database: MARIADB, POSTGRESQL",
                "Available type of database: MARIADB, POSTGRESQL"
        ));

        outConfig.set("server.plugin-prefix", strings[Strings.PLUGIN_PREFIX.ordinal()]);
        outConfig.set("database.type", strings[Strings.DATABASE_TYPE.ordinal()]);
        outConfig.set("database.host", strings[Strings.DATABASE_HOST.ordinal()]);
        outConfig.set("database.port", strings[Strings.DATABASE_PORT.ordinal()]);
        outConfig.set("database.database", strings[Strings.DATABASE_DATABASE.ordinal()]);
        outConfig.set("database.user", strings[Strings.DATABASE_USER.ordinal()]);
        outConfig.set("database.password", strings[Strings.DATABASE_PASSWORD.ordinal()]);
        outConfig.set("database.table-cache", strings[Strings.DATABASE_TABLE.ordinal()]);
        outConfig.set("database.pool-size", integers[Integers.DATABASE_POOL_SIZE.ordinal()]);
    }

    @Override
    public void executeCritical() {
        try {
            EterniaLib.setDatabase(new DatabaseImpl(this.plugin));
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}
