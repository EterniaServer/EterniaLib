package br.com.eterniaserver.eternialib.core.configurations;

import br.com.eterniaserver.eternialib.core.enums.Commands;
import br.com.eterniaserver.eternialib.core.baseobjects.CommandLocale;

public class CommandsLocaleCfg {

    private final CommandLocale[] defaults = new CommandLocale[Commands.values().length];

    public CommandsLocaleCfg() {

        addDefault(Commands.COMMAND,
                "comand",
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
