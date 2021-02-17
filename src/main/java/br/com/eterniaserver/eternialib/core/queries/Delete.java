package br.com.eterniaserver.eternialib.core.queries;

import br.com.eterniaserver.eternialib.core.interfaces.Query;
import br.com.eterniaserver.eternialib.core.baseobjects.Cells;

public class Delete implements Query {

    private final String table;

    public Cells where = new Cells();

    public Delete(String table) {
        this.table = table;
    }

    @Override
    public String queryString() {
        return "DELETE FROM " + table + " WHERE " + where.get() + ";";
    }

}
