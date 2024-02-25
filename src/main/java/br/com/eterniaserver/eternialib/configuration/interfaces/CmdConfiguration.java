package br.com.eterniaserver.eternialib.configuration.interfaces;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.configuration.CommandLocale;
import br.com.eterniaserver.eternialib.configuration.enums.PathType;
import org.bukkit.configuration.file.FileConfiguration;

public interface CmdConfiguration<C extends Enum<C>> extends ReloadableConfiguration {

    default void addCommandLocale(C commandsEnum, CommandLocale locale) {
        FileConfiguration inFile = inFileConfiguration();
        FileConfiguration outFile = outFileConfiguration();

        String name = inFile.getString(PathType.COMMAND_NAME.getPath(commandsEnum), locale.name());
        String syntax = inFile.getString(PathType.COMMAND_SYNTAX.getPath(commandsEnum), locale.syntax());
        String description = inFile.getString(PathType.COMMAND_DESCRIPTION.getPath(commandsEnum), locale.description());
        String permission = inFile.getString(PathType.COMMAND_PERMISSION.getPath(commandsEnum), locale.perm());
        String aliases = inFile.getString(PathType.COMMAND_ALIASES.getPath(commandsEnum), locale.aliases());

        EterniaLib.getCmdManager().getCommandReplacements().addReplacements(
                commandsEnum.name().toLowerCase(), name,
                commandsEnum.name().toLowerCase() + "_DESCRIPTION", description,
                commandsEnum.name().toLowerCase() + "_PERM", permission,
                commandsEnum.name().toLowerCase() + "_SYNTAX", syntax,
                commandsEnum.name().toLowerCase() + "_ALIASES", aliases
        );

        outFile.set(PathType.COMMAND_NAME.getPath(commandsEnum), name);
        outFile.set(PathType.COMMAND_SYNTAX.getPath(commandsEnum), syntax);
        outFile.set(PathType.COMMAND_DESCRIPTION.getPath(commandsEnum), description);
        outFile.set(PathType.COMMAND_PERMISSION.getPath(commandsEnum), permission);
        outFile.set(PathType.COMMAND_ALIASES.getPath(commandsEnum), aliases);
    }

}
