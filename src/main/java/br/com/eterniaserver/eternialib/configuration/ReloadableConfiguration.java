package br.com.eterniaserver.eternialib.configuration;

import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.configuration.enums.PathType;
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

    String[] messages();

    CommandLocale[] commandsLocale();

    ConfigurationCategory category();

    void executeConfig();

    void executeCritical();

    default <E extends Enum<E>> void addCommandLocale(Enum<E> commandsEnum, CommandLocale locale) {
        FileConfiguration inFile = inFileConfiguration();
        FileConfiguration outFile = outFileConfiguration();

        CommandLocale[] commandLocales = commandsLocale();

        String name = inFile.getString(PathType.COMMAND_NAME.getPath(commandsEnum), locale.name());
        String syntax = inFile.getString(PathType.COMMAND_SYNTAX.getPath(commandsEnum), locale.syntax());
        String description = inFile.getString(PathType.COMMAND_DESCRIPTION.getPath(commandsEnum), locale.description());
        String permission = inFile.getString(PathType.COMMAND_PERMISSION.getPath(commandsEnum), locale.perm());
        String aliases = inFile.getString(PathType.COMMAND_ALIASES.getPath(commandsEnum), locale.aliases());

        commandLocales[commandsEnum.ordinal()] = new CommandLocale(name, syntax, description, permission, aliases);

        outFile.set(PathType.COMMAND_NAME.getPath(commandsEnum), name);
        outFile.set(PathType.COMMAND_SYNTAX.getPath(commandsEnum), syntax);
        outFile.set(PathType.COMMAND_DESCRIPTION.getPath(commandsEnum), description);
        outFile.set(PathType.COMMAND_PERMISSION.getPath(commandsEnum), permission);
        outFile.set(PathType.COMMAND_ALIASES.getPath(commandsEnum), aliases);
    }

    default <E extends Enum<E>> void addMessage(Enum<E> messagesEnum, String text, String notes) {
        FileConfiguration inFile = inFileConfiguration();
        FileConfiguration outFile = outFileConfiguration();
        String[] messages = messages();

        messages[messagesEnum.ordinal()] = inFile.getString(PathType.MESSAGE.getPath(messagesEnum), text);

        outFile.set(PathType.MESSAGE.getPath(messagesEnum), messages[messagesEnum.ordinal()]);
        outFile.set(PathType.MESSAGE_NOTE.getPath(messagesEnum), notes);
    }

    default void saveConfiguration(final boolean inFolder) {
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