package br.com.eterniaserver.eternialib.configuration.enums;

public enum PathType {

    MESSAGE(EnumConstants.MESSAGES_PREFIX, "text"),
    MESSAGE_NOTE(EnumConstants.MESSAGES_PREFIX, "note"),
    COMMAND_NAME(EnumConstants.COMMANDS_PREFIX, "name"),
    COMMAND_SYNTAX(EnumConstants.COMMANDS_PREFIX, "syntax"),
    COMMAND_DESCRIPTION(EnumConstants.COMMANDS_PREFIX, "description"),
    COMMAND_PERMISSION(EnumConstants.COMMANDS_PREFIX, "permission"),
    COMMAND_ALIASES(EnumConstants.COMMANDS_PREFIX, "aliases");

    private final String prefix;
    private final String suffix;

    PathType(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public <E extends Enum<E>> String getPath(Enum<E> commandEnum) {
        return "%s.%s.%s".formatted(prefix, commandEnum.name(), suffix);
    }

    private static class EnumConstants {
        private static final String MESSAGES_PREFIX = "messages";
        private static final String COMMANDS_PREFIX = "commands";
    }

}
