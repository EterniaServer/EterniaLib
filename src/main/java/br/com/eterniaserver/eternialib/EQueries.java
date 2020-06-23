package br.com.eterniaserver.eternialib;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class EQueries {

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

    public static double queryFloat(final String query, final String value) {
        return queryFloat(query, value, value);
    }

    public static double queryFloat(final String query, final String value, final String value2) {
        AtomicReference<Float> result = new AtomicReference<>((float) 0);
        EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getString(value) != null) {
                result.set(resultSet.getFloat(value2));
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

    public static HashMap<String, Location> getMap(final String query, final String value, final String value2) {
        AtomicReference<HashMap<String, Location>> map = new AtomicReference<>();
        EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
            final HashMap<String, Location> tempMap = new HashMap<>();
            PreparedStatement getHashMap = connection.prepareStatement(query);
            ResultSet resultSet = getHashMap.executeQuery();
            String[] values;
            while (resultSet.next()) {
                values = resultSet.getString(value2).split(":");
                tempMap.put(resultSet.getString(value), new Location(Bukkit.getWorld(values[0]),
                        Double.parseDouble(values[1]),
                        (Double.parseDouble(values[2]) + 1),
                        Double.parseDouble(values[3]),
                        Float.parseFloat(values[4]),
                        Float.parseFloat(values[5])));
            }
            map.set(tempMap);
        });
        return map.get();
    }

}
