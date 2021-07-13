package br.com.eterniaserver.eternialib;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

class TestClassBlocking {

    @Test
    @DisplayName("Confirm that no one can instantiate the CommandManager")
    void commandManager() {
        final var constructor = CommandManager.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("Confirm that no one can instantiate the UUIDFetcher")
    void uuidFetcher() {
        final var constructor = UUIDFetcher.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("Confirm that no one can instantiate the SQL")
    void sql() {
        final var constructor = SQL.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("Confirm that no one can instantiate the Constants")
    void constants() {
        final var constructor = Constants.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("Confirm that no one can instantiate the CmdConfirmationManager")
    void cmdConfirmationManager() {
        final var constructor = CmdConfirmationManager.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

}
