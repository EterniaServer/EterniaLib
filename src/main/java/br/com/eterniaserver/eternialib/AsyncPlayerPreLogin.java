package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.sql.queries.Insert;
import br.com.eterniaserver.eternialib.sql.queries.Select;
import br.com.eterniaserver.eternialib.sql.queries.Update;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AsyncPlayerPreLogin implements Listener {

    public AsyncPlayerPreLogin() {
        try (Connection connection = SQL.getConnection()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(new Select("el_cache").queryString());
                statement.execute();
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    String playerName = resultSet.getString("player_name");
                    UUIDFetcher.lookupCache.put(playerName, uuid);
                    UUIDFetcher.lookupNameCache.put(uuid, playerName);
                }
                resultSet.close();
                statement.close();
                EterniaLib.report(EterniaLib.configs.msgLoadCache.replace("{0}", String.valueOf(UUIDFetcher.lookupNameCache.size())));
            } else {
                EterniaLib.report("$8[$aE$9L$8] $7A conecção com a database está fechada$8.".replace('$', (char) 0x00A7));
            }
        } catch (SQLException e) {
            EterniaLib.report(EterniaLib.configs.msgError);
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {

        String playerName = event.getPlayer().getName();
        UUID uuid = UUIDFetcher.getUUIDOfWithoutSave(playerName);

        if (!UUIDFetcher.lookupNameCache.containsKey(uuid)) {
            UUIDFetcher.lookupCache.put(playerName, uuid);
            UUIDFetcher.lookupNameCache.put(uuid, playerName);
            Insert insert = new Insert("el_cache");
            insert.columns.set("uuid", "player_name");
            insert.values.set(uuid.toString(), playerName);
            SQL.executeAsync(insert);
            return;
        }


        if (!UUIDFetcher.lookupNameCache.get(uuid).equals(playerName)) {
            UUIDFetcher.lookupCache.put(playerName, uuid);
            UUIDFetcher.lookupNameCache.put(uuid, playerName);
            Update update = new Update("el_cache");
            update.set.set("player_name", playerName);
            update.where.set("uuid", uuid.toString());
            SQL.executeAsync(update);
        }

    }

}
