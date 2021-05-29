package br.com.eterniaserver.eternialib;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handlers the UUIDs of players that Eternia's plugins will use.
 */
public final class UUIDFetcher {

    protected static final Map<String, UUID> lookupCache = new HashMap<>();

    /**
     * Static class should not be initialized.
     */
    private UUIDFetcher() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Receive the {@link UUID} of the player if he has played before, otherwise it returns null.
     *
     * @param playerName is the player's name
     * @return player {@link UUID} or null
     */
    public static UUID getUUIDOf(final String playerName) {
        return lookupCache.get(playerName);
    }

}