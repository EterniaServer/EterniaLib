package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.interfaces.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class SQL {

    private SQL() {
        throw new IllegalStateException("Utility class");
    }

    public static Connection getConnection() throws SQLException {
        return EterniaLib.getMySQL() ? EterniaLib.hikari.getConnection() : EterniaLib.connection;
    }

    public static void executeAsync(Query query) {
        if (EterniaLib.getMySQL()) {
            CompletableFuture.runAsync(() -> execute(query));
        } else {
            execute(query);
        }
    }

    public static void execute(Query query) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(query.queryString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException error) {
            error.printStackTrace();
        }

    }
}
