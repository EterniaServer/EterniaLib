package br.com.eterniaserver.eternialib;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.eterniaserver.eternialib.sql.Connections;

public class EQueries {

    public static boolean queryBoolean(final String query, final String value) {
        if (EterniaLib.mysql) {
            final AtomicBoolean result = new AtomicBoolean(false);
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
        boolean result2 = false;
        try {
            final PreparedStatement statement2 = Connections.connection.prepareStatement(query);
            final ResultSet resultSet2 = statement2.executeQuery();
            if (resultSet2.next() && resultSet2.getString(value) != null) {
                result2 = true;
            }
            resultSet2.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result2;
    }

    public static double queryDouble(final String query, final String value) {
        return queryDouble(query, value, value);
    }

    public static double queryDouble(final String query, final String value, final String value2) {
        if (EterniaLib.mysql) {
            final AtomicReference<Double> result = new AtomicReference<>(0.0);
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
        double result2 = 0.0;
        try {
            final PreparedStatement statement2 = Connections.connection.prepareStatement(query);
            final ResultSet resultSet2 = statement2.executeQuery();
            if (resultSet2.next() && resultSet2.getString(value) != null) {
                result2 = resultSet2.getDouble(value2);
            }
            resultSet2.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result2;
    }

    public static double queryFloat(final String query, final String value) {
        return queryFloat(query, value, value);
    }

    public static double queryFloat(final String query, final String value, final String value2) {
        if (EterniaLib.mysql) {
            final AtomicReference<Float> result = new AtomicReference<>(0.0f);
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
        float result2 = 0.0f;
        try {
            final PreparedStatement statement2 = Connections.connection.prepareStatement(query);
            final ResultSet resultSet2 = statement2.executeQuery();
            if (resultSet2.next() && resultSet2.getString(value) != null) {
                result2 = resultSet2.getFloat(value2);
            }
            resultSet2.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result2;
    }

    public static int queryInteger(final String query, final String value) {
        return queryInteger(query, value, value);
    }

    public static int queryInteger(final String query, final String value, final String value2) {
        if (EterniaLib.mysql) {
            final AtomicReference<Integer> result = new AtomicReference<>(0);
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
        int result2 = 0;
        try {
            final PreparedStatement statement2 = Connections.connection.prepareStatement(query);
            final ResultSet resultSet2 = statement2.executeQuery();
            if (resultSet2.next() && resultSet2.getString(value) != null) {
                result2 = resultSet2.getInt(value2);
            }
            resultSet2.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result2;
    }

    public static String queryString(final String query, final String value) {
        return queryString(query, value, value);
    }

    public static String queryString(final String query, final String value, final String value2) {
        if (EterniaLib.mysql) {
            final AtomicReference<String> result = new AtomicReference<>("");
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
        String result2 = "";
        try {
            final PreparedStatement statement2 = Connections.connection.prepareStatement(query);
            final ResultSet resultSet2 = statement2.executeQuery();
            if (resultSet2.next() && resultSet2.getString(value) != null) {
                result2 = resultSet2.getString(value2);
            }
            resultSet2.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result2;
    }

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
        try {
            final PreparedStatement statement = Connections.connection.prepareStatement(query);
            final ResultSet resultSet2 = statement.executeQuery();
            while (resultSet2.next()) {
                result.add(resultSet2.getString(value));
            }
            resultSet2.close();
            statement.close();
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
            try {
                final PreparedStatement statement2 = Connections.connection.prepareStatement(query);
                statement2.execute();
                statement2.close();
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
            try {
                final PreparedStatement statement2 = Connections.connection.prepareStatement(query);
                statement2.execute();
                statement2.close();
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
            });
            return map.get();
        } else {
            final HashMap<String, String> map2 = new HashMap<>();
            try {
                final PreparedStatement statement = Connections.connection.prepareStatement(query);
                final ResultSet resultSet2 = statement.executeQuery();
                while (resultSet2.next()) {
                    map2.put(resultSet2.getString(value), resultSet2.getString(value2));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map2;
        }
    }

    public static HashMap<byte[], String> getMapBytesString(final String query, final String value, final String value2) {
        if (EterniaLib.mysql) {
            final AtomicReference<HashMap<byte[], String>> map = new AtomicReference<>(new HashMap<>());
            EterniaLib.getPlugin().connections.executeSQLQuery(connection -> {
                PreparedStatement getHashMap = connection.prepareStatement(query);
                ResultSet resultSet = getHashMap.executeQuery();
                while (resultSet.next()) {
                    map.get().put(resultSet.getBytes(value), resultSet.getString(value2));
                }
            });
            return map.get();
        } else {
            final HashMap<byte[], String> map2 = new HashMap<>();
            try {
                final PreparedStatement statement = Connections.connection.prepareStatement(query);
                final ResultSet resultSet2 = statement.executeQuery();
                while (resultSet2.next()) {
                    map2.put(resultSet2.getBytes(value), resultSet2.getString(value2));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map2;
        }
    }

}