package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import org.bukkit.plugin.java.JavaPlugin;

public final class EterniaLib extends JavaPlugin {

    private final boolean[] booleans = new boolean[Booleans.values().length];
    private final int[] integers = new int[Integers.values().length];
    private final String[] strings = new String[Strings.values().length];

    @Override
    public void onEnable() {
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public int getInteger(final Integers entry) {
        return integers[entry.ordinal()];
    }

    public boolean getBoolean(final Booleans entry) {
        return booleans[entry.ordinal()];
    }

    public String getString(final Strings entry) {
        return strings[entry.ordinal()];
    }

}
