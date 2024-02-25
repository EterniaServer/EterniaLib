package br.com.eterniaserver.eternialib.configuration.interfaces;

import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;


public interface ReloadableConfiguration {

    FileConfiguration inFileConfiguration();

    FileConfiguration outFileConfiguration();

    String getFolderPath();

    String getFilePath();

    ConfigurationCategory category();

    void executeConfig();

    void executeCritical();

    default void saveConfiguration(boolean inFolder) {
        String folderPath = getFolderPath();
        if (inFolder && new File(folderPath).mkdir()) {
            Bukkit.getLogger().log(Level.FINE, "Folder path {0} created.", folderPath);
        }

        String filePath = getFilePath();
        try {
            outFileConfiguration().save(filePath);
        } catch (IOException exception) {
            Bukkit.getLogger().log(Level.WARNING, "Creation failed to file {0}.", filePath);
        }
    }

}