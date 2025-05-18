package br.com.eterniaserver.eternialib.commands.impl;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.commands.CommandManager;
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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.logging.Level;

public class CommandManagerImpl implements CommandManager {

    private final PaperCommandManager manager;

    public CommandManagerImpl(EterniaLib plugin) {
        manager = new PaperCommandManager(plugin);
        manager.enableUnstableAPI("help");

        Path pluginFolder = plugin.getDataFolder().toPath();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(pluginFolder, "command_messages_*.yml")) {
            for (Path entry : stream) {
                String fileName = entry.getFileName().toString();
                String locale = fileName
                        .replace("command_messages_", "")
                        .replace(".yml", "");

                File localeFile = entry.toFile();
                if (!localeFile.exists()) {
                    plugin.saveResource(localeFile.getName(), false);
                }

                manager.getLocales().loadYamlLanguageFile(localeFile.getName(), Locale.forLanguageTag(locale));
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error when creating or loading YML configuration file.");
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().log(Level.SEVERE, "YML configuration file is invalid.");
        }

        manager.getLocales().setDefaultLocale(Locale.forLanguageTag("pt"));
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
