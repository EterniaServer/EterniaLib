package br.com.eterniaserver.eternialib;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.Locale;

import br.com.eterniaserver.eternialib.sql.Connections;

import org.bukkit.plugin.java.JavaPlugin;

public class EterniaLib extends JavaPlugin {

    private static PaperCommandManager manager;
    private static EterniaLib plugin;
    public Connections connections;
    public static boolean mysql;

    public void onEnable() {
        manager = new PaperCommandManager(this);

        EterniaLib.plugin = this;
        try {
            manager.getLocales().loadYamlLanguageFile("acf_messages.yml", Locale.ENGLISH);
            this.connections = new Connections();
        } catch (IOException | InvalidConfigurationException e) {
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

    public static PaperCommandManager getManager() {
        return manager;
    }

}