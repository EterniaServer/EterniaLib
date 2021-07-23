package br.com.eterniaserver.eternialib.core.configurations;

import br.com.eterniaserver.eternialib.Constants;
import br.com.eterniaserver.eternialib.core.enums.Commands;
import br.com.eterniaserver.eternialib.core.baseobjects.CommandLocale;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CommandsLocaleCfg {

    private final CommandLocale[] defaults = new CommandLocale[Commands.values().length];

    public CommandsLocaleCfg() throws IOException {
        final String commandPerm = "eternia.command";

        addDefault(Commands.COMMAND,
                "command",
                commandPerm,
                " <página>",
                " Receba ajuda para o sistema de commandos confirmaveis");

        addDefault(Commands.COMMAND_ACCEPT,
                "accept",
                commandPerm,
                null,
                " Confirme o uso de um comando");

        addDefault(Commands.COMMAND_DENY,
                "deny",
                commandPerm,
                null,
                " Negue o uso de um comando");

        addDefault(Commands.ETERNIA,
                "eternia",
                "eternia.settings",
                " <página>",
                " Receba ajuda para as configurações internas dos plugins 'Eternia'");

        addDefault(Commands.ETERNIA_RELOAD,
                "reload",
                "eternia.settings.reload",
                " <módulo>",
                " Reinicie algum módulo de algum plugin");

        // Load and save the configurations
        final FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Constants.COMMANDS_FILE_PATH));

        for (final Commands entry : Commands.values()) {
            final String cmdName = config.getString(entry.name() + ".name", defaults[entry.ordinal()].getName());
            final String cmdSyntax = config.getString(entry.name() + ".syntax", defaults[entry.ordinal()].getSyntax());
            final String cmdDescription = config.getString(entry.name() + ".description", defaults[entry.ordinal()].getDescription());
            final String cmdPerm = config.getString(entry.name() + ".perm", defaults[entry.ordinal()].getPerm());
            final String cmdAliases = config.getString(entry.name() + ".aliases", defaults[entry.ordinal()].getAliases());

            this.defaults[entry.ordinal()] = new CommandLocale(cmdName, cmdSyntax, cmdDescription, cmdPerm, cmdAliases);

            config.set(entry.name() + ".hashcode", this.defaults[entry.ordinal()].hash());
            config.set(entry.name() + ".name", cmdName);
            config.set(entry.name() + ".description", cmdDescription);
            config.set(entry.name() + ".perm", cmdPerm);
            config.set(entry.name() + ".syntax", cmdSyntax);
        }

        config.save(Constants.COMMANDS_FILE_PATH);
    }

    private void addDefault(Commands id, String name, String perm, String syntax, String description) {
        defaults[id.ordinal()] = new CommandLocale(name, syntax, description, perm, null);
    }

    public String getName(Commands id) {
        return defaults[id.ordinal()].getName();
    }

    public String getSyntax(Commands id) {
        return defaults[id.ordinal()].getSyntax() != null ? defaults[id.ordinal()].getSyntax() : "";
    }

    public String getDescription(Commands id) {
        return defaults[id.ordinal()].getDescription();
    }

    public String getPerm(Commands id) {
        return defaults[id.ordinal()].getPerm();
    }

}
