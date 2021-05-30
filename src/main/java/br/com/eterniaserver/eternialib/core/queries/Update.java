package br.com.eterniaserver.eternialib.core.queries;

import br.com.eterniaserver.eternialib.core.interfaces.Query;
import br.com.eterniaserver.eternialib.core.baseobjects.Cells;

public class Update implements Query {

    private final String table;

    public final Cells set = new Cells();
    public final Cells where = new Cells();

    public Update(String table) {
        this.table = table;
    }

    @Override
    public String queryString() {
        return "UPDATE " + table + " SET " + set.get() + " WHERE " + where.get() + ";";
    }

}
