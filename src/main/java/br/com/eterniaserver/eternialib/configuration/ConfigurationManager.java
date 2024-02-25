package br.com.eterniaserver.eternialib.configuration;

import br.com.eterniaserver.eternialib.configuration.interfaces.ReloadableConfiguration;

import java.util.Set;

public interface ConfigurationManager {

    Set<String> getConfigurations();

    ReloadableConfiguration getConfiguration(String name);

    void registerConfiguration(String plugin, String config, boolean inFolder, ReloadableConfiguration reloadableImp);

}
