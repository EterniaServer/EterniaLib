package br.com.eterniaserver.eternialib;

import org.bukkit.configuration.InvalidConfigurationException;
import java.io.IOException;
import br.com.eterniaserver.eternialib.sql.Connections;
import org.bukkit.plugin.java.JavaPlugin;

public class EterniaLib extends JavaPlugin
{
    private static EterniaLib plugin;
    public Connections connections;
    public static boolean mysql;

    public void onEnable() {
        EterniaLib.plugin = this;
        try {
            this.connections = new Connections();
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        this.connections.Close();
    }

    public static EterniaLib getPlugin() {
        return EterniaLib.plugin;
    }

    public static boolean getMySQL() {
        return EterniaLib.mysql;
    }
}