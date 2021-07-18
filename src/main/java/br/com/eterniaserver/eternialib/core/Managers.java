package br.com.eterniaserver.eternialib.core;

import br.com.eterniaserver.eternialib.CommandManager;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.commands.CommandConfirm;
import br.com.eterniaserver.eternialib.core.commands.Eternia;
import br.com.eterniaserver.eternialib.core.configurations.CommandsLocaleCfg;
import br.com.eterniaserver.eternialib.core.enums.Commands;

public class Managers {

    private final EterniaLib plugin;

    public Managers(EterniaLib plugin) {
        this.plugin = plugin;

        loadCommandsLocale();
        loadCompletions();
        registerCommands();
    }

    private void loadCommandsLocale() {
        CommandsLocaleCfg cmdsLocale = new CommandsLocaleCfg();

        for (Commands command : Commands.values()) {
            CommandManager.getCommandReplacements().addReplacements(
                    command.name().toLowerCase(), cmdsLocale.getName(command),
                    command.name().toLowerCase() + "_description", cmdsLocale.getDescription(command),
                    command.name().toLowerCase() + "_perm", cmdsLocale.getPerm(command),
                    command.name().toLowerCase() + "_syntax", cmdsLocale.getSyntax(command)
            );
        }
    }

    private void loadCompletions() {
        CommandManager.getCommandCompletions().registerCompletion("eternia_cmds", shop -> plugin.getReloadableConfigList());
    }

    private void registerCommands() {
        CommandManager.registerCommand(new Eternia(plugin));
        CommandManager.registerCommand(new CommandConfirm(plugin));
    }

}