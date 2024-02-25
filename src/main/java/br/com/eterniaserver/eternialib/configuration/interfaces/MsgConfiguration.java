package br.com.eterniaserver.eternialib.configuration.interfaces;

import br.com.eterniaserver.eternialib.chat.MessageMap;
import br.com.eterniaserver.eternialib.configuration.enums.PathType;
import org.bukkit.configuration.file.FileConfiguration;

public interface MsgConfiguration<M extends Enum<M>> extends ReloadableConfiguration {

    MessageMap<M, String> messages();

    default void addMessage(M messagesEnum, String text, String... notes) {
        FileConfiguration inFile = inFileConfiguration();
        FileConfiguration outFile = outFileConfiguration();

        MessageMap<M, String> messages = messages();

        messages.put(messagesEnum, inFile.getString(PathType.MESSAGE.getPath(messagesEnum), text));

        outFile.set(PathType.MESSAGE.getPath(messagesEnum), messages.get(messagesEnum));

        for (int i = 0; i < notes.length; i++) {
            notes[i] = "%d: %s".formatted(i, notes[i]);
        }

        outFile.set(PathType.MESSAGE_NOTE.getPath(messagesEnum), notes);
    }

}
