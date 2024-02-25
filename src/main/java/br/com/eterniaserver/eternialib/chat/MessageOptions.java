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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        MessageOptions that = (MessageOptions) obj;
        if (prefix != that.prefix || args.length != that.args.length) {
            return false;
        }

        for (int i = 0; i < args.length; i++) {
            if (!args[i].equals(that.args[i])) {
                return false;
            }
        }

        return true;
    }
}
