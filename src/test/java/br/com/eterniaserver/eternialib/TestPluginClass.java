package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;

import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import br.com.eterniaserver.eternialib.core.enums.Strings;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;

class TestPluginClass {

    private static EterniaLib plugin;

    @BeforeAll
    public static void setUp() throws IOException {
        MockBukkit.mock();
        final FileConfiguration file = YamlConfiguration.loadConfiguration(new File(Constants.CONFIG_FILE_PATH));
        file.set("sql.mysql", false);
        file.save(Constants.CONFIG_FILE_PATH);

        plugin = MockBukkit.load(EterniaLib.class);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Just check the resources")
    void testResources() {
        Assertions.assertEquals("127.0.0.1", plugin.getString(Strings.SQL_HOST));
        Assertions.assertEquals(10, plugin.getInt(Integers.SQL_POOL_SIZE));
        Assertions.assertNotNull(plugin.getMessage(Messages.ERROR));
    }

}
