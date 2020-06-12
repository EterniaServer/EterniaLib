package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.sql.Connections;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class EterniaLib extends JavaPlugin {

    private static EterniaLib plugin;
    public Connections connections;

    @Override
    public void onEnable() {

        plugin = this;

        try {
            connections = new Connections();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    public void onDisable() {
        connections.Close();
    }

    public static EterniaLib getPlugin() {
        return plugin;
    }

}