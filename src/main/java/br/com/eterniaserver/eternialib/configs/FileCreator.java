package br.com.eterniaserver.eternialib.configs;

import org.bukkit.plugin.Plugin;

import java.io.File;

public class FileCreator {

    public static File fileLoad(final Plugin plugin, final String fileName) {

        final File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) plugin.saveResource(fileName, false);
        return file;

    }

}
