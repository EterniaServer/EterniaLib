package br.com.eterniaserver.eternialib.core;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.commands.EterniaCmd;
import br.com.eterniaserver.eternialib.core.commands.EterniaLogs;
import br.com.eterniaserver.eternialib.core.entities.PlayerUUID;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.core.handlers.PlayerHandler;
import br.com.eterniaserver.eternialib.database.Entity;

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
        try {
            Entity<PlayerUUID> entity = new Entity<>(PlayerUUID.class);
            EterniaLib.addTableName("%eternia_lib_player_uuid%", plugin.getStrings().get(Strings.PLAYER_UUID_TABLE_NAME));
            EterniaLib.getDatabase().register(PlayerUUID.class, entity);
        }
        catch (Exception exception) {
            EterniaLib.registerLog("EE-301-Manager.java");
            plugin.getLogger().severe("Error while registering entities: " + exception.getMessage());
        }

        List<PlayerUUID> playerUUIDList = EterniaLib.getDatabase().listAll(PlayerUUID.class);
        playerUUIDList.forEach(playerUUID -> EterniaLib.registerNewUUID(playerUUID.getPlayerName(), playerUUID.getUuid()));

        plugin.getServer().getPluginManager().registerEvents(new PlayerHandler(plugin), plugin);
    }

    private void loadCompletions() {
        EterniaLib.getCmdManager().getCommandCompletions().registerCompletion("eternia_cmds", completionContext -> EterniaLib.getCfgManager().getConfigurations());
    }

    private void registerCommands() {
        EterniaLib.getCmdManager().registerCommand(new EterniaCmd());
        EterniaLib.getCmdManager().registerCommand(new EterniaLogs(plugin));
    }

}
