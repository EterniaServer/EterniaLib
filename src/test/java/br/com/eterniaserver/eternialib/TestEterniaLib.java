package br.com.eterniaserver.eternialib;

import be.seeseemelk.mockbukkit.MockBukkit;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestEterniaLib {

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
    @DisplayName("Static Getters")
    void testGetters() {
        Assertions.assertNotNull(EterniaLib.getDatabase());
        Assertions.assertNotNull(EterniaLib.getCmdManager());
        Assertions.assertNotNull(EterniaLib.getVersion());
        Assertions.assertNotNull(EterniaLib.getAdvancedCmdManager());
    }

    @Test
    @DisplayName("Test getComponentMessage")
    void testComponentMessage() {
        Component expect = Component
                .text("Configuração eternia_central recarregada", NamedTextColor.GRAY)
                .append(Component.text(".", NamedTextColor.DARK_GRAY));
        Component result = plugin.getComponentMessage(Messages.CONFIG_RELOADED, false, "eternia_central");

        Assertions.assertEquals(expect, result);
    }

    @Test
    @DisplayName("Test getMessage")
    void testGetMessage() {
        String expect = "<color:#aaaaaa>Configuração eternia_central recarregada<color:#555555>.";
        String result = plugin.getMessage(Messages.CONFIG_RELOADED, false, "eternia_central");

        String expectWithPrefix = "<color:#555555>[<color:#55ff55>E<color:#5555ff>L<color:#555555>] <color:#aaaaaa>Configuração eternia_central recarregada<color:#555555>.";
        String resultWithPrefix = plugin.getMessage(Messages.CONFIG_RELOADED, true, "eternia_central");

        Assertions.assertEquals(expect, result);
        Assertions.assertEquals(expectWithPrefix, resultWithPrefix);
    }

    @Test
    @DisplayName("Test parseColor")
    void testParseColor() {
        String colorMessage = "<color:#555555>HELLO WORLD";

        Component expect = Component.text("HELLO WORLD").color(NamedTextColor.DARK_GRAY);
        Component result = plugin.parseColor(colorMessage);

        Assertions.assertEquals(expect, result);
    }

}
