package br.com.eterniaserver.eternialib.configuration;

import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;


public interface FileCfg {

    FileConfiguration inFileConfiguration();

    FileConfiguration outFileConfiguration();

    String getFolderPath();

    String getFilePath();

    String[] messages();

    ConfigurationCategory category();

    void executeConfig();

    void executeCritical();

    default <E extends Enum<E>> void addMessage(Enum<E> messagesEnum, String text, String notes) {
        FileConfiguration inFile = inFileConfiguration();
        FileConfiguration outFile = outFileConfiguration();
        String[] messages = messages();

        messages[messagesEnum.ordinal()] = inFile.getString(getPath(messagesEnum, false), text);

        outFile.set(getPath(messagesEnum, false), messages[messagesEnum.ordinal()]);
        outFile.set(getPath(messagesEnum, true), notes);
    }

    default void saveConfiguration(final boolean inFolder) {
        String folderPath = getFolderPath();
        if (inFolder && new File(folderPath).mkdir()) {
            Bukkit.getLogger().log(Level.FINE, "Folder path " + folderPath + "created.");
        }

        String filePath = getFilePath();
        try {
            outFileConfiguration().save(filePath);
        } catch (IOException exception) {
            Bukkit.getLogger().log(Level.WARNING, "Creation failed to file " + filePath);
        }
    }

    private <E extends Enum<E>> String getPath(Enum<E> messagesEnum, boolean isHelp) {
        if (isHelp) {
            return "messages." + messagesEnum.name() + ".notes";
        }

        return "messages." + messagesEnum.name() + ".text";
    }
}