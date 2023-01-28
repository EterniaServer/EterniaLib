package br.com.eterniaserver.eternialib.configuration.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestPathType {

    enum TestEnum {TEST}

    @Test
    void testPathTypeMessage() {
        PathType pathType = PathType.MESSAGE;
        String expected = "messages.TEST.text";

        Assertions.assertEquals(expected, pathType.getPath(TestEnum.TEST));
    }

    @Test
    void testPathTypeMessage_NOTE() {
        PathType pathType = PathType.MESSAGE_NOTE;
        String expected = "messages.TEST.note";

        Assertions.assertEquals(expected, pathType.getPath(TestEnum.TEST));
    }

    @Test
    void testPathTypeCommandName() {
        PathType pathType = PathType.COMMAND_NAME;
        String expected = "commands.TEST.name";

        Assertions.assertEquals(expected, pathType.getPath(TestEnum.TEST));
    }

    @Test
    void testPathTypeCommandSyntax() {
        PathType pathType = PathType.COMMAND_SYNTAX;
        String expected = "commands.TEST.syntax";

        Assertions.assertEquals(expected, pathType.getPath(TestEnum.TEST));
    }

    @Test
    void testPathTypeCommandDescription() {
        PathType pathType = PathType.COMMAND_DESCRIPTION;
        String expected = "commands.TEST.description";

        Assertions.assertEquals(expected, pathType.getPath(TestEnum.TEST));
    }

    @Test
    void testPathTypeCommandPermission() {
        PathType pathType = PathType.COMMAND_PERMISSION;
        String expected = "commands.TEST.permission";

        Assertions.assertEquals(expected, pathType.getPath(TestEnum.TEST));
    }

    @Test
    void testPathTypeCommandAliases() {
        PathType pathType = PathType.COMMAND_ALIASES;
        String expected = "commands.TEST.aliases";

        Assertions.assertEquals(expected, pathType.getPath(TestEnum.TEST));
    }
}
