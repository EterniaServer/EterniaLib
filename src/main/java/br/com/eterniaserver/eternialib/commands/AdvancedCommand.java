package br.com.eterniaserver.eternialib.commands;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.commands.enums.AdvancedCategory;
import br.com.eterniaserver.eternialib.commands.enums.AdvancedRules;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;


public abstract class AdvancedCommand {

    public abstract Player sender();

    public abstract void execute();

    public abstract String getTimeMessage();

    public abstract void abort(Component message);

    public abstract boolean isAborted();

    public abstract BukkitTask executeAsynchronously();

    public abstract AdvancedCategory getCategory();

    public abstract int neededTimeInSeconds();

    public abstract int getCommandTicks();

    public abstract void addCommandTicks(int ticks);

    public abstract AdvancedRules[] getAdvancedRules();

    public boolean hasRule(AdvancedRules rule) {
        for (AdvancedRules advancedRules : getAdvancedRules()) {
            if (advancedRules == rule) {
                return true;
            }
        }
        return false;
    }

    public void runTimeMessage(EterniaLib plugin) {
        Player sender = sender();
        String timeMessage = getTimeMessage();
        String secondsLeft = (neededTimeInSeconds() - (getCommandTicks() / 20)) + "s";

        Component message = plugin.getComponentMessage(Messages.TIME_MESSAGE, true, timeMessage, secondsLeft);
        sender.sendMessage(message);
    }

    public boolean increaseCommandTicks(int ticks) {
        int commandTicks = getCommandTicks() + ticks;
        if (commandTicks >= neededTimeInSeconds() * 20) {
            return true;
        }

        addCommandTicks(commandTicks);
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object instanceof AdvancedCommand other) {
            return sender().equals(other.sender());
        }

        return false;
    }

    @Override
    public int hashCode() {
        String senderUUIDInString = sender().getUniqueId().toString();
        String commandCategoryName = getCategory().name();
        String timeInString = String.valueOf(neededTimeInSeconds());

        return (senderUUIDInString + commandCategoryName + timeInString).hashCode();
    }

}
