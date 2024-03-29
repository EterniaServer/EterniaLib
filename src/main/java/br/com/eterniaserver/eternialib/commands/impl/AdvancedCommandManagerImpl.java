package br.com.eterniaserver.eternialib.commands.impl;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.commands.AdvancedCommand;
import br.com.eterniaserver.eternialib.commands.AdvancedCommandManager;
import br.com.eterniaserver.eternialib.commands.enums.AdvancedCategory;
import br.com.eterniaserver.eternialib.commands.enums.AdvancedRules;
import br.com.eterniaserver.eternialib.core.enums.Messages;

import net.kyori.adventure.text.Component;

import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class AdvancedCommandManagerImpl implements AdvancedCommandManager {

    private final int tickDelay;
    private final EterniaLib plugin;

    private final Map<String, AdvancedCommand> commandsMap = new ConcurrentHashMap<>();

    private final Map<BukkitTask, UUID> executingTasks = new ConcurrentHashMap<>();

    public AdvancedCommandManagerImpl(EterniaLib plugin, int tickDelay) {
        this.plugin = plugin;
        this.tickDelay = tickDelay;
    }

    @Override
    public void checkHasBreakingRule(UUID uuid, AdvancedRules rule) {
        String commandEntry = getCommandEntry(uuid, AdvancedCategory.TIMED);
        AdvancedCommand registeredCommand = commandsMap.get(commandEntry);
        if (registeredCommand == null || !registeredCommand.hasRule(rule)) {
            return;
        }

        Component message = switch (rule) {
            case NOT_ATTACK -> EterniaLib.getChatCommons().parseMessage(Messages.ATTACKED);
            case NOT_BREAK_BLOCK -> EterniaLib.getChatCommons().parseMessage(Messages.BLOCK_BRAKED);
            case NOT_JUMP -> EterniaLib.getChatCommons().parseMessage(Messages.JUMPED);
            case NOT_SNEAK -> EterniaLib.getChatCommons().parseMessage(Messages.SNEAKED);
            default -> EterniaLib.getChatCommons().parseMessage(Messages.MOVED);
        };

        registeredCommand.abort(message);
        commandsMap.remove(commandEntry);
    }

    @Override
    public AdvancedCommand getAndRemoveCommand(final UUID uuid) {
        String commandEntry = getCommandEntry(uuid, AdvancedCategory.CONFIRMATION);

        return commandsMap.remove(commandEntry);
    }

    @Override
    public boolean addConfirmationCommand(AdvancedCommand command) {
        UUID uuid = command.sender().getUniqueId();
        String commandEntry = getCommandEntry(uuid, AdvancedCategory.CONFIRMATION);

        Component msg = EterniaLib.getChatCommons().parseMessage(Messages.CONFIRMED_COMMAND_MESSAGE);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> command.sender().sendMessage(msg));

        if (commandsMap.containsKey(commandEntry)) {
            return false;
        }

        commandsMap.put(commandEntry, command);
        return true;
    }

    @Override
    public BukkitTask[] getAndRemoveTasks(UUID uuid) {
        BukkitTask[] tasks = executingTasks.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(uuid))
                .map(Map.Entry::getKey)
                .toArray(BukkitTask[]::new);

        for (BukkitTask task : tasks) {
            executingTasks.remove(task);
        }

        return tasks;
    }

    @Override
    public void abortTimedCommand(UUID uuid) {
        String entry = getCommandEntry(uuid, AdvancedCategory.TIMED);
        AdvancedCommand command = commandsMap.get(entry);

        if (command != null) {
            command.abort(EterniaLib.getChatCommons().parseMessage(Messages.COMMAND_CANCELLED));
        }
    }

    @Override
    public boolean addTimedCommand(final AdvancedCommand command) {
        String entry = getCommandEntry(command.sender().getUniqueId(), AdvancedCategory.TIMED);
        AdvancedCommand registeredCommand = commandsMap.get(entry);

        if (registeredCommand != null) {
            return false;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, command::runTimeMessage);
        commandsMap.put(entry, command);
        return true;
    }

    @Override
    public void removeCommandsFromPlayer(UUID uuid) {
        String timedEntry = getCommandEntry(uuid, AdvancedCategory.TIMED);
        String confirmationEntry = getCommandEntry(uuid, AdvancedCategory.CONFIRMATION);

        AdvancedCommand confirmationCommand = commandsMap.get(confirmationEntry);
        AdvancedCommand timedCommand = commandsMap.get(timedEntry);

        Component message = EterniaLib.getChatCommons().parseMessage(Messages.COMMAND_CANCELLED);
        if (confirmationCommand != null) {
            confirmationCommand.abort(message);
        }
        if (timedCommand != null) {
            timedCommand.abort(message);
        }
    }

    @Override
    public void run() {
        Iterator<Map.Entry<String, AdvancedCommand>> i = commandsMap.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, AdvancedCommand> entry = i.next();
            AdvancedCommand command = entry.getValue();
            boolean isTimed = command.getCategory() == AdvancedCategory.TIMED;
            boolean finishedTicks = command.increaseCommandTicks(tickDelay);

            if (command.isAborted()) {
                i.remove();
            }
            else if (isTimed && finishedTicks) {
                BukkitTask task = command.executeAsynchronously();
                if (task != null) {
                    executingTasks.put(task, command.sender().getUniqueId());
                }
                command.execute();
                i.remove();
            }
            else if (isTimed) {
                command.runTimeMessage();
            }
            else if (finishedTicks) {
                command.abort(EterniaLib.getChatCommons().parseMessage(Messages.COMMAND_CANCELLED));
                i.remove();
            }
        }
    }

    private String getCommandEntry(UUID uuid, AdvancedCategory category) {
        return uuid.toString() + "|" + category.name();
    }

}
