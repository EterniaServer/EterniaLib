package br.com.eterniaserver.eternialib.database.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestDatabaseException {

    @Test
    void testDatabaseException() {
        String expect = "Test";
        DatabaseException databaseException = new DatabaseException(expect);
        String result = databaseException.getMessage();

        Assertions.assertEquals(expect, result);
    }

}
