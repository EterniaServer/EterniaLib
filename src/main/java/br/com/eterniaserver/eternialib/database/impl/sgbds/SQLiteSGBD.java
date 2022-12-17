package br.com.eterniaserver.eternialib.database.impl.sgbds;

public class SQLiteSGBD extends MySQLSGBD {

    public SQLiteSGBD() {
        super();
    }

    @Override
    public String jdbcStr() {
        return "sqlite:";
    }

    @Override
    public String getLastInsertId(String tableName) {
        return "SELECT LAST_INSERT_ROWID();";
    }

}
