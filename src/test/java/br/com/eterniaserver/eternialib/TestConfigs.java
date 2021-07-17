package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;

import br.com.eterniaserver.eternialib.core.baseobjects.ItemSaveAndUseMeta;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class TestConfigs {


    @BeforeAll
    public static void setUp() {
        MockBukkit.mock();
        MockBukkit.load(EterniaLib.class);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("verify with ISUM method is working")
    void testItemSaveAndUseMetaValid() {
        final String errorStr = "error";
        final ItemStack itemStack = new ItemStack(Material.ACACIA_SIGN);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            itemMeta.setLore(List.of("test"));
        }

        itemStack.setItemMeta(itemMeta);

        final ItemSaveAndUseMeta itemSUMInvalid = new ItemSaveAndUseMeta(itemStack, 0);

        Assertions.assertNotEquals(errorStr, itemSUMInvalid.getItemName());
        Assertions.assertNotEquals(errorStr, itemSUMInvalid.getItemDisplayName());
        Assertions.assertNotEquals(List.of(errorStr), itemSUMInvalid.getItemLore());
        Assertions.assertNotEquals(errorStr, itemSUMInvalid.getItemMaterial());
        Assertions.assertEquals(0, itemSUMInvalid.getPosition());
    }

    @Test
    @DisplayName("verify with ISUM method is working")
    void testItemSaveAndUseInvalid() {
        final String errorStr = "error";
        final ItemStack itemStack = new ItemStack(Material.ACACIA_SIGN);

        final ItemSaveAndUseMeta itemSUMInvalid = new ItemSaveAndUseMeta(itemStack, 0);

        Assertions.assertEquals(errorStr, itemSUMInvalid.getItemName());
        Assertions.assertEquals(errorStr, itemSUMInvalid.getItemDisplayName());
        Assertions.assertEquals(List.of(errorStr), itemSUMInvalid.getItemLore());
        Assertions.assertEquals(errorStr, itemSUMInvalid.getItemMaterial());
        Assertions.assertEquals(0, itemSUMInvalid.getPosition());
    }

}
