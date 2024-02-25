package br.com.eterniaserver.eternialib.chat;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.EnumMap;

@Getter
public class MessageMap<E extends Enum<E>, V> extends EnumMap<E, V> {

    private final Class<E> keyType;
    private final E prefixKey;

    public MessageMap(@Nonnull Class<E> keyType, @Nonnull E prefixKey) {
        super(keyType);

        this.keyType = keyType;
        this.prefixKey = prefixKey;
    }

    @Override
    public String toString() {
        return "{'keyType': %s, 'prefixKey': %s}".formatted(keyType, prefixKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return 31 * keyType.hashCode() + prefixKey.hashCode();
    }
}