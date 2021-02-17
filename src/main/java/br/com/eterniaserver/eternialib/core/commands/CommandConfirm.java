package br.com.eterniaserver.eternialib.core.commands;

import br.com.eterniaserver.eternialib.CmdConfirmationManager;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import br.com.eterniaserver.eternialib.core.interfaces.CommandConfirmable;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;

import org.bukkit.entity.Player;

@CommandAlias("%command")
public class CommandConfirm extends BaseCommand {

    private final EterniaLib plugin;

    public CommandConfirm(final EterniaLib plugin) {
        this.plugin = plugin;
    }

    @Default
    @HelpCommand
    @Syntax("%command_syntax")
    @Description("%command_description")
    @CommandPermission("%command_perm")
    public void onCommandHelp(final CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("%command_accept")
    @Syntax("%command_accept_syntax")
    @Description("%command_accept_description")
    @CommandPermission("%command_accept_perm")
    public void onAccept(final Player player) {
        CommandConfirmable commandConfirmable = CmdConfirmationManager.getAndRemoveCommand(player.getUniqueId());

        if (commandConfirmable == null) {
            plugin.sendMessage(player, Messages.COMMAND_INVALID);
            return;
        }

        commandConfirmable.execute();
    }

    @Subcommand("%command_deny")
    @Syntax("%command_deny_syntax")
    @Description("%command_deny_description")
    @CommandPermission("%command_deny_perm")
    public void onDeny(final Player player) {
        CommandConfirmable commandConfirmable = CmdConfirmationManager.getAndRemoveCommand(player.getUniqueId());

        if (commandConfirmable == null) {
            plugin.sendMessage(player, Messages.COMMAND_INVALID);
            return;
        }

        plugin.sendMessage(player, Messages.COMMAND_DENIED);
    }

}