package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.interfaces.Query;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class SQL {

    private SQL() {
        throw new IllegalStateException("Utility class");
    }

    public static CachedRowSet getRowSet(Query query) {
        CachedRowSet cachedRowSet = null;

        try (PreparedStatement preparedStatement = EterniaLib.connections.getConnection().prepareStatement(query.queryString()); ResultSet resultSet = preparedStatement.getResultSet()) {
            cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
            cachedRowSet.populate(resultSet);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return cachedRowSet;
    }

    public static void executeAsync(Query query) {
        if (EterniaLib.mysql) {
            CompletableFuture.runAsync(() -> execute(query));
            return;
        }
        execute(query);
    }

    public static void execute(Query query) {
        try (PreparedStatement preparedStatement = EterniaLib.connections.getConnection().prepareStatement(query.queryString())) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }


}
