package br.com.eterniaserver.eternialib.configuration.impl;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.configuration.ConfigurationManager;
import br.com.eterniaserver.eternialib.configuration.interfaces.MsgConfiguration;
import br.com.eterniaserver.eternialib.configuration.interfaces.ReloadableConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigurationManagerImpl implements ConfigurationManager {

    private final Map<String, ReloadableConfiguration> configurations = new HashMap<>();

    @Override
    public Set<String> getConfigurations() {
        return configurations.keySet();
    }

    @Override
    public ReloadableConfiguration getConfiguration(String name) {
        return configurations.get(name);
    }

    @Override
    public void registerConfiguration(String plugin, String config, boolean inFolder, ReloadableConfiguration reloadableImp) {
        String entry = (plugin + "_" + config).toLowerCase().replace(" ", "_");

        configurations.put(entry, reloadableImp);

        reloadableImp.executeConfig();
        reloadableImp.executeCritical();
        reloadableImp.saveConfiguration(inFolder);

        if (reloadableImp instanceof MsgConfiguration<?> reloadableCfg) {
            EterniaLib.getChatCommons().registerMessage(reloadableCfg.messages());
        }
    }

}
