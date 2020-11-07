package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.interfaces.Query;
import org.bukkit.Bukkit;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class SQL {

    private SQL() {
        throw new IllegalStateException("Utility class");
    }

    public static Connection getConnection() throws SQLException {
        return EterniaLib.getMySQL() ? Connections.hikari.getConnection() : Connections.connection;
    }

    public static void executeAsync(Query query) {
        if (EterniaLib.mysql) {
            EterniaLib.runAsync(() -> execute(query));
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
