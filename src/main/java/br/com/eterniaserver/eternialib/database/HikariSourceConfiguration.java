package br.com.eterniaserver.eternialib.database;

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

import java.util.Map;

@Getter
public class HikariSourceConfiguration {

    private final HikariDataSource dataSource;
    private final SGBDInterface sgbdInterface;

    public HikariSourceConfiguration(Map<Strings, String> strings,
                                     Map<Integers, Integer> integers,
                                     Map<Booleans, Boolean> booleans) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("EterniaLib HikariPool");

        String databaseType = strings.get(Strings.DATABASE_TYPE);
        DatabaseType type = DatabaseType.valueOf(databaseType);

        this.sgbdInterface = switch (type) {
            case MYSQL -> new MySQLSGBD();
            case MARIADB -> new MariaDBSGBD();
            case SQLITE -> new SQLiteSGBD();
        };

        if (type == DatabaseType.SQLITE) {
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
        } else {
            hikariConfig.setUsername(strings.get(Strings.DATABASE_USER));
            hikariConfig.setPassword(strings.get(Strings.DATABASE_PASSWORD));
        }

        hikariConfig.setJdbcUrl(sgbdInterface.jdbcStr(
                strings.get(Strings.DATABASE_HOST),
                strings.get(Strings.DATABASE_PORT),
                strings.get(Strings.DATABASE_DATABASE)
        ));

        // MySQL specific configurations
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Pool configurations
        hikariConfig.setMaxLifetime(integers.get(Integers.HIKARI_MAX_LIFE_TIME));
        hikariConfig.setConnectionTimeout(integers.get(Integers.HIKARI_CONNECTION_TIME_OUT));
        hikariConfig.setLeakDetectionThreshold(integers.get(Integers.HIKARI_LEAK_THRESHOLD));
        hikariConfig.setMinimumIdle(integers.get(Integers.HIKARI_MIN_POOL_SIZE));
        hikariConfig.setMaximumPoolSize(integers.get(Integers.HIKARI_MAX_POOL_SIZE));
        hikariConfig.setAllowPoolSuspension(booleans.get(Booleans.HIKARI_ALLOW_POOL_SUSPENSION));

        this.dataSource = new HikariDataSource(hikariConfig);
    }

}
