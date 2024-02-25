package br.com.eterniaserver.eternialib.chat;

import javax.annotation.Nonnull;

import java.util.Arrays;

public record MessageOptions(boolean prefix, @Nonnull String... args) {

    private static final MessageOptions EMPTY = new MessageOptions();

    public MessageOptions(@Nonnull String... args) {
        this(true, args);
    }

    public static MessageOptions empty() {
        return EMPTY;
    }

    @Override
    public String toString() {
        return "{'prefix': %s, 'args': %s}".formatted(prefix, args);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof MessageOptions messageOptions) {
            return hashCode() == messageOptions.hashCode();
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(prefix);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
}
