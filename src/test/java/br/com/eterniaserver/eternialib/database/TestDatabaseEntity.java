package br.com.eterniaserver.eternialib.database;

import be.seeseemelk.mockbukkit.MockBukkit;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.database.exceptions.DatabaseException;
import br.com.eterniaserver.eternialib.database.exceptions.EntityException;
import br.com.eterniaserver.eternialib.utils.ProfileEntity;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TestDatabaseEntity {

    @BeforeAll
    public static void loadAndTestRegisterEntity() throws EntityException, DatabaseException {
        MockBukkit.mock();
        MockBukkit.load(EterniaLib.class);

        Entity<ProfileEntity> entity = new Entity<>(ProfileEntity.class);
        EterniaLib.getDatabase().register(ProfileEntity.class, entity);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    void insertAndGetEntityInstance() {
        ProfileEntity profile = new ProfileEntity();
        ProfileEntity getProfile;
        profile.name = "YuriNogueira";
        profile.money = new BigDecimal("15.0");

        EterniaLib.getDatabase().insert(ProfileEntity.class, profile);
        getProfile = EterniaLib.getDatabase().get(ProfileEntity.class, profile.id);

        Assertions.assertNotNull(profile.id);
        Assertions.assertNotNull(getProfile.id);
    }
}
