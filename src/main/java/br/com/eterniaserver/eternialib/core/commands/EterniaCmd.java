package br.com.eterniaserver.eternialib.core.commands;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.commands.AdvancedCommand;
import br.com.eterniaserver.eternialib.configuration.ReloadableConfiguration;
import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.core.enums.Messages;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

import net.kyori.adventure.text.Component;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandAlias("%eternia")
public class EterniaCmd extends BaseCommand {

    private final EterniaLib plugin;

    public EterniaCmd(EterniaLib plugin) {
        this.plugin = plugin;
    }

    @Default
    @CatchUnknown
    @HelpCommand
    @Syntax("%eternia_syntax")
    @CommandPermission("%eternia_perm")
    @Description("%eternia_description")
    public void onHelp(CommandHelp help) {
        help.showHelp();
    }

    @CommandAlias("%accept")
    @Syntax("%accept_syntax")
    @CommandPermission("%accept_perm")
    @Description("%accept_description")
    public void onAccept(Player player) {
        AdvancedCommand advancedCommand = EterniaLib.getAdvancedCmdManager().getAndRemoveCommand(player.getUniqueId());
        if (advancedCommand == null) {
            Component message = plugin.getComponentMessage(Messages.ACCEPT_NO_COMMAND, true);
            player.sendMessage(message);
            return;
        }

        advancedCommand.execute();
        advancedCommand.executeAsynchronously();

        Component message = plugin.getComponentMessage(Messages.ACCEPTED_COMMAND, true);
        player.sendMessage(message);
    }

    @CommandAlias("%deny")
    @Syntax("%deny_syntax")
    @CommandPermission("%deny_perm")
    @Description("%deny_description")
    public void onDeny(Player player) {
        AdvancedCommand advancedCommand = EterniaLib.getAdvancedCmdManager().getAndRemoveCommand(player.getUniqueId());
        if (advancedCommand == null) {
            Component message = plugin.getComponentMessage(Messages.DENY_NO_COMMAND, true);
            player.sendMessage(message);
            return;
        }

        Component message = plugin.getComponentMessage(Messages.DENIED_COMMAND, true);
        player.sendMessage(message);
    }

    @Subcommand("%eternia_reload")
    @Syntax("%eternia_reload_syntax")
    @CommandPermission("%eternia_reload_perm")
    @Description("%eternia_reload_description")
    @CommandCompletion("@eternia_cmds")
    public void onReload(CommandSender sender, String config) {
        String[] sep = config.split(":");
        String commandEntry = sep[0];
        String commandCheck = sep.length > 1 ? sep[1] : "";

        ReloadableConfiguration reloadableConfiguration = plugin.getConfiguration(commandEntry);

        if (reloadableConfiguration == null) {
            Component message = plugin.getComponentMessage(Messages.CONFIG_INVALID, true, commandEntry);
            sender.sendMessage(message);
            return;
        }

        if (reloadableConfiguration.category() == ConfigurationCategory.BLOCKED) {
            Component message = plugin.getComponentMessage(Messages.CONFIG_BLOCKED, true, commandEntry);
            sender.sendMessage(message);
            return;
        }

        if (reloadableConfiguration.category() == ConfigurationCategory.WARNING_ADVICE && !commandCheck.equals("t")) {
            Component message = plugin.getComponentMessage(Messages.CONFIG_ADVICE, true, commandEntry);
            sender.sendMessage(message);
            return;
        }

        reloadableConfiguration.executeConfig();
        reloadableConfiguration.executeCritical();

        Component message = plugin.getComponentMessage(Messages.CONFIG_RELOADED, true, commandEntry);
        sender.sendMessage(message);
    }

}