package br.com.eterniaserver.eternialib.database.impl.sgbds;

public class MariaDBSGBD extends MySQLSGBD {

    public MariaDBSGBD() {
        super();
    }

    @Override
    public String jdbcStr() {
        return "mariadb://";
    }
}
