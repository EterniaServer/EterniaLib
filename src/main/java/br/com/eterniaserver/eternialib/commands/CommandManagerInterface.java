package br.com.eterniaserver.eternialib.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.BukkitConditionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.CommandConditions;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.CommandReplacements;

public interface CommandManagerInterface {

    void registerCommand(final BaseCommand baseCommand);

    CommandReplacements getCommandReplacements();

    CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> getCommandConditions();

    CommandCompletions<BukkitCommandCompletionContext> getCommandCompletions();

    CommandContexts<BukkitCommandExecutionContext> getCommandContexts();

}
