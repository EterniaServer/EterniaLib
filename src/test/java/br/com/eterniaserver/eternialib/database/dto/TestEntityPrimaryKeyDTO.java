package br.com.eterniaserver.eternialib.database.dto;

import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import br.com.eterniaserver.eternialib.utils.entities.Complete;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

class TestEntityPrimaryKeyDTO {

    private static EntityPrimaryKeyDTO<Complete> idKey;
    private static EntityPrimaryKeyDTO<Complete> firstNameKey;
    private static EntityPrimaryKeyDTO<Complete> descriptionKey;
    private static EntityPrimaryKeyDTO<Complete> birthdateKey;
    private static EntityPrimaryKeyDTO<Complete> moneyKey;
    private static EntityPrimaryKeyDTO<Complete> experienceKey;
    private static EntityPrimaryKeyDTO<Complete> endDataKey;

    @BeforeAll
    public static void init() throws NoSuchMethodException, IllegalAccessException {
        idKey = new EntityPrimaryKeyDTO<>(Complete.class, "id", "id", FieldType.INTEGER, true);
        firstNameKey = new EntityPrimaryKeyDTO<>(Complete.class, "firstName", "first_name", FieldType.STRING, false);
        descriptionKey = new EntityPrimaryKeyDTO<>(Complete.class, "description", "description", FieldType.TEXT, false);
        birthdateKey = new EntityPrimaryKeyDTO<>(Complete.class, "birthdate", "birthdate", FieldType.DATE, false);
        moneyKey = new EntityPrimaryKeyDTO<>(Complete.class, "money", "money", FieldType.DECIMAL, false);
        experienceKey = new EntityPrimaryKeyDTO<>(Complete.class, "experience", "experience", FieldType.DOUBLE, false);
        endDataKey = new EntityPrimaryKeyDTO<>(Complete.class, "endDate", "end_date", FieldType.TIMESTAMP, false);
    }

    @Test
    void testEntityDataDTOGetter() throws Throwable {
        Complete complete = new Complete();

        Integer id = 1;
        String firstName = "Nome";
        String description = "Descrição";
        Date birthdate = Date.valueOf(LocalDate.of(2000, 1, 1));
        BigDecimal money = new BigDecimal("100.00");
        Double experience = 100.0;
        Timestamp dataEnd = new Timestamp(Date.valueOf(LocalDate.now()).getTime());

        complete.setId(id);
        complete.setFirstName(firstName);
        complete.setDescription(description);
        complete.setBirthdate(birthdate);
        complete.setMoney(money);
        complete.setExperience(experience);
        complete.setEndDate(dataEnd);

        Assertions.assertEquals(id, idKey.getGetterMethod().invoke(complete));
        Assertions.assertEquals(firstName, firstNameKey.getGetterMethod().invoke(complete));
        Assertions.assertEquals(description, descriptionKey.getGetterMethod().invoke(complete));
        Assertions.assertEquals(birthdate, birthdateKey.getGetterMethod().invoke(complete));
        Assertions.assertEquals(money, moneyKey.getGetterMethod().invoke(complete));
        Assertions.assertEquals(experience, experienceKey.getGetterMethod().invoke(complete));
        Assertions.assertEquals(dataEnd, endDataKey.getGetterMethod().invoke(complete));
    }

    @Test
    void testEntityDataDTOSetter() throws Throwable {
        Complete complete = new Complete();

        Integer id = 1;
        String firstName = "Nome";
        String description = "Descrição";
        Date birthdate = Date.valueOf(LocalDate.of(2000, 1, 1));
        BigDecimal money = new BigDecimal("100.00");
        Double experience = 100.0;
        Timestamp dataEnd = new Timestamp(Date.valueOf(LocalDate.now()).getTime());

        idKey.getSetterMethod().invoke(complete, id);
        firstNameKey.getSetterMethod().invoke(complete, firstName);
        descriptionKey.getSetterMethod().invoke(complete, description);
        birthdateKey.getSetterMethod().invoke(complete, birthdate);
        moneyKey.getSetterMethod().invoke(complete, money);
        experienceKey.getSetterMethod().invoke(complete, experience);
        endDataKey.getSetterMethod().invoke(complete, dataEnd);

        Assertions.assertEquals(id, complete.getId());
        Assertions.assertEquals(firstName, complete.getFirstName());
        Assertions.assertEquals(description, complete.getDescription());
        Assertions.assertEquals(birthdate, complete.getBirthdate());
        Assertions.assertEquals(money, complete.getMoney());
        Assertions.assertEquals(experience, complete.getExperience());
        Assertions.assertEquals(dataEnd, complete.getEndDate());
    }

}
