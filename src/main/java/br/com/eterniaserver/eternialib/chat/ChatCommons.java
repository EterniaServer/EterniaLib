package br.com.eterniaserver.eternialib.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public interface ChatCommons {

    <E extends Enum<E>> void registerMessage(MessageMap<E, String> messagesMap);

    void sendMessage(CommandSender sender, Enum<?> messageEnum);
    void sendMessage(CommandSender sender, Enum<?> messageEnum, MessageOptions options);

    Component parseMessage(Enum<?> messageEnum);
    Component parseMessage(Enum<?> messageEnum, MessageOptions options);
    String getMessage(Enum<?> messageEnum);
    String getMessage(Enum<?> messageEnum, MessageOptions options);

    Component parseColor(String message);
    String getColor(String message);

    Component deserialize(String message);
    String serializer(Component component);
    String plain(Component component);

}
