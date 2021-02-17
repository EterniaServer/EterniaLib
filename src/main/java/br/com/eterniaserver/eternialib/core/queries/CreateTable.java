package br.com.eterniaserver.eternialib.core.queries;

import br.com.eterniaserver.eternialib.core.interfaces.Query;
import br.com.eterniaserver.eternialib.core.baseobjects.Columns;

public class CreateTable implements Query {

    private final String table;

    public Columns columns = new Columns();

    public CreateTable(String table) {
        this.table = table;
    }

    @Override
    public String queryString() {
        return "CREATE TABLE IF NOT EXISTS " + table + " " + columns.get() + ";";
    }

}
