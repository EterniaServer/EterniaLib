package br.com.eterniaserver.eternialib.sql.queries;

import br.com.eterniaserver.eternialib.interfaces.Query;
import br.com.eterniaserver.eternialib.sql.objects.Columns;
import br.com.eterniaserver.eternialib.sql.objects.Values;

public class Insert implements Query {

    private final String table;

    public Columns columns = new Columns();
    public Values values = new Values();

    public Insert(String table) {
        this.table = table;
    }

    @Override
    public String queryString() {
        return "INSERT INTO " + table + " " + columns.get() + " VALUES " + values.get() + ";";
    }
}
