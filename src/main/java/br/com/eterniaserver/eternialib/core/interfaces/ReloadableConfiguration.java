package br.com.eterniaserver.eternialib.core.interfaces;

import br.com.eterniaserver.eternialib.core.enums.ConfigurationCategory;

/**
 * A base class to create new {@link ReloadableConfiguration}s.
 */
public interface ReloadableConfiguration {

    ConfigurationCategory category();

    void executeConfig();

}
