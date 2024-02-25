package br.com.eterniaserver.eternialib.database;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.database.enums.DatabaseType;
import br.com.eterniaserver.eternialib.database.impl.SGBDInterface;
import br.com.eterniaserver.eternialib.database.impl.sgbds.MariaDBSGBD;
import br.com.eterniaserver.eternialib.database.impl.sgbds.MySQLSGBD;
import br.com.eterniaserver.eternialib.database.impl.sgbds.SQLiteSGBD;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

@Getter
public class HikariSourceConfiguration {

    private final HikariDataSource dataSource;
    private final SGBDInterface sgbdInterface;

    public HikariSourceConfiguration(EterniaLib plugin) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("EterniaLib HikariPool");

        String databaseType = plugin.getStrings().get(Strings.DATABASE_TYPE);
        DatabaseType type = DatabaseType.valueOf(databaseType);

        this.sgbdInterface = switch (type) {
            case MYSQL -> new MySQLSGBD();
            case MARIADB -> new MariaDBSGBD();
            case SQLITE -> new SQLiteSGBD();
        };

        if (type == DatabaseType.SQLITE) {
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
        } else {
            hikariConfig.setUsername(plugin.getStrings().get(Strings.DATABASE_USER));
            hikariConfig.setPassword(plugin.getStrings().get(Strings.DATABASE_PASSWORD));
        }

        hikariConfig.setJdbcUrl(sgbdInterface.jdbcStr(
                plugin.getStrings().get(Strings.DATABASE_HOST),
                plugin.getStrings().get(Strings.DATABASE_PORT),
                plugin.getStrings().get(Strings.DATABASE_DATABASE)
        ));

        // MySQL specific configurations
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Pool configurations
        hikariConfig.setMaxLifetime(plugin.getIntegers().get(Integers.HIKARI_MAX_LIFE_TIME));
        hikariConfig.setConnectionTimeout(plugin.getIntegers().get(Integers.HIKARI_CONNECTION_TIME_OUT));
        hikariConfig.setLeakDetectionThreshold(plugin.getIntegers().get(Integers.HIKARI_LEAK_THRESHOLD));
        hikariConfig.setMinimumIdle(plugin.getIntegers().get(Integers.HIKARI_MIN_POOL_SIZE));
        hikariConfig.setMaximumPoolSize(plugin.getIntegers().get(Integers.HIKARI_MAX_POOL_SIZE));
        hikariConfig.setAllowPoolSuspension(plugin.getBooleans().get(Booleans.HIKARI_ALLOW_POOL_SUSPENSION));

        this.dataSource = new HikariDataSource(hikariConfig);
    }

}
