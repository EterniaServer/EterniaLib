package br.com.eterniaserver.eternialib.chat.impl;

import br.com.eterniaserver.eternialib.chat.ChatCommons;
import br.com.eterniaserver.eternialib.chat.MessageMap;
import br.com.eterniaserver.eternialib.chat.MessageOptions;
import br.com.eterniaserver.eternialib.core.enums.Strings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.command.CommandSender;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatCommonsImpl implements ChatCommons {

    private final Pattern colorredPattern;
    private final Pattern colorPattern;
    private final MiniMessage miniMessage;

    private final Map<String, MessageMap<?, String>> messagesMaps = new HashMap<>();

    public ChatCommonsImpl(EnumMap<Strings, String> strings) {
        this.colorredPattern = Pattern.compile(strings.get(Strings.CONST_IS_COLORED));
        this.colorPattern = Pattern.compile(strings.get(Strings.CONST_COLOR_PATTERN));
        this.miniMessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(TagResolver.resolver("a", (args, context) -> {
                            String link = args.popOr("Invalid").value();
                            TextColor color = TextColor.fromHexString(strings.get(Strings.CONST_LINK_COLOR));
                            Component text = Component.text(link).color(color);
                            return Tag.styling(ClickEvent.openUrl(link), HoverEvent.showText(text));
                        }))
                        .build())
                .build();
    }

    @Override
    public <E extends Enum<E>> void registerMessage(MessageMap<E, String> messagesMap) {
        Class<E> keyType = messagesMap.getKeyType();
        String keyTypeName = keyType.getCanonicalName();

        for (E messageEnum : keyType.getEnumConstants()) {
            String message = messagesMap.get(messageEnum);
            if (message != null) {
                messagesMap.put(messageEnum, getColor(message));
            }
        }

        messagesMaps.put(keyTypeName, messagesMap);
    }

    @Override
    public void sendMessage(CommandSender sender, Enum<?> messageEnum) {
        sendMessage(sender, messageEnum, MessageOptions.empty());
    }

    @Override
    public void sendMessage(CommandSender sender, Enum<?> messageEnum, MessageOptions options) {
        Component message = parseMessage(messageEnum, options);
        sender.sendMessage(message);
    }

    @Override
    public Component parseMessage(Enum<?> messageEnum) {
        return parseMessage(messageEnum, MessageOptions.empty());
    }

    @Override
    public Component parseMessage(Enum<?> messageEnum, MessageOptions options) {
        return miniMessage.deserialize(getMessage(messageEnum, options));
    }

    @Override
    public String getMessage(Enum<?> messageEnum) {
        return getMessage(messageEnum, MessageOptions.empty());
    }

    @Override
    public String getMessage(Enum<?> messageEnum, MessageOptions options) {
        MessageMap<?, String> messages = messagesMaps.get(messageEnum.getDeclaringClass().getCanonicalName());
        String message = messages.get(messageEnum);

        String[] args = options.args();
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", args[i]);
        }

        if (options.prefix()) {
            message = messages.get(messages.getPrefixKey()) + message;
        }

        return message;
    }

    @Override
    public Component parseColor(String message) {
        return miniMessage.deserialize(getColor(message));
    }

    @Override
    public String getColor(String message) {
        if (colorredPattern.matcher(message).find()) {
            return message;
        }

        Matcher matcher = colorPattern.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, matcherReplacement(matcher.group()));
        }

        return matcher.appendTail(buffer).toString();
    }

    @Override
    public Component deserialize(String message) {
        return miniMessage.deserialize(message);
    }

    @Override
    public String serializer(Component component) {
        return miniMessage.serialize(component);
    }

    @Override
    public String plain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    private String matcherReplacement(String group) {
        if (group.charAt(0) == '&') {
            group = switch (group.charAt(1)) {
                case '0' -> "black";
                case '1' -> "dark_blue";
                case '2' -> "dark_green";
                case '3' -> "dark_aqua";
                case '4' -> "dark_red";
                case '5' -> "dark_purple";
                case '6' -> "gold";
                case '7' -> "gray";
                case '8' -> "dark_gray";
                case '9' -> "blue";
                case 'a' -> "green";
                case 'b' -> "aqua";
                case 'c' -> "red";
                case 'd' -> "light_purple";
                case 'e' -> "yellow";
                case 'f' -> "white";
                case 'k' -> "obfuscated";
                case 'l' -> "bold";
                case 'm' -> "strikethrough";
                case 'n' -> "underline";
                case 'o' -> "italic";
                case 'r' -> "reset";
                default -> group;
            };
        }

        return '<' + group + '>';
    }

}
