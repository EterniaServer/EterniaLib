package br.com.eterniaserver.eternialib;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class TestClassBlocking {

    @Test
    @DisplayName("Confirm that no one can instantiate the CommandManager")
    void commandManager() {
        final Constructor<?> constructor = CommandManager.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("Confirm that no one can instantiate the UUIDFetcher")
    void uuidFetcher() {
        final Constructor<?> constructor = UUIDFetcher.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("Confirm that no one can instantiate the SQL")
    void sql() {
        final Constructor<?> constructor = SQL.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("Confirm that no one can instantiate the Constants")
    void constants() {
        final Constructor<?> constructor = Constants.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("Confirm that no one can instantiate the CmdConfirmationManager")
    void cmdConfirmationManager() {
        final Constructor<?> constructor = CmdConfirmationManager.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

}
