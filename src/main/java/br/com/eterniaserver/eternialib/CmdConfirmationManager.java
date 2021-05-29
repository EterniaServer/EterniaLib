package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.core.interfaces.CommandConfirmable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handlers the command confirmation manager.
 */
public class CmdConfirmationManager {

    private static final Map<UUID, CommandConfirmable> commandConfirmMap = new HashMap<>();

    /**
     * Static class should not be initialized.
     */
    private CmdConfirmationManager() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns a class of command if has one scheduled, otherwise it returns null.
     *
     * @param uuid is the {@link UUID} of player
     * @return is the {@link CommandConfirmable} object
     */
    public static CommandConfirmable getAndRemoveCommand(final UUID uuid) {
        final CommandConfirmable commandConfirmable = commandConfirmMap.get(uuid);
        commandConfirmMap.remove(uuid);
        return commandConfirmable;
    }

    /**
     * Schedule a command that needs to be confirmed.
     *
     * @param player is the object of player
     * @param confirmable is the {@link CommandConfirmable} object
     */
    public static void scheduleCommand(final Player player,
                                       final CommandConfirmable confirmable) {
        commandConfirmMap.put(player.getUniqueId(), confirmable);
    }

}
