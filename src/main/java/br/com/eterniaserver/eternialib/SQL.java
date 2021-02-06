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
        }

        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + EterniaLib.dataFolder);
        } catch (ClassNotFoundException e) {
            EterniaLib.report("$8[$aE$9L$8] $7Classe SQLite não encontrada no sistema$8.".replace('$', (char) 0x00A7));
            e.printStackTrace();
            return null;
        }
    }

    public static void executeAsync(Query query) {
        if (EterniaLib.getMySQL()) {
            EterniaLib.runAsync(() -> execute(query));
            return;
        }

        execute(query);
    }

    public static void execute(Query query) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query.queryString());
                preparedStatement.execute();
                preparedStatement.close();
            } else {
                EterniaLib.report("$8[$aE$9L$8] $7A conecção com a database está fechada$8.".replace('$', (char) 0x00A7));
            }
        } catch (SQLException error) {
            EterniaLib.report("$8[$aE$9L$8] $7Erro ao executar querry$8.".replace('$', (char) 0x00A7));
            error.printStackTrace();
        }

    }
}
