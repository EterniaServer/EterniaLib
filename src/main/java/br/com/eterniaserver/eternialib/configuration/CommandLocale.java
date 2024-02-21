package br.com.eterniaserver.eternialib.configuration;

public record CommandLocale(String name, String syntax, String description, String perm, String aliases) {

    public CommandLocale {
        if (name == null) {
            throw new IllegalArgumentException("Command name cannot be null");
        }
        if (syntax == null) {
            syntax = "";
        }
        if (description == null) {
            description = "";
        }
        if (perm == null || perm.isEmpty()) {
            perm = "eternia.command";
        }
        if (aliases == null) {
            aliases = "";
        }
    }

}
