package br.com.eterniaserver.eternialib;

import java.io.File;

public class Constants {

    public static final String DATA_LAYER_FOLDER_PATH = "plugins" + File.separator + "EterniaLib";
    public static final String CONFIG_FILE_PATH = DATA_LAYER_FOLDER_PATH + File.separator + "configs.yml";
    public static final String MESSAGES_FILE_PATH = DATA_LAYER_FOLDER_PATH + File.separator + "messages.yml";
    public static final String LOBBY_FILE_PATH = DATA_LAYER_FOLDER_PATH + File.separator + "lobby.yml";
    public static final String DATABASE_FILE_PATH = DATA_LAYER_FOLDER_PATH + File.separator + "eternia.db";

    public static final String TABLE_CACHE = "el_cache";
    public static final String UUID_STR = "uuid";
    public static final String PLAYER_NAME_STR = "player_name";

    /**
     * Static class should not be initialized.
     */
    private Constants() {
        throw new IllegalStateException("Utility class");
    }


}
