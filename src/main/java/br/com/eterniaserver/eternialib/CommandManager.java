package br.com.eterniaserver.eternialib;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.BukkitConditionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.CommandConditions;
import co.aikar.commands.CommandReplacements;

public class CommandManager {

    private CommandManager() {
        throw new IllegalStateException("Utility class");
    }

    public static void registerCommand(BaseCommand baseCommand) {
        EterniaLib.manager.registerCommand(baseCommand);
    }

    public static CommandReplacements getCommandReplacements() {
        return EterniaLib.manager.getCommandReplacements();
    }

    public static CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> getCommandConditions() {
        return EterniaLib.manager.getCommandConditions();
    }

    public static CommandCompletions<BukkitCommandCompletionContext> getCommandCompletions() {
        return EterniaLib.manager.getCommandCompletions();
    }

}
