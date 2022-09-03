package br.com.eterniaserver.eternialib.commands.impl;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.commands.CommandManagerInterface;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.BukkitConditionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.CommandConditions;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.CommandReplacements;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class CommandManager implements CommandManagerInterface {

    private static final String ACF_MESSAGES = "command_messages.yml";

    private final PaperCommandManager manager;

    public CommandManager(final EterniaLib plugin) throws IOException, InvalidConfigurationException {
        this.manager = new PaperCommandManager(plugin);
        this.manager.enableUnstableAPI("help");

        File files = new File(plugin.getDataFolder(), ACF_MESSAGES);

        if (!files.exists()) {
            plugin.saveResource(ACF_MESSAGES, false);
        }

        this.manager.getLocales().loadYamlLanguageFile(ACF_MESSAGES, Locale.ENGLISH);
        this.manager.getLocales().setDefaultLocale(Locale.ENGLISH);
    }

    @Override
    public void registerCommand(BaseCommand baseCommand) {
        this.manager.registerCommand(baseCommand);
    }

    @Override
    public CommandReplacements getCommandReplacements() {
        return this.manager.getCommandReplacements();
    }

    @Override
    public CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> getCommandConditions() {
        return this.manager.getCommandConditions();
    }

    @Override
    public CommandCompletions<BukkitCommandCompletionContext> getCommandCompletions() {
        return this.manager.getCommandCompletions();
    }

    @Override
    public CommandContexts<BukkitCommandExecutionContext> getCommandContexts() {
        return this.manager.getCommandContexts();
    }
}
