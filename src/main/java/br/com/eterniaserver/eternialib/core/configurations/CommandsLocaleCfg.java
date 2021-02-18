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

    public CommandsLocaleCfg() {

        addDefault(Commands.COMMAND,
                "command",
                "eternia.command",
                " <página>",
                " Receba ajuda para o sistema de commandos confirmaveis",
                null);

        addDefault(Commands.COMMAND_ACCEPT,
                "accept",
                "eternia.command",
                null,
                " Confirme o uso de um comando",
                null);

        addDefault(Commands.COMMAND_DENY,
                "deny",
                "eternia.command",
                null,
                " Negue o uso de um comando",
                null);

        addDefault(Commands.ETERNIA,
                "eternia",
                "eternia.settings",
                " <página>",
                " Receba ajuda para as configurações internas dos plugins 'Eternia'",
                null);

        addDefault(Commands.ETERNIA_RELOAD,
                "reload",
                "eternia.settings.reload",
                " <módulo>",
                " Reinicie algum módulo de algum plugin",
                null);

        // Load and save the configurations
        final FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Constants.COMMANDS_FILE_PATH));

        for (final Commands entry : Commands.values()) {
            final CommandLocale commandLocale = defaults[entry.ordinal()];

            this.defaults[entry.ordinal()].name = config.getString(entry.name() + ".name", commandLocale.name);
            config.set(entry.name() + ".name", this.defaults[entry.ordinal()].name);

            if (commandLocale.syntax != null) {
                this.defaults[entry.ordinal()].syntax = config.getString(entry.name() + ".syntax", commandLocale.syntax);
                config.set(entry.name() + ".syntax", this.defaults[entry.ordinal()].syntax);
            }

            this.defaults[entry.ordinal()].description = config.getString(entry.name() + ".description", commandLocale.description);
            config.set(entry.name() + ".description", this.defaults[entry.ordinal()].description);

            this.defaults[entry.ordinal()].perm = config.getString(entry.name() + ".perm", commandLocale.perm);
            config.set(entry.name() + ".perm", this.defaults[entry.ordinal()].perm);

            if (commandLocale.aliases != null) {
                this.defaults[entry.ordinal()].aliases = config.getString(entry.name() + ".aliases", commandLocale.aliases);
                config.set(entry.name() + ".aliases", this.defaults[entry.ordinal()].aliases);
            }

        }

        try {
            config.save(Constants.COMMANDS_FILE_PATH);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    private void addDefault(Commands id, String name, String perm, String syntax, String description, String aliases) {
        defaults[id.ordinal()] = new CommandLocale(name, syntax, description, perm, aliases);
    }

    public String getName(Commands id) {
        return defaults[id.ordinal()].name;
    }

    public String getSyntax(Commands id) {
        return defaults[id.ordinal()].syntax != null ? defaults[id.ordinal()].syntax : "";
    }

    public String getDescription(Commands id) {
        return defaults[id.ordinal()].description;
    }

    public String getPerm(Commands id) {
        return defaults[id.ordinal()].perm;
    }

    public String getAliases(Commands id) {
        return defaults[id.ordinal()].aliases != null ? defaults[id.ordinal()].aliases : "";
    }


}
