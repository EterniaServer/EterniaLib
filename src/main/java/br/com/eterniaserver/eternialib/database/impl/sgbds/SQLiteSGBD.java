package br.com.eterniaserver.eternialib.database.impl.sgbds;

public class SQLiteSGBD extends MySQLSGBD {

    public SQLiteSGBD() {
        super();
    }

    @Override
    public String jdbcStr(String... args) {
        if (args.length < 1) {
            return null;
        }
        return "jdbc:sqlite:%s".formatted(args[0]);
    }

    @Override
    public String getLastInsertId(String tableName) {
        return "SELECT LAST_INSERT_ROWID();";
    }

}
