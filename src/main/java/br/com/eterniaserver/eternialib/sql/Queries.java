package br.com.eterniaserver.eternialib.sql;

import br.com.eterniaserver.eternialib.EterniaLib;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Queries {

    public static boolean queryBoolean(final String query, final String value) {
        AtomicBoolean result = new AtomicBoolean(false);

        EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getString(value) != null) {
                result.set(true);
            }
            resultSet.close();
            statement.close();
        });

        return result.get();
    }

    public static double queryDouble(final String query, final String value) {
        return queryDouble(query, value, value);
    }

    public static double queryDouble(final String query, final String value, final String value2) {
        AtomicReference<Double> result = new AtomicReference<>(0.0);
        EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getString(value) != null) {
                result.set(resultSet.getDouble(value2));
            }
            resultSet.close();
            statement.close();
        });
        return result.get();
    }

    public static int queryInteger(final String query, final String value) {
        return queryInteger(query, value, value);
    }

    public static int queryInteger(final String query, final String value, final String value2) {
        AtomicReference<Integer> result = new AtomicReference<>(0);

        EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getString(value) != null) {
                result.set(resultSet.getInt(value2));
            }
            resultSet.close();
            statement.close();
        });

        return result.get();
    }

    public static String queryString(final String query, final String value) {
        return queryString(query, value, value);
    }

    public static String queryString(final String query, final String value, final String value2) {
        AtomicReference<String> result = new AtomicReference<>("");
        EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getString(value) != null) {
                result.set(resultSet.getString(value2));
            }
            resultSet.close();
            statement.close();
        });
        return result.get();
    }

    public static List<String> queryStringList(final String query, final String value) {
        List<String> accounts = new ArrayList<>();
        EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
            PreparedStatement getbaltop = connection.prepareStatement(query);
            ResultSet resultSet = getbaltop.executeQuery();
            while (resultSet.next()) {
                final String warpname = resultSet.getString(value);
                accounts.add(warpname);
            }
            resultSet.close();
            getbaltop.close();
        });
        return accounts;
    }

    public static void executeQuery(final String query) {
        EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.execute();
            statement.close();
        }, true);
    }


}
