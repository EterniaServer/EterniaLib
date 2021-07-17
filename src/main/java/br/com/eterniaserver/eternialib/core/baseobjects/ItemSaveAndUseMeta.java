package br.com.eterniaserver.eternialib.core.baseobjects;

import br.com.eterniaserver.eternialib.EterniaLib;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ItemSaveAndUseMeta {

    private final int position;

    private final String itemName;
    private final String itemDisplayName;
    private final List<String> itemLore;
    private final String itemMaterial;

    public ItemSaveAndUseMeta(final ItemStack itemStack, final int position) {
        final String error = "error";
        ItemMeta itemMeta;

        this.position = position;

        try {
            itemMeta = itemStack.getItemMeta();
            itemMeta.getLore();
        }
        catch (NullPointerException exception) {
            this.itemName = error;
            this.itemDisplayName = error;
            this.itemLore = List.of(error);
            this.itemMaterial = error;
            return;
        }

        this.itemName = itemMeta.getPersistentDataContainer().get(EterniaLib.getServerKey(), PersistentDataType.STRING);
        this.itemDisplayName = itemMeta.getDisplayName();
        this.itemLore = itemMeta.getLore();
        this.itemMaterial = itemStack.getType().name();
    }

    public int getPosition() {
        return position;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDisplayName() {
        return itemDisplayName;
    }

    public List<String> getItemLore() {
        return itemLore;
    }

    public String getItemMaterial() {
        return itemMaterial;
    }
}