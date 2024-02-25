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

}