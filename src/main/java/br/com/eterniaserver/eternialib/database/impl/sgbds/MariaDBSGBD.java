package br.com.eterniaserver.eternialib.database.impl.sgbds;

public class MariaDBSGBD extends MySQLSGBD {

    public MariaDBSGBD() {
        super();
    }

    @Override
    public String jdbcStr(String... args) {
        if (args.length < 3) {
            return null;
        }
        return "jdbc:mariadb://%s:%s/%s".formatted(args[0], args[1], args[2]);
    }
}
