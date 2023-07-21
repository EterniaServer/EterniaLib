package br.com.eterniaserver.eternialib.database;

import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.exceptions.EntityException;
import br.com.eterniaserver.eternialib.utils.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestEntity {

    private static Entity<Person> personEntity;

    @BeforeAll
    public static void init() throws EntityException, NoSuchMethodException, IllegalAccessException {
        personEntity = new Entity<>(Person.class);
    }

    @Test
    void testEntityCacheCache() {
        Integer id = 1;
        Person person = new Person();
        person.setId(id);

        personEntity.addEntity(id, person);

        Assertions.assertEquals(person, personEntity.getEntity(id));

        personEntity.removeEntity(id);

        Assertions.assertNull(personEntity.getEntity(id));
    }

    @Test
    void testGetTableName() {
        String expected = "eternia_person";
        String result = personEntity.tableName();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void testGetDataDTOFromFieldName() {
        String fieldName = "firstName";

        EntityDataDTO<Person> expected = personEntity.getEntityDataDTOList().get(0);
        EntityDataDTO<Person> result = personEntity.getDataDTO(fieldName);

        Assertions.assertEquals(expected, result);
    }


}
