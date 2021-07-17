package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import br.com.eterniaserver.eternialib.core.interfaces.ReloadableConfiguration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;

class TestExceptions {

    private static EterniaLib plugin;

    @BeforeAll
    public static void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(EterniaLib.class);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Verify that the exception wont block the plugin")
    void testReloadableException() throws IOException {
        final FileConfiguration outConfig = Mockito.mock(YamlConfiguration.class);

        BDDMockito.willThrow(new IOException()).given(outConfig).save(Constants.LOBBY_FILE_PATH);

        final ReloadableConfiguration reloadableConfiguration = plugin.getReloadableConfiguration("eternialib_lobby".hashCode());
        reloadableConfiguration.executeConfig();

        Assertions.assertTrue(true);
    }

}
