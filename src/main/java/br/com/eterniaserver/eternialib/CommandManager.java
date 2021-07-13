package br.com.eterniaserver.eternialib;

import co.aikar.commands.*;

/**
 * Handlers the ACF.
 */
public class CommandManager {

    /**
     * Static class should not be initialized.
     */
    private CommandManager() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Register a new {@link BaseCommand}.
     *
     * @param baseCommand is the class of command
     */
    public static void registerCommand(final BaseCommand baseCommand) {
        EterniaLib.manager.registerCommand(baseCommand);
    }

    /**
     * Returns the {@link CommandReplacements} handler of ACF.
     *
     * @return the {@link CommandReplacements} handler
     */
    public static CommandReplacements getCommandReplacements() {
        return EterniaLib.manager.getCommandReplacements();
    }

    /**
     * Returns the {@link CommandConditions} handler of ACF.
     *
     * @return the {@link CommandConditions} handler
     */
    public static CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> getCommandConditions() {
        return EterniaLib.manager.getCommandConditions();
    }

    /**
     * Returns the {@link CommandCompletions} handler of ACF.
     *
     * @return the {@link CommandCompletions} handler
     */
    public static CommandCompletions<BukkitCommandCompletionContext> getCommandCompletions() {
        return EterniaLib.manager.getCommandCompletions();
    }

    /**
     * Returns the {@link CommandContexts} handler of ACF.
     *
     * @return the {@link CommandContexts} handler
     */
    public static CommandContexts<BukkitCommandExecutionContext> getCommandContexts() {
        return EterniaLib.manager.getCommandContexts();
    }

}
