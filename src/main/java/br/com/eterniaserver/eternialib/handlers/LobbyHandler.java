package br.com.eterniaserver.eternialib.handlers;

import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.EterniaLib;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class LobbyHandler implements Listener {

    private final EterniaLib plugin;
    private final ItemStack selectServer;
    private final NamespacedKey serverKey;
    private final List<ItemStack> itemStacks;

    public LobbyHandler(final EterniaLib plugin, final List<ItemStack> itemStacks) {
        this.plugin = plugin;
        this.serverKey = new NamespacedKey(plugin, "eternialib-lobby");

        final ItemStack itemStack = new ItemStack(Material.COMPASS);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(this.serverKey, PersistentDataType.STRING, "open-inv");
        itemMeta.setDisplayName(plugin.getString(Strings.SELECT_TITLE_NAME));
        itemStack.setItemMeta(itemMeta);

        this.selectServer = itemStack;
        this.itemStacks = itemStacks;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {

        event.setCancelled(true);

        final Player player = (Player) event.getWhoClicked();
        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        final String title = event.getView().getTitle();

        if (!title.equals(plugin.getString(Strings.SELECT_TITLE_NAME))) {
            return;
        }

        teleportPlayerToServer(player, itemStack.getItemMeta().getPersistentDataContainer().get(this.serverKey, PersistentDataType.STRING));

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {

        final Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK || action == Action.PHYSICAL) {
            return;
        }

        final Player player = event.getPlayer();
        final ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        final PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();

        if (container == null || !container.has(this.serverKey, PersistentDataType.STRING)) {
            return;
        }

        player.closeInventory();
        final Inventory serverSelector = plugin.getServer().createInventory(
                player,
                plugin.getInt(Integers.GUI_SIZE),
                plugin.getString(Strings.SELECT_TITLE_NAME));

        for (int i = 0; i < plugin.getInt(Integers.GUI_SIZE); i++) {
            if (itemStacks.get(i) != null) {
                serverSelector.setItem(i, itemStacks.get(i));
            }
        }

        player.openInventory(serverSelector);

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {

        if (!plugin.getBool(Booleans.BLOCK_SWAP_ITEMS)) {
            return;
        }

        event.setCancelled(true);

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (!plugin.getBool(Booleans.DISABLE_ITEM_DROP)
                || event.getPlayer().hasPermission(plugin.getString(Strings.DROP_PERM))) {
            return;
        }

        event.setCancelled(true);

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(EntityPickupItemEvent event) {

        final Entity entity = event.getEntity();

        if (!(entity instanceof Player)
                || !plugin.getBool(Booleans.DISABLE_ITEM_PICKUP)
                || entity.hasPermission(plugin.getString(Strings.PICKUP_PERM))) {
            return;
        }

        event.setCancelled(true);

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerJoinEvent event) {

        if (!plugin.getBool(Booleans.CLEAR_INV)) {
            event.getPlayer().getInventory().setItem(0, new ItemStack(selectServer));
            return;
        }

        event.getPlayer().getInventory().clear();
        event.getPlayer().getInventory().setItem(0, new ItemStack(selectServer));

    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {

        final Player player = event.getPlayer();

        if (!plugin.getBool(Booleans.CLEAR_INV)) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                    () -> player.getInventory().setItem(0, new ItemStack(selectServer)));
            return;
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            player.getInventory().clear();
            player.getInventory().setItem(0, new ItemStack(selectServer));
        });

    }

    private void teleportPlayerToServer(final Player player, final String server) {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(outputStream)) {
            dos.writeUTF("Connect");
            dos.writeUTF(server);
            player.sendPluginMessage(plugin, "BungeeCord", outputStream.toByteArray());
        } catch (IOException exception) {
            Bukkit.getLogger().warning("Can't send message to BungeeCord, exception class: " + exception.getClass().getName());
        }
    }
}
