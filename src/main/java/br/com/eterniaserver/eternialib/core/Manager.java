package br.com.eterniaserver.eternialib.core;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.commands.Eternia;
import br.com.eterniaserver.eternialib.core.entities.PlayerUUID;
import br.com.eterniaserver.eternialib.core.handlers.PlayerHandler;
import br.com.eterniaserver.eternialib.database.Entity;
import br.com.eterniaserver.eternialib.database.exceptions.DatabaseException;
import br.com.eterniaserver.eternialib.database.exceptions.EntityException;

import java.util.List;

public class Manager {

    private final EterniaLib plugin;

    public Manager(EterniaLib plugin) {
        this.plugin = plugin;

        registerEntities();
        loadCompletions();
        registerCommands();
    }

    private void registerEntities() {
        try{
            Entity<PlayerUUID> entity = new Entity<>(PlayerUUID.class);
            EterniaLib.getDatabase().register(PlayerUUID.class, entity);
        }
        catch (EntityException | DatabaseException exception) {
            // Todo alert registration error
        }

        List<PlayerUUID> playerUUIDList = EterniaLib.getDatabase().listAll(PlayerUUID.class);
        playerUUIDList.forEach(playerUUID -> EterniaLib.registerNewUUID(playerUUID.playerName, playerUUID.uuid));

        plugin.getServer().getPluginManager().registerEvents(new PlayerHandler(plugin), plugin);
    }

    private void loadCompletions() {
        EterniaLib.getCmdManager().getCommandCompletions().registerCompletion("eternia_cmds", completionContext -> plugin.getConfigurations());
    }

    private void registerCommands() {
        EterniaLib.getCmdManager().registerCommand(new Eternia(plugin));
    }

}
