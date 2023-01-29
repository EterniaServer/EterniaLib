package br.com.eterniaserver.eternialib.database.dto;

import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import br.com.eterniaserver.eternialib.utils.Complete;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

class TestEntityDataDTO {

    private static EntityDataDTO<Complete> idData;
    private static EntityDataDTO<Complete> firstNameData;
    private static EntityDataDTO<Complete> uuidData;
    private static EntityDataDTO<Complete> descriptionData;
    private static EntityDataDTO<Complete> birthdateData;
    private static EntityDataDTO<Complete> moneyData;
    private static EntityDataDTO<Complete> experienceData;
    private static EntityDataDTO<Complete> endDataData;

    @BeforeAll
    public static void init() throws NoSuchMethodException, IllegalAccessException {
        idData = new EntityDataDTO<>(Complete.class, "id", "id", FieldType.INTEGER, false);
        firstNameData = new EntityDataDTO<>(Complete.class, "firstName", "first_name", FieldType.STRING, false);
        uuidData = new EntityDataDTO<>(Complete.class, "uuid", "uuid", FieldType.UUID, false);
        descriptionData = new EntityDataDTO<>(Complete.class, "description", "description", FieldType.TEXT, false);
        birthdateData = new EntityDataDTO<>(Complete.class, "birthdate", "birthdate", FieldType.DATE, false);
        moneyData = new EntityDataDTO<>(Complete.class, "money", "money", FieldType.DECIMAL, false);
        experienceData = new EntityDataDTO<>(Complete.class, "experience", "experience", FieldType.DOUBLE, false);
        endDataData = new EntityDataDTO<>(Complete.class, "endDate", "end_date", FieldType.TIMESTAMP, false);
    }

    @Test
    void testEntityDataDTOGetter() throws Throwable {
        Complete complete = new Complete();

        Integer id = 1;
        String firstName = "Nome";
        UUID uuid = UUID.randomUUID();
        String description = "Descrição";
        Date birthdate = Date.valueOf(LocalDate.of(2000, 1, 1));
        BigDecimal money = new BigDecimal("100.00");
        Double experience = 100.0;
        Timestamp dataEnd = new Timestamp(Date.valueOf(LocalDate.now()).getTime());

        complete.setId(id);
        complete.setFirstName(firstName);
        complete.setUuid(uuid);
        complete.setDescription(description);
        complete.setBirthdate(birthdate);
        complete.setMoney(money);
        complete.setExperience(experience);
        complete.setEndDate(dataEnd);

        Assertions.assertEquals(id, idData.getGetterMethod().invoke(complete));
        Assertions.assertEquals(firstName, firstNameData.getGetterMethod().invoke(complete));
        Assertions.assertEquals(uuid, uuidData.getGetterMethod().invoke(complete));
        Assertions.assertEquals(description, descriptionData.getGetterMethod().invoke(complete));
        Assertions.assertEquals(birthdate, birthdateData.getGetterMethod().invoke(complete));
        Assertions.assertEquals(money, moneyData.getGetterMethod().invoke(complete));
        Assertions.assertEquals(experience, experienceData.getGetterMethod().invoke(complete));
        Assertions.assertEquals(dataEnd, endDataData.getGetterMethod().invoke(complete));
    }

    @Test
    void testEntityDataDTOSetter() throws Throwable {
        Complete complete = new Complete();

        Integer id = 1;
        String firstName = "Nome";
        UUID uuid = UUID.randomUUID();
        String description = "Descrição";
        Date birthdate = Date.valueOf(LocalDate.of(2000, 1, 1));
        BigDecimal money = new BigDecimal("100.00");
        Double experience = 100.0;
        Timestamp dataEnd = new Timestamp(Date.valueOf(LocalDate.now()).getTime());

        idData.getSetterMethod().invoke(complete, id);
        firstNameData.getSetterMethod().invoke(complete, firstName);
        uuidData.getSetterMethod().invoke(complete, uuid);
        descriptionData.getSetterMethod().invoke(complete, description);
        birthdateData.getSetterMethod().invoke(complete, birthdate);
        moneyData.getSetterMethod().invoke(complete, money);
        experienceData.getSetterMethod().invoke(complete, experience);
        endDataData.getSetterMethod().invoke(complete, dataEnd);

        Assertions.assertEquals(id, complete.getId());
        Assertions.assertEquals(firstName, complete.getFirstName());
        Assertions.assertEquals(uuid, complete.getUuid());
        Assertions.assertEquals(description, complete.getDescription());
        Assertions.assertEquals(birthdate, complete.getBirthdate());
        Assertions.assertEquals(money, complete.getMoney());
        Assertions.assertEquals(experience, complete.getExperience());
        Assertions.assertEquals(dataEnd, complete.getEndDate());
    }

}
