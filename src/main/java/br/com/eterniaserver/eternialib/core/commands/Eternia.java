package br.com.eterniaserver.eternialib.core.commands;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import br.com.eterniaserver.eternialib.core.interfaces.ReloadableConfiguration;

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

@CommandAlias("%eternia")
public class Eternia extends BaseCommand {

    private final EterniaLib plugin;

    public Eternia(final EterniaLib plugin) {
        this.plugin = plugin;
    }

    @Default
    @CatchUnknown
    @HelpCommand
    @Syntax("%eternia_syntax")
    @CommandPermission("%eternia_perm")
    @Description("%eternia_description")
    public void onHelp(final CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("%eternia_reload")
    @Syntax("%eternia_reload_syntax")
    @CommandPermission("%eternia_reload_perm")
    @CommandCompletion("@eternia_cmds")
    @Description("%eternia_reload_description")
    public void onReload(final CommandSender sender, final String config) {

        final String[] sep = config.split(":");
        final ReloadableConfiguration reloadableConfiguration = plugin.getReloadableConfiguration(sep[0].hashCode());

        if (reloadableConfiguration == null) {
            plugin.sendMessage(sender, Messages.CONFIG_INVALID);
            return;
        }

        if ((reloadableConfiguration.category() == ConfigurationCategory.WARNING_ADVICE && sep.length == 1)
                || reloadableConfiguration.category() == ConfigurationCategory.WARNING_ADVICE && !sep[1].equals("t")) {
            plugin.sendMessage(sender, Messages.CONFIG_WARNING);
            return;
        }

        reloadableConfiguration.executeConfig();
        plugin.sendMessage(sender, Messages.CONFIG_RELOADED, sep[0]);

    }

}
