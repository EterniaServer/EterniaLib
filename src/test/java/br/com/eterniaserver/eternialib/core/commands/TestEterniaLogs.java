package br.com.eterniaserver.eternialib.core.commands;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

class TestEterniaLogs {

    @Test
    void testEterniaLogWithErrorsCommand() {
        CommandSender sender = Mockito.mock(CommandSender.class);
        EterniaLib plugin = Mockito.mock(EterniaLib.class);
        Component component = Mockito.mock(Component.class);

        EterniaLogs eterniaLogs = new EterniaLogs(plugin);
        List<String> errors = List.of("error1", "error2", "error3");
        String errorsString = String.join(", ", errors);

        Mockito.when(plugin.getErrors()).thenReturn(errors);
        Mockito.when(plugin.getComponentMessage(Messages.LOG_LIST_OF_LOGS, true, errorsString)).thenReturn(component);

        eterniaLogs.onLog(sender);

        Mockito.verify(plugin, Mockito.times(1)).getComponentMessage(Messages.LOG_LIST_OF_LOGS, true, errorsString);
        Mockito.verify(sender, Mockito.times(1)).sendMessage(component);
    }

    @Test
    void testEterniaLogEmptyCommand() {
        CommandSender sender = Mockito.mock(CommandSender.class);
        EterniaLib plugin = Mockito.mock(EterniaLib.class);
        Component component = Mockito.mock(Component.class);

        EterniaLogs eterniaLogs = new EterniaLogs(plugin);
        List<String> errors = List.of();

        Mockito.when(plugin.getErrors()).thenReturn(errors);
        Mockito.when(plugin.getComponentMessage(Messages.LOG_EMPTY, true)).thenReturn(component);

        eterniaLogs.onLog(sender);

        Mockito.verify(plugin, Mockito.times(1)).getComponentMessage(Messages.LOG_EMPTY, true);
        Mockito.verify(sender, Mockito.times(1)).sendMessage(component);
    }

}
