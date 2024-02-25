package br.com.eterniaserver.eternialib.core.commands;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.chat.MessageOptions;
import br.com.eterniaserver.eternialib.commands.AdvancedCommand;
import br.com.eterniaserver.eternialib.configuration.interfaces.ReloadableConfiguration;
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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandAlias("%eternia")
public class EterniaCmd extends BaseCommand {

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
            EterniaLib.getChatCommons().sendMessage(player, Messages.ACCEPT_NO_COMMAND);
            return;
        }

        advancedCommand.execute();
        advancedCommand.executeAsynchronously();

        EterniaLib.getChatCommons().sendMessage(player, Messages.ACCEPTED_COMMAND);
    }

    @CommandAlias("%deny")
    @Syntax("%deny_syntax")
    @CommandPermission("%deny_perm")
    @Description("%deny_description")
    public void onDeny(Player player) {
        AdvancedCommand advancedCommand = EterniaLib.getAdvancedCmdManager().getAndRemoveCommand(player.getUniqueId());
        if (advancedCommand == null) {
            EterniaLib.getChatCommons().sendMessage(player, Messages.DENY_NO_COMMAND);
            return;
        }

        EterniaLib.getChatCommons().sendMessage(player, Messages.DENIED_COMMAND);
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

        ReloadableConfiguration reloadableConfiguration = EterniaLib.getCfgManager().getConfiguration(commandEntry);

        if (reloadableConfiguration == null) {
            EterniaLib.getChatCommons().sendMessage(sender, Messages.CONFIG_INVALID);
            return;
        }

        if (reloadableConfiguration.category() == ConfigurationCategory.BLOCKED) {
            EterniaLib.getChatCommons().sendMessage(sender, Messages.CONFIG_BLOCKED, new MessageOptions(commandEntry));
            return;
        }

        if (reloadableConfiguration.category() == ConfigurationCategory.WARNING_ADVICE && !commandCheck.equals("t")) {
            EterniaLib.getChatCommons().sendMessage(sender, Messages.CONFIG_ADVICE, new MessageOptions(commandEntry));
            return;
        }

        reloadableConfiguration.executeConfig();
        reloadableConfiguration.executeCritical();

        EterniaLib.getChatCommons().sendMessage(sender, Messages.CONFIG_RELOADED, new MessageOptions(commandEntry));
    }

}