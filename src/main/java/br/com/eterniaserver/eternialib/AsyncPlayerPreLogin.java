package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.sql.queries.Insert;
import br.com.eterniaserver.eternialib.sql.queries.Select;
import br.com.eterniaserver.eternialib.sql.queries.Update;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AsyncPlayerPreLogin implements Listener {

    public AsyncPlayerPreLogin() {

        try {
            PreparedStatement statement = SQL.getConnection().prepareStatement(new Select("el_cache").queryString());
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
            Bukkit.getConsoleSender().sendMessage(EterniaLib.configs.msgLoadCache.replace("{0}", String.valueOf(UUIDFetcher.lookupNameCache.size())));
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(EterniaLib.configs.msgError);
            e.printStackTrace();
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

        String playerName = event.getName();
        UUID uuid = UUIDFetcher.getUUIDOf(playerName);

        if (!UUIDFetcher.lookupNameCache.containsKey(uuid)) {
            UUIDFetcher.lookupCache.put(playerName, uuid);
            UUIDFetcher.lookupNameCache.put(uuid, playerName);
            Insert insert = new Insert("el_cache");
            insert.columns.set("uuid", "player_name");
            insert.values.set(uuid.toString(), playerName);
            SQL.execute(insert);
            return;
        }

        if (!UUIDFetcher.lookupNameCache.get(uuid).equals(playerName)) {
            UUIDFetcher.lookupCache.put(playerName, uuid);
            UUIDFetcher.lookupNameCache.put(uuid, playerName);
            Update update = new Update("el_cache");
            update.set.set("player_name", playerName);
            update.where.set("uuid", uuid.toString());
            SQL.execute(update);
        }

    }

}
