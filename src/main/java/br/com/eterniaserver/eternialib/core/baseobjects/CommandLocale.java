package br.com.eterniaserver.eternialib.core.baseobjects;

public record CommandLocale(String name,
                            String syntax,
                            String description,
                            String perm,
                            String aliases) {

    @Deprecated
    public String getName() {
        return name;
    }

    @Deprecated
    public String getSyntax() {
        return syntax;
    }

    @Deprecated
    public String getDescription() {
        return description;
    }

    @Deprecated
    public String getPerm() {
        return perm;
    }

    @Deprecated
    public String getAliases() {
        return aliases;
    }

}
