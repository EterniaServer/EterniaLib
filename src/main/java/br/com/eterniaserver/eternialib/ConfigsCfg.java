package br.com.eterniaserver.eternialib;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigsCfg {

    public static final String DATA_LAYER_FOLDER_PATH = "plugins" + File.separator + "EterniaLib";
    public static final String CONFIG_FILE_PATH = DATA_LAYER_FOLDER_PATH + File.separator + "configs.yml";
    public static final String DATABASE_FILE_PATH = DATA_LAYER_FOLDER_PATH + File.separator + "eternia.db";

    protected final boolean mysql;

    protected final String host;
    protected final String port;
    protected final String database;
    protected final String user;
    protected final String password;

    protected final int poolSize;

    protected final String msgLoadCache;
    protected final String msgUsingMySQL;
    protected final String msgUsingSQLite;
    protected final String msgError;
    protected final String msgCreateFile;

    protected final boolean v1_4;
    protected final boolean v1_5;
    protected final boolean v1_6;
    protected final boolean v1_7;
    protected final boolean v1_8;
    protected final boolean v1_9;
    protected final boolean v1_10;
    protected final boolean v1_11;
    protected final boolean v1_12;
    protected final boolean v1_13;
    protected final boolean v1_14;
    protected final boolean v1_15;

    public ConfigsCfg() {

        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(CONFIG_FILE_PATH));
        FileConfiguration outConfig = new YamlConfiguration();

        this.mysql = config.getBoolean("sql.mysql", false);

        this.host = config.getString("sql.host", "127.0.0.1");
        this.port = config.getString("sql.port", "3306");
        this.database = config.getString("sql.database", "admin");
        this.user = config.getString("sql.user", "admin");
        this.password = config.getString("sql.password", "admin");

        this.poolSize = config.getInt("sql.pool-size", 15);

        this.msgLoadCache = config.getString("messages.load-cache" ,"$8[$aE$9L$8] $7Carregados $3{0}$7 arquivos de jogadores$8.").replace('$', (char) 0x00A7);
        this.msgUsingMySQL = config.getString("messages.load-mysql", "$8[$aE$9L$8] $7Conexão $3MySQL $7feita com sucesso$8.").replace('$', (char) 0x00A7);
        this.msgUsingSQLite = config.getString("messages.load-sqlite", "$8[$aE$9L$8] $7Conexão $3SQLite $7feita com sucesso$8.").replace('$', (char) 0x00A7);
        this.msgError = config.getString("messages.connection-error", "$8[$aE$9L$8] $7Erro ao se conectar a database$8.").replace('$', (char) 0x00A7);
        this.msgCreateFile = config.getString("messages.create-file", "$8[$aE$9L$8] $7Criando arquivo SQLite$8.").replace('$', (char) 0x00A7);

        outConfig.set("sql.mysql", this.mysql);

        outConfig.set("sql.host", this.host);
        outConfig.set("sql.port", this.port);
        outConfig.set("sql.database", this.database);
        outConfig.set("sql.user", this.user);
        outConfig.set("sql.password", this.password);

        outConfig.set("sql.pool-size", this.poolSize);

        outConfig.set("messages.load-cache", this.msgLoadCache);
        outConfig.set("messages.load-mysql", this.msgUsingMySQL);
        outConfig.set("messages.load-sqlite", this.msgUsingSQLite);
        outConfig.set("messages.connection-error", this.msgError);
        outConfig.set("messages.create-file", this.msgCreateFile);

        this.v1_4 = config.getBoolean("version-enabled.v1_4", false);
        this.v1_5 = config.getBoolean("version-enabled.v1_5", false);
        this.v1_6 = config.getBoolean("version-enabled.v1_6", false);
        this.v1_7 = config.getBoolean("version-enabled.v1_7", false);
        this.v1_8 = config.getBoolean("version-enabled.v1_8", false);
        this.v1_9 = config.getBoolean("version-enabled.v1_9", false);
        this.v1_10 = config.getBoolean("version-enabled.v1_10", false);
        this.v1_11 = config.getBoolean("version-enabled.v1_11", false);
        this.v1_12 = config.getBoolean("version-enabled.v1_12", false);
        this.v1_13 = config.getBoolean("version-enabled.v1_13", false);
        this.v1_14 = config.getBoolean("version-enabled.v1_14", false);
        this.v1_15 = config.getBoolean("version-enabled.v1_15", false);
        outConfig.set("version-enabled.v1_4", this.v1_4);
        outConfig.set("version-enabled.v1_5", this.v1_5);
        outConfig.set("version-enabled.v1_6", this.v1_6);
        outConfig.set("version-enabled.v1_7", this.v1_7);
        outConfig.set("version-enabled.v1_8", this.v1_8);
        outConfig.set("version-enabled.v1_9", this.v1_9);
        outConfig.set("version-enabled.v1_10", this.v1_10);
        outConfig.set("version-enabled.v1_11", this.v1_11);
        outConfig.set("version-enabled.v1_12", this.v1_12);
        outConfig.set("version-enabled.v1_13", this.v1_13);
        outConfig.set("version-enabled.v1_14", this.v1_14);
        outConfig.set("version-enabled.v1_15", this.v1_15);

        try {
            outConfig.save(CONFIG_FILE_PATH);
        } catch (IOException exception) {
            EterniaLib.report("$8[$aE$9L$8] $7Erroa o salvar arquivo na pasta$8.".replace('$', (char) 0x00A7));
            exception.printStackTrace();
        }

    }

}
