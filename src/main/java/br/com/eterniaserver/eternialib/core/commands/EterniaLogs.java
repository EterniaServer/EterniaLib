package br.com.eterniaserver.eternialib.core.commands;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.chat.MessageOptions;
import br.com.eterniaserver.eternialib.core.enums.Messages;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;

import org.bukkit.command.CommandSender;

import java.util.List;

@CommandAlias("%eternia_logs")
public class EterniaLogs extends BaseCommand {

    private final EterniaLib plugin;

    public EterniaLogs(EterniaLib plugin) {
        this.plugin = plugin;
    }

    @Default
    @CatchUnknown
    @CommandPermission("%eternia_logs_perm")
    @Description("%eternia_logs_description")
    public void onLog(CommandSender sender) {
        List<String> errors = plugin.getErrors();
        if (errors.isEmpty()) {
            EterniaLib.getChatCommons().sendMessage(sender, Messages.LOG_EMPTY);
            return;
        }

        String errorsString = String.join(", ", errors);
        EterniaLib.getChatCommons().sendMessage(sender, Messages.LOG_LIST_OF_LOGS, new MessageOptions(errorsString));
    }

}
