package br.com.eterniaserver.eternialib.commands;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.core.commands.EterniaCmd;
import br.com.eterniaserver.eternialib.core.configs.CoreCfg;
import br.com.eterniaserver.eternialib.core.enums.Messages;

import co.aikar.commands.CommandHelp;

import net.kyori.adventure.text.Component;

import org.bukkit.command.CommandSender;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

class TestEterniaCmdCmd {

    @Test
    void testEterniaHelpCommand() {
        EterniaLib plugin = Mockito.mock(EterniaLib.class);
        CommandHelp commandHelp = Mockito.mock(CommandHelp.class);

        EterniaCmd eterniaCmd = new EterniaCmd(plugin);

        eterniaCmd.onHelp(commandHelp);

        Mockito.verify(commandHelp, Mockito.times(1)).showHelp();
    }

    @Test
    void testEterniaReloadWarningAdviceConfigWithoutCheck() {
        CommandSender sender = Mockito.mock(CommandSender.class);
        EterniaLib plugin = Mockito.mock(EterniaLib.class);
        CoreCfg coreCfg = Mockito.mock(CoreCfg.class);
        Component component = Mockito.mock(Component.class);

        String entry = "eterniatest_config";
        EterniaCmd eterniaCmd = new EterniaCmd(plugin);

        Mockito.when(coreCfg.category()).thenReturn(ConfigurationCategory.WARNING_ADVICE);
        Mockito.when(plugin.getConfiguration(entry)).thenReturn(coreCfg);
        Mockito.when(plugin.getComponentMessage(Messages.CONFIG_ADVICE, true, entry)).thenReturn(component);

        eterniaCmd.onReload(sender, entry);

        Mockito.verify(plugin, Mockito.times(1)).getComponentMessage(Messages.CONFIG_ADVICE, true, entry);
        Mockito.verify(sender, Mockito.times(1)).sendMessage(component);
        Mockito.verify(coreCfg, Mockito.times(0)).executeConfig();
        Mockito.verify(coreCfg, Mockito.times(0)).executeCritical();
    }

    @Test
    void testEterniaReloadWarningAdviceConfigWithCheck() {
        CommandSender sender = Mockito.mock(CommandSender.class);
        EterniaLib plugin = Mockito.mock(EterniaLib.class);
        CoreCfg coreCfg = Mockito.mock(CoreCfg.class);
        Component component = Mockito.mock(Component.class);

        String entry = "eterniatest_config:t";
        String configString = "eterniatest_config";
        EterniaCmd eterniaCmd = new EterniaCmd(plugin);

        Mockito.when(coreCfg.category()).thenReturn(ConfigurationCategory.WARNING_ADVICE);
        Mockito.when(plugin.getConfiguration(configString)).thenReturn(coreCfg);
        Mockito.when(plugin.getComponentMessage(Messages.CONFIG_RELOADED, true, configString)).thenReturn(component);

        eterniaCmd.onReload(sender, entry);

        Mockito.verify(plugin, Mockito.times(1)).getComponentMessage(Messages.CONFIG_RELOADED, true, configString);
        Mockito.verify(sender, Mockito.times(1)).sendMessage(component);
        Mockito.verify(coreCfg, Mockito.times(1)).executeConfig();
        Mockito.verify(coreCfg, Mockito.times(1)).executeCritical();
    }

    @Test
    void testEterniaReloadGenericWithCheck() {
        CommandSender sender = Mockito.mock(CommandSender.class);
        EterniaLib plugin = Mockito.mock(EterniaLib.class);
        CoreCfg coreCfg = Mockito.mock(CoreCfg.class);
        Component component = Mockito.mock(Component.class);

        String entry = "eterniatest_config:t";
        String configString = "eterniatest_config";
        EterniaCmd eterniaCmd = new EterniaCmd(plugin);

        Mockito.when(coreCfg.category()).thenReturn(ConfigurationCategory.GENERIC);
        Mockito.when(plugin.getConfiguration(configString)).thenReturn(coreCfg);
        Mockito.when(plugin.getComponentMessage(Messages.CONFIG_RELOADED, true, configString)).thenReturn(component);

        eterniaCmd.onReload(sender, entry);

        Mockito.verify(plugin, Mockito.times(1)).getComponentMessage(Messages.CONFIG_RELOADED, true, configString);
        Mockito.verify(sender, Mockito.times(1)).sendMessage(component);
        Mockito.verify(coreCfg, Mockito.times(1)).executeConfig();
        Mockito.verify(coreCfg, Mockito.times(1)).executeCritical();
    }

    @Test
    void testEterniaReloadBlocked() {
        CommandSender sender = Mockito.mock(CommandSender.class);
        EterniaLib plugin = Mockito.mock(EterniaLib.class);
        CoreCfg coreCfg = Mockito.mock(CoreCfg.class);
        Component component = Mockito.mock(Component.class);

        String entry = "eterniatest_config";
        EterniaCmd eterniaCmd = new EterniaCmd(plugin);

        Mockito.when(coreCfg.category()).thenReturn(ConfigurationCategory.BLOCKED);
        Mockito.when(plugin.getConfiguration(entry)).thenReturn(coreCfg);
        Mockito.when(plugin.getComponentMessage(Messages.CONFIG_BLOCKED, true, entry)).thenReturn(component);

        eterniaCmd.onReload(sender, entry);

        Mockito.verify(plugin, Mockito.times(1)).getComponentMessage(Messages.CONFIG_BLOCKED, true, entry);
        Mockito.verify(sender, Mockito.times(1)).sendMessage(component);
        Mockito.verify(coreCfg, Mockito.times(0)).executeConfig();
        Mockito.verify(coreCfg, Mockito.times(0)).executeCritical();
    }

    @Test
    void testEterniaReloadInvalid() {
        CommandSender sender = Mockito.mock(CommandSender.class);
        EterniaLib plugin = Mockito.mock(EterniaLib.class);
        Component component = Mockito.mock(Component.class);

        String entry = "eterniatest_invalid";
        EterniaCmd eterniaCmd = new EterniaCmd(plugin);

        Mockito.when(plugin.getConfiguration(entry)).thenReturn(null);
        Mockito.when(plugin.getComponentMessage(Messages.CONFIG_INVALID, true, entry)).thenReturn(component);

        eterniaCmd.onReload(sender, entry);

        Mockito.verify(plugin, Mockito.times(1)).getComponentMessage(Messages.CONFIG_INVALID, true, entry);
        Mockito.verify(sender, Mockito.times(1)).sendMessage(component);
    }

}
