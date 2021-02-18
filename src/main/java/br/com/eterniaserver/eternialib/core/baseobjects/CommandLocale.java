package br.com.eterniaserver.eternialib.core.baseobjects;

public class CommandLocale {

    public String name;
    public String syntax;
    public String description;
    public String perm;
    public String aliases;

    public CommandLocale(final String name, final String syntax, final String description, final String perm, final String aliases) {
        this.name = name;
        this.syntax = syntax;
        this.description = description;
        this.perm = perm;
        this.aliases = aliases;
    }

}
