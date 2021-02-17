package br.com.eterniaserver.eternialib.core.baseobjects;

public class CommandLocale {

    public final String name;
    public final String syntax;
    public final String description;
    public final String perm;
    public final String aliases;

    public CommandLocale(final String name, final String syntax, final String description, final String perm, final String aliases) {
        this.name = name;
        this.syntax = syntax;
        this.description = description;
        this.perm = perm;
        this.aliases = aliases;
    }

}
