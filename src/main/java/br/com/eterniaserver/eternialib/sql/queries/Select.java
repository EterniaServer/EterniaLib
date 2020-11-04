package br.com.eterniaserver.eternialib.sql.queries;

import br.com.eterniaserver.eternialib.interfaces.Query;

public class Select implements Query {

    final String table;

    public Select(String table) {
        this.table = table;
    }

    @Override
    public String queryString() {
        return "SELECT * FROM " + table;
    }
}
