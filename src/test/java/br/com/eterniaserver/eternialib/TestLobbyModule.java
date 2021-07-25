package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.core.interfaces.ReloadableConfiguration;
import br.com.eterniaserver.eternialib.handlers.LobbyHandler;

import net.bytebuddy.utility.RandomString;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
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
    @DisplayName("Test if event will be blocked")
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

    @Test
    @DisplayName("Test if event is working")
    void testInventoryClickEvent() {
        final PlayerMock playerMock = server.addPlayer(new RandomString(16).nextString());

        playerMock.openInventory(playerMock.getInventory());
        InventoryClickEvent event = new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.ARMOR, 0, ClickType.LEFT, InventoryAction.NOTHING);

        lobbyHandler.onInventoryClick(event);
        Assertions.assertTrue(event.isCancelled());
        playerMock.closeInventory();

        Inventory inventory = Bukkit.createInventory(playerMock, 27, "false_false");
        inventory.setItem(0, new ItemStack(Material.ACACIA_SIGN));
        playerMock.openInventory(inventory);
        event = new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.MOVE_TO_OTHER_INVENTORY);

        lobbyHandler.onInventoryClick(event);
        Assertions.assertTrue(event.isCancelled());
        playerMock.closeInventory();

        inventory = Bukkit.createInventory(playerMock, 9, "§6§lServidores");
        ItemStack itemStack = new ItemStack(Material.OAK_BOAT);

        inventory.setItem(0, itemStack);
        playerMock.openInventory(inventory);
        event = new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.COLLECT_TO_CURSOR);

        lobbyHandler.onInventoryClick(event);
        Assertions.assertTrue(event.isCancelled());
        playerMock.closeInventory();

        inventory = Bukkit.createInventory(playerMock, 27);
        inventory.setItem(0, null);
        playerMock.openInventory(inventory);
        event = new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.NOTHING);

        lobbyHandler.onInventoryClick(event);
        Assertions.assertTrue(event.isCancelled());
        playerMock.closeInventory();

        inventory = Bukkit.createInventory(playerMock, 27);
        inventory.setItem(0, new ItemStack(Material.AIR));
        playerMock.openInventory(inventory);
        event = new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.NOTHING);

        lobbyHandler.onInventoryClick(event);
        Assertions.assertTrue(event.isCancelled());
        playerMock.closeInventory();

        inventory = Bukkit.createInventory(playerMock, 9, plugin.getString(Strings.SELECT_TITLE_NAME));
        itemStack = new ItemStack(Material.OAK_BOAT);
        final ItemMeta itemMeta = server.getItemFactory().getItemMeta(Material.OAK_BOAT);

        Assertions.assertNotNull(itemMeta);
        Assertions.assertNotNull(itemMeta.getPersistentDataContainer());

        itemMeta.getPersistentDataContainer().set(EterniaLib.getServerKey(), PersistentDataType.STRING, "survival");
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(0, itemStack);
        playerMock.openInventory(inventory);
        event = new InventoryClickEvent(playerMock.getOpenInventory(), InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.COLLECT_TO_CURSOR);

        lobbyHandler.onInventoryClick(event);
        Assertions.assertTrue(event.isCancelled());
        playerMock.closeInventory();

    }

}
