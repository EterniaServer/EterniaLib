package br.com.eterniaserver.eternialib;

import br.com.eterniaserver.eternialib.core.Managers;
import br.com.eterniaserver.eternialib.core.configurations.ConfigsCfg;
import br.com.eterniaserver.eternialib.core.configurations.LobbyCfg;
import br.com.eterniaserver.eternialib.core.configurations.MessagesCfg;
import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.handlers.AsyncPlayerPreLoginHandler;
import br.com.eterniaserver.eternialib.handlers.LobbyHandler;
import br.com.eterniaserver.eternialib.core.queries.CreateTable;

import br.com.eterniaserver.eternialib.core.interfaces.ReloadableConfiguration;
import br.com.eterniaserver.eternialib.core.baseobjects.CustomizableMessage;

import co.aikar.commands.PaperCommandManager;

import com.zaxxer.hikari.HikariDataSource;

import org.bstats.bukkit.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;

import javax.annotation.Nonnull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class EterniaLib extends JavaPlugin {

    private ItemStack[] itemStacks;

    private static final int[] integers = new int[Integers.values().length];
    private static final boolean[] booleans = new boolean[Booleans.values().length];
    private static final String[] strings = new String[Strings.values().length];
    private static final CustomizableMessage[] messages = new CustomizableMessage[Messages.values().length];
    private static final Map<Integer, ReloadableConfiguration> configurations = new HashMap<>();
    private static final List<String> reloadableConfig = new ArrayList<>();
    private static final List<String> protocolVersions = new ArrayList<>();

    protected static PaperCommandManager manager;

    protected static final HikariDataSource hikari = new HikariDataSource();
    protected static final File dataFolder = new File(Constants.DATABASE_FILE_PATH);

    @Override
    public void onEnable() {

        new Metrics(this, 8442);

        loadAllConfigs();
        setUpManager();
        setUpConnection();

        new Managers(this);

        CreateTable createTable = new CreateTable("el_cache");
        createTable.columns.set("uuid varchar(36)", "player_name varchar(16)");
        SQL.execute(createTable);

        this.getServer().getPluginManager().registerEvents(new AsyncPlayerPreLoginHandler(this), this);

        if (getBool(Booleans.LOBBY_SYSTEM)) {
            this.getServer().getPluginManager().registerEvents(new LobbyHandler(this), this);
        }

        if (getBool(Booleans.PROTOCOL_SUPPORT)) {
            hookIntoProtocolSupport();
        }

    }

    private void setUpManager() {

        manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");

        try {
            final String acf = "acf_messages.yml";
            final File files = new File(getDataFolder(), acf);

            if (!files.exists()) {
                saveResource(acf, false);
            }

            manager.getLocales().loadYamlLanguageFile(acf, Locale.ENGLISH);
            manager.getLocales().setDefaultLocale(Locale.ENGLISH);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    private void setUpConnection() {

        if (EterniaLib.getMySQL()) {
            hikari.setPoolName("EterniaServer MySQL Pool");
            hikari.setJdbcUrl("jdbc:mysql://" + getString(Strings.SQL_HOST) +
                    ":" + getString(Strings.SQL_PORT) +
                    "/" + getString(Strings.SQL_DATABASE));

            hikari.addDataSourceProperty("user", getString(Strings.SQL_USER));
            hikari.addDataSourceProperty("password", getString(Strings.SQL_PASSWORD));
            hikari.setMaximumPoolSize(getInt(Integers.SQL_POOL_SIZE));
            hikari.setMinimumIdle(getInt(Integers.SQL_POOL_SIZE));
            hikari.setConnectionTestQuery("SELECT 1");
            hikari.setMaxLifetime(850000);
            hikari.setConnectionTimeout(300000);
            hikari.setIdleTimeout(120000);
            hikari.setLeakDetectionThreshold(300000);

            report(getMessage(Messages.USING_MYSQL));
            return;
        }

        try {
            Class.forName("org.sqlite.JDBC");
            if (!dataFolder.exists() && dataFolder.createNewFile()) {
                report(getMessage(Messages.FILE_CREATED));
            }
            report(getMessage(Messages.USING_SQLITE));
        } catch (IOException | ClassNotFoundException e) {
            report(getMessage(Messages.ERROR));
            this.getServer().getPluginManager().disablePlugin(this);
        }

    }

    private void hookIntoProtocolSupport() {
        for (final ProtocolVersion version : ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_4_7, ProtocolVersion.MINECRAFT_1_16_3)) {
            if (!protocolVersions.contains(version.name())) {
                ProtocolSupportAPI.disableProtocolVersion(version);
            }
        }
    }

    public List<String> getReloadableConfigList() {
        return reloadableConfig;
    }

    public void registerNewUUID(final String playerName, final UUID uuid) {
        UUIDFetcher.lookupCache.put(playerName, uuid);
    }

    public void loadAllConfigs() {
        final ConfigsCfg configsCfg = new ConfigsCfg(strings, integers, booleans, protocolVersions);
        final MessagesCfg messagesCfg = new MessagesCfg(messages);
        final LobbyCfg lobbyCfg = new LobbyCfg(this, strings, booleans, integers);

        addReloadableConfiguration("eternialib", "config", configsCfg);
        addReloadableConfiguration("eternialib", "messages", messagesCfg);
        addReloadableConfiguration("eternialib", "lobby", lobbyCfg);

        configsCfg.executeConfig();
        messagesCfg.executeConfig();
        lobbyCfg.executeConfig();
    }

    public int getInt(final Integers entry) {
        return integers[entry.ordinal()];
    }

    public boolean getBool(final Booleans entry) {
        return booleans[entry.ordinal()];
    }

    public String getString(final Strings entry) {
        return strings[entry.ordinal()];
    }

    public ItemStack[] getItemStacks() {
        return itemStacks;
    }

    public void setItemStacks(ItemStack[] itemStacks) {
        this.itemStacks = itemStacks;
    }

    public void sendMessage(final CommandSender sender, final Messages entry, final String... args) {
        sender.sendMessage(getMessage(entry, args));
    }

    public String getMessage(final Messages entry, final String... args) {
        String message = messages[entry.ordinal()].text;

        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", args[i]);
        }

        return getString(Strings.SERVER_PREFIX) + message;
    }

    public ReloadableConfiguration getReloadableConfiguration(int hashCode) {
        return configurations.get(hashCode);
    }

    /**
     * Add a {@link ReloadableConfiguration} to be possible of reload.
     *
     * @param pluginName is the name of the main plugin.
     * @param configName is the name of the configuration that will be reloadable
     * @param reloadableConfiguration is the object of configuration
     */
    public static void addReloadableConfiguration(@Nonnull final String pluginName,
                                                  @Nonnull final String configName,
                                                  @Nonnull final ReloadableConfiguration reloadableConfiguration) {
        final String cfgName = pluginName + "_" + configName;

        reloadableConfig.add(cfgName);
        configurations.put(cfgName.hashCode(), reloadableConfiguration);
    }

    /**
     * Returns a {@link Boolean} primitive that checks if the plugin is using MySQL (MariaDB).
     *
     * @return true or false
     */
    public static boolean getMySQL() {
        return booleans[Booleans.MYSQL.ordinal()];
    }

    /**
     * Sends a message to console.
     *
     * @param message is the {@link String} of message
     */
    public static void report(@Nonnull final String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

}