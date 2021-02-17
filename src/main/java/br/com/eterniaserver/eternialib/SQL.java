package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.core.interfaces.Query;

import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
    @Nullable
    public static Connection getConnection() throws SQLException {

        if (EterniaLib.MySQL) {
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
    public static void executeAsync(@Nonnull final Query query) {

        if (!EterniaLib.MySQL) {
            execute(query);
            return;
        }

        if (plugin == null) {
            plugin = (EterniaLib) Bukkit.getPluginManager().getPlugin("EterniaLib");
        }

        Bukkit.getScheduler().runTask(plugin, () -> execute(query));

    }

    /**
     * Executes the informed {@link Query} in synchronous mode.
     *
     * @param query is the {@link Query} object
     */
    public static void execute(@Nonnull final Query query) {

        if (!EterniaLib.MySQL) {
            try (final PreparedStatement preparedStatement = getConnection().prepareStatement(query.queryString())) {
                preparedStatement.execute();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return;
        }

        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(query.queryString());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

    }

}
