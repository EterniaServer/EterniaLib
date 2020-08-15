package br.com.eterniaserver.eternialib;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.eterniaserver.eternialib.sql.Connections;

public class EQueries {

    public static List<String> queryStringList(final String query, final String value) {
        if (EterniaLib.mysql) {
            final List<String> accounts = new ArrayList<>();
            EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
                PreparedStatement getbaltop = connection.prepareStatement(query);
                ResultSet resultSet = getbaltop.executeQuery();
                while (resultSet.next()) {
                    accounts.add(resultSet.getString(value));
                }
                resultSet.close();
                getbaltop.close();
            });
            return accounts;
        }
        final List<String> result = new ArrayList<>();
        try (PreparedStatement statement = Connections.connection.prepareStatement(query); ResultSet resultSet2 = statement.executeQuery()) {
            while (resultSet2.next()) {
                result.add(resultSet2.getString(value));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void executeQuery(final String query) {
        if (EterniaLib.mysql) {
            EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.execute();
                statement.close();
            }, true);
        } else {
            try (PreparedStatement statement = Connections.connection.prepareStatement(query)) {
                statement.execute();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void executeQuery(final String query, final boolean async) {
        if (EterniaLib.mysql) {
            EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.execute();
                statement.close();
            }, async);
        } else {
            try (PreparedStatement statement = Connections.connection.prepareStatement(query)) {
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static HashMap<String, String> getMapString(final String query, final String value, final String value2) {
        if (EterniaLib.mysql) {
            final AtomicReference<HashMap<String, String>> map = new AtomicReference<>(new HashMap<>());
            EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
                PreparedStatement getHashMap = connection.prepareStatement(query);
                ResultSet resultSet = getHashMap.executeQuery();
                while (resultSet.next()) {
                    map.get().put(resultSet.getString(value), resultSet.getString(value2));
                }
                getHashMap.close();
                resultSet.close();
            });
            return map.get();
        } else {
            final HashMap<String, String> map2 = new HashMap<>();
            try (PreparedStatement statement = Connections.connection.prepareStatement(query); ResultSet resultSet2 = statement.executeQuery()) {
                while (resultSet2.next()) {
                    map2.put(resultSet2.getString(value), resultSet2.getString(value2));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map2;
        }
    }

}