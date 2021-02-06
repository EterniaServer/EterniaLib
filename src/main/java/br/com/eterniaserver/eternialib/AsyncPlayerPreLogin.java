package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.sql.queries.Insert;
import br.com.eterniaserver.eternialib.sql.queries.Select;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AsyncPlayerPreLogin implements Listener {

    private static final String TABLE_CACHE = "el_cache";
    protected static final String UUID_STR = "uuid";
    protected static final String PLAYER_NAME_STR = "player_name";

    public AsyncPlayerPreLogin() {
        try (Connection connection = SQL.getConnection()) {
            if (connection == null) {
                EterniaLib.report("$8[$aE$9L$8] $7A conecção com a database está fechada$8.".replace('$', (char) 0x00A7));
                return;
            }

            final PreparedStatement statement = connection.prepareStatement(new Select("el_cache").queryString());
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String playerName = resultSet.getString("player_name");
                UUIDFetcher.lookupCache.put(playerName, uuid);
                UUIDFetcher.lookupNameCache.put(uuid, playerName);
            }

            resultSet.close();
            statement.close();
            EterniaLib.report(EterniaLib.configs.msgLoadCache.replace("{0}", String.valueOf(UUIDFetcher.lookupNameCache.size())));
        } catch (SQLException e) {
            EterniaLib.report(EterniaLib.configs.msgError);
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        final String playerName = event.getName();

        if (UUIDFetcher.lookupCache.containsKey(playerName)) {
            return;
        }

        final UUID uuid = event.getUniqueId();

        UUIDFetcher.lookupCache.put(playerName, uuid);
        UUIDFetcher.lookupNameCache.put(uuid, playerName);

        final Insert insert = new Insert(TABLE_CACHE);
        insert.columns.set(UUID_STR, PLAYER_NAME_STR);
        insert.values.set(uuid.toString(), playerName);
        SQL.execute(insert);

    }

}
