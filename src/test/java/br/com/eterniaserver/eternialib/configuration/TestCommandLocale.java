package br.com.eterniaserver.eternialib.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCommandLocale {

    @Test
    void testValid() {
        CommandLocale commandLocale = new CommandLocale(
                "name",
                "syntax",
                "description",
                "perm",
                "aliases"
        );

        Assertions.assertEquals("name", commandLocale.name());
        Assertions.assertEquals("syntax", commandLocale.syntax());
        Assertions.assertEquals("description", commandLocale.description());
        Assertions.assertEquals("perm", commandLocale.perm());
        Assertions.assertEquals("aliases", commandLocale.aliases());
    }

    @Test
    void testInvalid() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CommandLocale(
                null,
                "syntax",
                "description",
                "perm",
                "aliases"
        ));
    }

    @Test
    void testNullSyntax() {
        CommandLocale commandLocale = new CommandLocale(
                "name",
                null,
                "description",
                "perm",
                "aliases"
        );

        Assertions.assertEquals("name", commandLocale.name());
        Assertions.assertEquals("", commandLocale.syntax());
        Assertions.assertEquals("description", commandLocale.description());
        Assertions.assertEquals("perm", commandLocale.perm());
        Assertions.assertEquals("aliases", commandLocale.aliases());
    }

    @Test
    void testNullDescription() {
        CommandLocale commandLocale = new CommandLocale(
                "name",
                "syntax",
                null,
                "perm",
                "aliases"
        );

        Assertions.assertEquals("name", commandLocale.name());
        Assertions.assertEquals("syntax", commandLocale.syntax());
        Assertions.assertEquals("", commandLocale.description());
        Assertions.assertEquals("perm", commandLocale.perm());
        Assertions.assertEquals("aliases", commandLocale.aliases());
    }

    @Test
    void testNullPermission() {
        CommandLocale commandLocale = new CommandLocale(
                "name",
                "syntax",
                "description",
                null,
                "aliases"
        );

        Assertions.assertEquals("name", commandLocale.name());
        Assertions.assertEquals("syntax", commandLocale.syntax());
        Assertions.assertEquals("description", commandLocale.description());
        Assertions.assertEquals("eternia.command", commandLocale.perm());
        Assertions.assertEquals("aliases", commandLocale.aliases());
    }

    @Test
    void testEmptyPermission() {
        CommandLocale commandLocale = new CommandLocale(
                "name",
                "syntax",
                "description",
                "",
                "aliases"
        );

        Assertions.assertEquals("name", commandLocale.name());
        Assertions.assertEquals("syntax", commandLocale.syntax());
        Assertions.assertEquals("description", commandLocale.description());
        Assertions.assertEquals("eternia.command", commandLocale.perm());
        Assertions.assertEquals("aliases", commandLocale.aliases());
    }

    @Test
    void testNullAliases() {
        CommandLocale commandLocale = new CommandLocale(
                "name",
                "syntax",
                "description",
                "perm",
                null
        );

        Assertions.assertEquals("name", commandLocale.name());
        Assertions.assertEquals("syntax", commandLocale.syntax());
        Assertions.assertEquals("description", commandLocale.description());
        Assertions.assertEquals("perm", commandLocale.perm());
        Assertions.assertEquals("", commandLocale.aliases());
    }

}
