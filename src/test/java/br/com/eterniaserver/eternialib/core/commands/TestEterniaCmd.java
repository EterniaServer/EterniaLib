package br.com.eterniaserver.eternialib.core.commands;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.chat.ChatCommons;
import br.com.eterniaserver.eternialib.chat.MessageOptions;
import br.com.eterniaserver.eternialib.configuration.ConfigurationManager;
import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.core.configs.CoreCfg;
import br.com.eterniaserver.eternialib.core.enums.Messages;

import co.aikar.commands.CommandHelp;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

@SuppressWarnings("ResultOfMethodCallIgnored")
class TestEterniaCmd {

    @Test
    void testEterniaHelpCommand() {
        CommandHelp commandHelp = Mockito.mock(CommandHelp.class);

        EterniaCmd eterniaCmd = new EterniaCmd();

        eterniaCmd.onHelp(commandHelp);

        Mockito.verify(commandHelp, Mockito.times(1)).showHelp();
    }

    @Test
    void testEterniaReloadWarningAdviceConfigWithoutCheck() {
        ConfigurationManager configurationManager = Mockito.mock(ConfigurationManager.class);
        ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
        CommandSender sender = Mockito.mock(CommandSender.class);
        CoreCfg coreCfg = Mockito.mock(CoreCfg.class);

        String entry = "eterniatest_config";
        EterniaCmd eterniaCmd = new EterniaCmd();

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            pluginStatic.when(EterniaLib::getCfgManager).thenReturn(configurationManager);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            Mockito.when(coreCfg.category()).thenReturn(ConfigurationCategory.WARNING_ADVICE);
            Mockito.when(configurationManager.getConfiguration(entry)).thenReturn(coreCfg);

            eterniaCmd.onReload(sender, entry);

            Mockito.verify(chatCommons, Mockito.times(1)).sendMessage(sender, Messages.CONFIG_ADVICE, new MessageOptions(entry));
            Mockito.verify(coreCfg, Mockito.times(0)).executeConfig();
            Mockito.verify(coreCfg, Mockito.times(0)).executeCritical();
        }
    }

    @Test
    void testEterniaReloadWarningAdviceConfigWithCheck() {
        ConfigurationManager configurationManager = Mockito.mock(ConfigurationManager.class);
        ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
        CommandSender sender = Mockito.mock(CommandSender.class);
        CoreCfg coreCfg = Mockito.mock(CoreCfg.class);

        String entry = "eterniatest_config:t";
        String configString = "eterniatest_config";
        EterniaCmd eterniaCmd = new EterniaCmd();

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            pluginStatic.when(EterniaLib::getCfgManager).thenReturn(configurationManager);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            Mockito.when(coreCfg.category()).thenReturn(ConfigurationCategory.WARNING_ADVICE);
            Mockito.when(configurationManager.getConfiguration(configString)).thenReturn(coreCfg);

            eterniaCmd.onReload(sender, entry);

            Mockito.verify(chatCommons, Mockito.times(1)).sendMessage(sender, Messages.CONFIG_RELOADED, new MessageOptions(configString));
            Mockito.verify(coreCfg, Mockito.times(1)).executeConfig();
            Mockito.verify(coreCfg, Mockito.times(1)).executeCritical();
        }
    }

    @Test
    void testEterniaReloadGenericWithCheck() {
        ConfigurationManager configurationManager = Mockito.mock(ConfigurationManager.class);
        ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
        CommandSender sender = Mockito.mock(CommandSender.class);
        CoreCfg coreCfg = Mockito.mock(CoreCfg.class);

        String entry = "eterniatest_config:t";
        String configString = "eterniatest_config";
        EterniaCmd eterniaCmd = new EterniaCmd();

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            pluginStatic.when(EterniaLib::getCfgManager).thenReturn(configurationManager);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            Mockito.when(coreCfg.category()).thenReturn(ConfigurationCategory.GENERIC);
            Mockito.when(configurationManager.getConfiguration(configString)).thenReturn(coreCfg);

            eterniaCmd.onReload(sender, entry);

            Mockito.verify(chatCommons, Mockito.times(1)).sendMessage(sender, Messages.CONFIG_RELOADED, new MessageOptions(configString));
            Mockito.verify(coreCfg, Mockito.times(1)).executeConfig();
            Mockito.verify(coreCfg, Mockito.times(1)).executeCritical();
        }
    }

    @Test
    void testEterniaReloadBlocked() {
        ConfigurationManager configurationManager = Mockito.mock(ConfigurationManager.class);
        ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
        CommandSender sender = Mockito.mock(CommandSender.class);
        CoreCfg coreCfg = Mockito.mock(CoreCfg.class);

        String entry = "eterniatest_config";
        EterniaCmd eterniaCmd = new EterniaCmd();

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            pluginStatic.when(EterniaLib::getCfgManager).thenReturn(configurationManager);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            Mockito.when(coreCfg.category()).thenReturn(ConfigurationCategory.BLOCKED);
            Mockito.when(configurationManager.getConfiguration(entry)).thenReturn(coreCfg);

            eterniaCmd.onReload(sender, entry);

            Mockito.verify(chatCommons, Mockito.times(1)).sendMessage(sender, Messages.CONFIG_BLOCKED, new MessageOptions(entry));
            Mockito.verify(coreCfg, Mockito.times(0)).executeConfig();
            Mockito.verify(coreCfg, Mockito.times(0)).executeCritical();
        }
    }

    @Test
    void testEterniaReloadInvalid() {
        ConfigurationManager configurationManager = Mockito.mock(ConfigurationManager.class);
        ChatCommons chatCommons = Mockito.mock(ChatCommons.class);
        CommandSender sender = Mockito.mock(CommandSender.class);

        String entry = "eterniatest_invalid";
        EterniaCmd eterniaCmd = new EterniaCmd();

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            pluginStatic.when(EterniaLib::getCfgManager).thenReturn(configurationManager);
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(chatCommons);

            Mockito.when(configurationManager.getConfiguration(entry)).thenReturn(null);

            eterniaCmd.onReload(sender, entry);

            Mockito.verify(chatCommons, Mockito.times(1)).sendMessage(sender, Messages.CONFIG_INVALID);
        }
    }

}
