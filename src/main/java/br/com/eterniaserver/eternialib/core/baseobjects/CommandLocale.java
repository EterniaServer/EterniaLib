package br.com.eterniaserver.eternialib.core.baseobjects;

public class CommandLocale {

    private final String name;
    private final String syntax;
    private final String description;
    private final String perm;
    private final String aliases;

    public CommandLocale(final String name, final String syntax, final String description, final String perm, final String aliases) {
        this.name = name;
        this.syntax = syntax;
        this.description = description;
        this.perm = perm;
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getDescription() {
        return description;
    }

    public String getPerm() {
        return perm;
    }

    public String getAliases() {
        return aliases;
    }

}
