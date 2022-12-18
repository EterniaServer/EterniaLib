package br.com.eterniaserver.eternialib.configuration.enums;

public enum PathType {

    MESSAGE,
    MESSAGE_NOTE,
    COMMAND_NAME,
    COMMAND_SYNTAX,
    COMMAND_DESCRIPTION,
    COMMAND_PERMISSION,
    COMMAND_ALIASES;

    public <E extends Enum<E>> String getPath(Enum<E> commandEnum) {
        return switch (this) {
            case MESSAGE -> "messages.%s.text".formatted(commandEnum.name());
            case MESSAGE_NOTE -> "messages.%s.note".formatted(commandEnum.name());
            case COMMAND_NAME -> "commands.%s.name".formatted(commandEnum.name());
            case COMMAND_SYNTAX -> "commands.%s.syntax".formatted(commandEnum.name());
            case COMMAND_DESCRIPTION -> "commands.%s.description".formatted(commandEnum.name());
            case COMMAND_PERMISSION -> "commands.%s.permission".formatted(commandEnum.name());
            case COMMAND_ALIASES -> "commands.%s.aliases".formatted(commandEnum.name());
        };
    }

}
