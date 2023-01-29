package br.com.eterniaserver.eternialib.database.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestEntityException {

    @Test
    void testEntityException() {
        String expect = "Test";
        EntityException entityException = new EntityException(expect);
        String result = entityException.getMessage();

        Assertions.assertEquals(expect, result);
    }

}
