package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.interfaces.Query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQL {

    private SQL() {
        throw new IllegalStateException("Utility class");
    }

    public static Connection getConnection() throws SQLException {
        if (EterniaLib.getMySQL()) {
            return EterniaLib.hikari.getConnection();
        } else {
            try {
                Class.forName("org.sqlite.JDBC");
                return DriverManager.getConnection("jdbc:sqlite:" + EterniaLib.dataFolder);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static void executeAsync(Query query) {
        if (EterniaLib.getMySQL()) {
            EterniaLib.runAsync(() -> execute(query));
        } else {
            execute(query);
        }
    }

    public static void execute(Query query) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query.queryString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException error) {
            error.printStackTrace();
        }

    }
}
