package br.com.eterniaserver.eternialib.configuration;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.chat.ChatCommons;
import br.com.eterniaserver.eternialib.configuration.impl.ConfigurationManagerImpl;
import br.com.eterniaserver.eternialib.configuration.interfaces.MsgConfiguration;
import br.com.eterniaserver.eternialib.configuration.interfaces.ReloadableConfiguration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Set;

@SuppressWarnings("ResultOfMethodCallIgnored")
class TestConfigurationManager {

    private ConfigurationManager configurationManager;

    @BeforeEach
    void setup() {
        configurationManager = new ConfigurationManagerImpl();
    }

    @Test
    void testGetConfigurations() {
        ReloadableConfiguration configuration = Mockito.mock(ReloadableConfiguration.class);

        configurationManager.registerConfiguration("EterniaLib", "config", true, configuration);

        Set<String> expect = Set.of("eternialib_config");
        Set<String> result = configurationManager.getConfigurations();

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testGetConfiguration() {
        ReloadableConfiguration expect = Mockito.mock(ReloadableConfiguration.class);

        configurationManager.registerConfiguration("EterniaLib", "config", true, expect);

        ReloadableConfiguration result = configurationManager.getConfiguration("eternialib_config");

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testRegisterConfiguration() {
        ReloadableConfiguration configuration = Mockito.mock(ReloadableConfiguration.class);

        configurationManager.registerConfiguration("EterniaLib", "config", true, configuration);

        ReloadableConfiguration result = configurationManager.getConfiguration("eternialib_config");

        Mockito.verify(configuration, Mockito.times(1)).executeConfig();
        Mockito.verify(configuration, Mockito.times(1)).executeCritical();
        Mockito.verify(configuration, Mockito.times(1)).saveConfiguration(true);

        Assertions.assertEquals(configuration, result);
    }

    @Test
    void testRegisterConfigurationInFolderFalse() {
        ReloadableConfiguration configuration = Mockito.mock(ReloadableConfiguration.class);

        configurationManager.registerConfiguration("EterniaLib", "config", false, configuration);

        ReloadableConfiguration result = configurationManager.getConfiguration("eternialib_config");

        Mockito.verify(configuration, Mockito.times(1)).executeConfig();
        Mockito.verify(configuration, Mockito.times(1)).executeCritical();
        Mockito.verify(configuration, Mockito.times(1)).saveConfiguration(false);

        Assertions.assertEquals(configuration, result);
    }

    @Test
    void testRegisterConfigurationMessage() {
        MsgConfiguration<?> configuration = Mockito.mock(MsgConfiguration.class);

        try (MockedStatic<EterniaLib> pluginStatic = Mockito.mockStatic(EterniaLib.class)) {
            pluginStatic.when(EterniaLib::getChatCommons).thenReturn(Mockito.mock(ChatCommons.class));

            configurationManager.registerConfiguration("EterniaLib", "config", true, configuration);

            ReloadableConfiguration result = configurationManager.getConfiguration("eternialib_config");

            Mockito.verify(configuration, Mockito.times(1)).executeConfig();
            Mockito.verify(configuration, Mockito.times(1)).executeCritical();
            Mockito.verify(configuration, Mockito.times(1)).saveConfiguration(true);

            Mockito.verify(configuration, Mockito.times(1)).messages();

            Assertions.assertEquals(configuration, result);
        }
    }

}
