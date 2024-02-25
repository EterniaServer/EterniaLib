package br.com.eterniaserver.eternialib.chat;

import javax.annotation.Nonnull;

public record MessageOptions(boolean prefix, @Nonnull String... args) {

    private static final MessageOptions EMPTY = new MessageOptions();

    public MessageOptions(@Nonnull String... args) {
        this(true, args);
    }

    public static MessageOptions empty() {
        return EMPTY;
    }

}
