package br.com.eterniaserver.eternialib.chat;

import br.com.eterniaserver.eternialib.chat.impl.ChatCommonsImpl;
import br.com.eterniaserver.eternialib.chat.enums.Messages;
import br.com.eterniaserver.eternialib.core.enums.Strings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.util.EnumMap;
import java.util.Map;

class TestChatCommons {

    private ChatCommons chatCommons;

    @BeforeEach
    void setup() {
        Map<Strings, String> strings = new EnumMap<>(Strings.class);
        strings.put(Strings.CONST_COLOR_PATTERN, "#[a-fA-F\\d]{6}|&[a-fk-or\\d]");
        strings.put(Strings.CONST_IS_COLORED, "[<>]");
        strings.put(Strings.CONST_LINK_COLOR, "#00ff00");


        MessageMap<Messages, String> messages = new MessageMap<>(Messages.class, Messages.SERVER_PREFIX);
        messages.put(Messages.SERVER_PREFIX, "<black>[</black><red>Lib</red><black>]</black><grey> </grey>");
        messages.put(Messages.GENERIC_MESSAGE, "<aqua>Generic</aqua><dark_grey>.</dark_grey>");
        messages.put(Messages.GENERIC_WITH_ARGS, "<aqua>Generic<dark_grey> {0}.");

        chatCommons = new ChatCommonsImpl(strings);
        chatCommons.registerMessage(messages);
    }

    @Test
    void testSendMessageOptions() {
        CommandSender sender = Mockito.mock(CommandSender.class);

        chatCommons.sendMessage(sender, Messages.GENERIC_MESSAGE, MessageOptions.empty());

        Mockito.verify(sender).sendMessage(Mockito.any(Component.class));
    }

    @Test
    void testSendMessage() {
        CommandSender sender = Mockito.mock(CommandSender.class);

        chatCommons.sendMessage(sender, Messages.GENERIC_MESSAGE);

        Mockito.verify(sender).sendMessage(Mockito.any(Component.class));
    }

    @Test
    void testParseMessage() {
        Component expect = Component.empty()
                .append(Component.text("[").color(TextColor.color(0x000000)))
                .append(Component.text("Lib").color(TextColor.color(0xFF5555)))
                .append(Component.text("]").color(TextColor.color(0x000000)))
                .append(Component.text(" ").color(TextColor.color(0xAAAAAA)))
                .append(Component.text("Generic").color(TextColor.color(0x55FFFF)))
                .append(Component.text(".").color(TextColor.color(0x555555))).compact();

        Component result = chatCommons.parseMessage(Messages.GENERIC_MESSAGE).compact();

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testParseMessageWithoutPrefix() {
        Component expect = Component.empty()
                .append(Component.text("Generic").color(TextColor.color(0x55FFFF)))
                .append(Component.text(".").color(TextColor.color(0x555555)));

        Component result = chatCommons.parseMessage(Messages.GENERIC_MESSAGE, new MessageOptions(false));

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testGetMessageWithArgs() {
        String expect = "<black>[</black><red>Lib</red><black>]</black><grey> </grey><aqua>Generic<dark_grey> Test.";

        String result = chatCommons.getMessage(Messages.GENERIC_WITH_ARGS, new MessageOptions("Test"));

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testGetMessagePrefixFalseOption() {
        MessageOptions options = new MessageOptions(false);
        String expect = "<aqua>Generic</aqua><dark_grey>.</dark_grey>";

        String result = chatCommons.getMessage(Messages.GENERIC_MESSAGE, options);

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testGetMessagePrefixTrueOption() {
        MessageOptions options = new MessageOptions(true);
        String expect = "<black>[</black><red>Lib</red><black>]</black><grey> </grey><aqua>Generic</aqua><dark_grey>.</dark_grey>";

        String result = chatCommons.getMessage(Messages.GENERIC_MESSAGE, options);

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testGetMessageEmptyOptions() {
        String expect = "<black>[</black><red>Lib</red><black>]</black><grey> </grey><aqua>Generic</aqua><dark_grey>.</dark_grey>";

        String result = chatCommons.getMessage(Messages.GENERIC_MESSAGE);

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testParseLinkTag() {
        String message = "<a:https://eterniaserver.com>EterniaServer</a>";
        Component expect = Component.text("EterniaServer")
                .hoverEvent(HoverEvent.showText(Component
                        .text("https://eterniaserver.com")
                        .color(TextColor.color(0x00ff00))))
                .clickEvent(ClickEvent.openUrl("https://eterniaserver.com"));

        Component result = chatCommons.parseColor(message);

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testDeserializeMessage() {
        String message = "<white>&aTest";

        Component expect = Component.text("&aTest").color(TextColor.color(0xffffff));
        Component result = chatCommons.deserialize(message);

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testSerializerMessage() {
        Component component = Component.text("&aTest").color(TextColor.color(0xffffff));

        String expect = "<white>&aTest";
        String result = chatCommons.serializer(component);

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testPlain() {
        Component component = Component.text("Test")
                .color(TextColor.color(0x55ffff))
                .append(Component.space())
                .append(Component.text("Plain"));
        String expect = "Test Plain";

        String result = chatCommons.plain(component);

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testParseColorNoCloseIsSameFromClose() {
        Component noClose = chatCommons.parseColor("<aqua>Test");
        Component close = chatCommons.parseColor("<aqua>Test</aqua>");

        Assertions.assertEquals(noClose, close);
    }

    @Test
    void testParseColor() {
        String message = "<aqua>Test";
        Component expect = Component.text("Test").color(TextColor.color(0x55ffff));

        Component result = chatCommons.parseColor(message);

        Assertions.assertEquals(expect, result);
    }

    @Test
    void testParseTag() {
        String message = "&0T&1e&2s&3t &4m&5e&6s&7s&8a&9g&ae &bw&ci&dt&eh &fn&ko &lt&ma&ng&os&r.";
        String expect =
                "<black>T<dark_blue>e<dark_green>s<dark_aqua>t " +
                "<dark_red>m<dark_purple>e<gold>s<gray>s<dark_gray>a<blue>g<green>e " +
                "<aqua>w<red>i<light_purple>t<yellow>h <white>n<obfuscated>o " +
                "<bold>t<strikethrough>a<underline>g<italic>s<reset>.";

        String result = chatCommons.getColor(message);

        Assertions.assertEquals(expect, result);
    }


}
