package br.com.eterniaserver.eternialib.core.commands;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.chat.ChatCommons;
import br.com.eterniaserver.eternialib.chat.MessageOptions;
import br.com.eterniaserver.eternialib.core.enums.Messages;

import net.kyori.adventure.text.Component;

import org.bukkit.command.CommandSender;

import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
class TestEterniaLogs {

    @Test
    void testEterniaLogWithErrorsCommand() {
        CommandSender sender = Mockito.mock(CommandSender.class);
        ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
        EterniaLib plugin = Mockito.mock(EterniaLib.class);

        EterniaLogs eterniaLogs = new EterniaLogs(plugin);
        List<String> errors = List.of("error1", "error2", "error3");
        String errorsString = String.join(", ", errors);

        Mockito.when(plugin.getErrors()).thenReturn(errors);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            eterniaLogs.onLog(sender);

            Mockito.verify(chatCommons, Mockito.times(1)).sendMessage(sender, Messages.LOG_LIST_OF_LOGS, new MessageOptions(errorsString));
        }
    }

    @Test
    void testEterniaLogEmptyCommand() {
        CommandSender sender = Mockito.mock(CommandSender.class);
        ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
        EterniaLib plugin = Mockito.mock(EterniaLib.class);
        Component component = Mockito.mock(Component.class);

        EterniaLogs eterniaLogs = new EterniaLogs(plugin);
        List<String> errors = List.of();

        Mockito.when(plugin.getErrors()).thenReturn(errors);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            Mockito.when(chatCommons.parseMessage(Messages.LOG_EMPTY)).thenReturn(component);

            eterniaLogs.onLog(sender);

            Mockito.verify(chatCommons, Mockito.times(1)).sendMessage(sender, Messages.LOG_EMPTY);
        }
    }

}
