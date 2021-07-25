package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.interfaces.ReloadableConfiguration;
import br.com.eterniaserver.eternialib.handlers.LobbyHandler;

import net.bytebuddy.utility.RandomString;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class TestLobbyModule {

    private static ServerMock server;
    private static EterniaLib plugin;
    private static LobbyHandler lobbyHandler;

    @BeforeAll
    public static void setUp() throws IOException, NoSuchFieldException, IllegalAccessException {
        server = MockBukkit.mock();
        final FileConfiguration file = YamlConfiguration.loadConfiguration(new File(Constants.CONFIG_FILE_PATH));
        file.set("sql.mysql", false);
        file.set("lobby.enabled", true);
        file.save(Constants.CONFIG_FILE_PATH);

        plugin = MockBukkit.load(EterniaLib.class);

        final Field field = EterniaLib.class.getDeclaredField("itemStacks");
        field.setAccessible(true);

        lobbyHandler = new LobbyHandler(plugin, (List<ItemStack>) field.get(plugin));
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test with event will be blocked")
    void testItemSwapEvent() throws NoSuchFieldException, IllegalAccessException {
        final PlayerMock playerMock = server.addPlayer(new RandomString(16).nextString());
        final ItemStack firstItem = new ItemStack(Material.OAK_SIGN);
        final ItemStack anotherItem = new ItemStack(Material.ACACIA_DOOR);

        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(playerMock, firstItem, anotherItem);
        lobbyHandler.onPlayerSwapHandItems(event);
        Assertions.assertTrue(event.isCancelled());

        final Field field = EterniaLib.class.getDeclaredField("booleans");
        field.setAccessible(true);
        final boolean[] booleans = (boolean[]) field.get(plugin);
        booleans[Booleans.BLOCK_SWAP_ITEMS.ordinal()] = false;

        event = new PlayerSwapHandItemsEvent(playerMock, firstItem, anotherItem);
        lobbyHandler.onPlayerSwapHandItems(event);
        Assertions.assertFalse(event.isCancelled());
    }

}
