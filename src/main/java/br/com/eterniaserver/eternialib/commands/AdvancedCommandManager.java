package br.com.eterniaserver.eternialib.commands;

import br.com.eterniaserver.eternialib.commands.enums.AdvancedRules;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public interface AdvancedCommandManager extends Runnable {

    void checkHasBreakingRule(UUID uuid, AdvancedRules rules);

    AdvancedCommand getAndRemoveCommand(UUID uuid);

    boolean addConfirmationCommand(AdvancedCommand command);

    BukkitTask[] getAndRemoveTasks(UUID uuid);

    void abortTimedCommand(UUID uuid);

    boolean addTimedCommand(AdvancedCommand command);

    void removeCommandsFromPlayer(UUID uuid);

}
