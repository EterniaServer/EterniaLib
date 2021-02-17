package br.com.eterniaserver.eternialib.handlers;

import br.com.eterniaserver.eternialib.Constants;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.SQL;
import br.com.eterniaserver.eternialib.UUIDFetcher;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import br.com.eterniaserver.eternialib.core.queries.Insert;
import br.com.eterniaserver.eternialib.core.queries.Select;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AsyncPlayerPreLoginHandler implements Listener {

    private final EterniaLib plugin;

    public AsyncPlayerPreLoginHandler(final EterniaLib plugin) {
        this.plugin = plugin;

        try (Connection connection = SQL.getConnection()) {
            if (connection == null) {
                EterniaLib.report("$8[$aE$9L$8] $7A conecção com a database está fechada$8.".replace('$', (char) 0x00A7));
                return;
            }

            final PreparedStatement statement = connection.prepareStatement(new Select("el_cache").queryString());
            final ResultSet resultSet = statement.executeQuery();
            int size = 0;

            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String playerName = resultSet.getString("player_name");
                plugin.registerNewUUID(playerName, uuid);
                size++;
            }

            resultSet.close();
            statement.close();
            EterniaLib.report(plugin.getMessage(Messages.LOAD_CACHE, String.valueOf(size)));
        } catch (SQLException e) {
            EterniaLib.report(plugin.getMessage(Messages.ERROR));
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {

        if (UUIDFetcher.getUUIDOf(event.getName()) != null) {
            return;
        }

        final Insert insert = new Insert(Constants.TABLE_CACHE);
        final String playerName = event.getName();
        final UUID uuid = event.getUniqueId();

        plugin.registerNewUUID(playerName, uuid);
        insert.columns.set(Constants.UUID_STR, Constants.PLAYER_NAME_STR);
        insert.values.set(uuid.toString(), playerName);
        SQL.execute(insert);

    }

}
