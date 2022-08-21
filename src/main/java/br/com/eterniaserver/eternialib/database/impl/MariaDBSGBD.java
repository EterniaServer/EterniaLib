package br.com.eterniaserver.eternialib.database.impl;

public class MariaDBSGBD extends MySQLSGBD {

    public MariaDBSGBD() {
        super();
    }

    @Override
    public String jdbcStr() {
        return "mariadb";
    }
}
