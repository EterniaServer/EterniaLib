package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.core.interfaces.Query;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handlers the SQL execution and connection of Eternia's plugins.
 */
public final class SQL {

    private static EterniaLib plugin;
    private static Connection connection;

    /**
     * Static class should not be initialized.
     */
    private SQL() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns a new {@link Connection} without having to worry about it be SQLite or MariaDB. If the system doesn't
     * have sqlite it will return null.
     *
     * @return the {@link Connection}
     */
    public static Connection getConnection() throws SQLException {

        if (EterniaLib.mySQL) {
            return EterniaLib.hikari.getConnection();
        }

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + Constants.DATABASE_FILE_PATH);
        }

        return connection;

    }

    /**
     * Executes the informed {@link Query} in asynchronous mode.
     *
     * @param query is the {@link Query} object
     */
    public static void executeAsync(final Query query) {

        if (!EterniaLib.mySQL) {
            execute(query);
            return;
        }

        if (plugin == null) {
            plugin = (EterniaLib) Bukkit.getPluginManager().getPlugin("EterniaLib");
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> execute(query));

    }

    /**
     * Executes the informed {@link Query} in synchronous mode.
     *
     * @param query is the {@link Query} object
     */
    public static void execute(final Query query) {

        if (!EterniaLib.mySQL) {
            try (final var preparedStatement = getConnection().prepareStatement(query.queryString())) {
                preparedStatement.execute();
            } catch (SQLException exception) {
                Bukkit.getLogger().warning("Can't connect to SQLite: " + exception.getClass().getName());
            }
            return;
        }

        try (final var connection = getConnection();
             final var preparedStatement = connection.prepareStatement(query.queryString())) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            Bukkit.getLogger().warning("Can't connect to MySQL: " + exception.getClass().getName());
        }

    }

}
