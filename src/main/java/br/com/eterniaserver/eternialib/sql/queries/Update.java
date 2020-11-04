package br.com.eterniaserver.eternialib.sql.queries;

import br.com.eterniaserver.eternialib.interfaces.Query;
import br.com.eterniaserver.eternialib.sql.objects.Cells;

public class Update implements Query {

    private final String table;

    public Cells set = new Cells();
    public Cells where = new Cells();

    public Update(String table) {
        this.table = table;
    }

    @Override
    public String queryString() {
        return "UPDATE " + table + " SET " + set.get() + " WHERE " + where.get();
    }

}
