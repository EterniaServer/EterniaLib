package br.com.eterniaserver.eternialib.core.configs;

import br.com.eterniaserver.eternialib.chat.ChatCommons;
import br.com.eterniaserver.eternialib.chat.MessageMap;
import br.com.eterniaserver.eternialib.chat.impl.ChatCommonsImpl;
import br.com.eterniaserver.eternialib.configuration.interfaces.CmdConfiguration;
import br.com.eterniaserver.eternialib.configuration.CommandLocale;
import br.com.eterniaserver.eternialib.configuration.interfaces.MsgConfiguration;
import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.configuration.interfaces.ReloadableConfiguration;
import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Commands;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.database.Database;
import br.com.eterniaserver.eternialib.database.HikariSourceConfiguration;
import br.com.eterniaserver.eternialib.database.impl.SQLDatabase;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;

public class CoreCfg implements ReloadableConfiguration, MsgConfiguration<Messages>, CmdConfiguration<Commands> {

    private static final String ERROR_LIST_NOTE = "Lista de erros";
    private static final String CONFIG_NAME_NOTE = "Nome da configuração";

    private static final String PLUGIN_PATH = "plugins" + File.separator + "EterniaLib";
    private static final String FOLDER_PATH = PLUGIN_PATH + File.separator + "config";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "core.yml";

    private final EnumMap<Strings, String> strings;
    private final EnumMap<Integers, Integer> integers;
    private final EnumMap<Booleans, Boolean> booleans;

    private final Consumer<ChatCommons> chatCommonsConsumer;
    private final Consumer<Database> databaseConsumer;

    private final MessageMap<Messages, String> messages;

    private final FileConfiguration inConfig;
    private final FileConfiguration outConfig;

    public CoreCfg(Consumer<ChatCommons> chatConsumer,
                   Consumer<Database> dbConsumer,
                   EnumMap<Strings, String> strings,
                   EnumMap<Integers, Integer> integers,
                   EnumMap<Booleans, Boolean> booleans) {
        this.chatCommonsConsumer = chatConsumer;
        this.databaseConsumer = dbConsumer;

        this.strings = strings;
        this.integers = integers;
        this.booleans = booleans;

        this.messages = new MessageMap<>(Messages.class, Messages.SERVER_PREFIX);

        this.inConfig = YamlConfiguration.loadConfiguration(new File(getFilePath()));
        this.outConfig = new YamlConfiguration();
    }

    @Override
    public FileConfiguration inFileConfiguration() {
        return inConfig;
    }

    @Override
    public FileConfiguration outFileConfiguration() {
        return outConfig;
    }

    @Override
    public String getFolderPath() {
        return FOLDER_PATH;
    }

    @Override
    public String getFilePath() {
        return FILE_PATH;
    }

    @Override
    public MessageMap<Messages, String> messages() {
        return messages;
    }

    @Override
    public ConfigurationCategory category() {
        return ConfigurationCategory.BLOCKED;
    }

    @Override
    public void executeConfig() {
        strings.put(Strings.DATABASE_TYPE, inConfig.getString("database.type", "SQLITE"));
        strings.put(Strings.DATABASE_HOST, inConfig.getString("database.host", "sqlite.db"));
        strings.put(Strings.DATABASE_PORT, inConfig.getString("database.port", ""));
        strings.put(Strings.DATABASE_DATABASE, inConfig.getString("database.database", ""));
        strings.put(Strings.DATABASE_USER, inConfig.getString("database.user", ""));
        strings.put(Strings.DATABASE_PASSWORD, inConfig.getString("database.password", ""));
        strings.put(Strings.PLAYER_UUID_TABLE_NAME, inConfig.getString("database.player-uuid.tableName", "el_cache_uuid"));
        strings.put(Strings.CONST_LINK_COLOR, inConfig.getString("const.link-color", "#926CEB"));
        strings.put(Strings.CONST_COLOR_PATTERN, inConfig.getString("const.color-pattern-regex", "#[a-fA-F\\d]{6}|&[a-fk-or\\d]"));
        strings.put(Strings.CONST_IS_COLORED, inConfig.getString("const.is-colored-pattern-regex", "[<>]"));

        integers.put(Integers.HIKARI_MIN_POOL_SIZE, inConfig.getInt("database.hikari.pool.min-size", 10));
        integers.put(Integers.HIKARI_MAX_POOL_SIZE, inConfig.getInt("database.hikari.pool.max-size", 10));
        integers.put(Integers.HIKARI_MAX_LIFE_TIME, inConfig.getInt("database.hikari.max-life-time", 850000));
        integers.put(Integers.HIKARI_CONNECTION_TIME_OUT, inConfig.getInt("database.hikari.connection-timeout", 300000));
        integers.put(Integers.HIKARI_LEAK_THRESHOLD, inConfig.getInt("database.hikari.leak-threshold", 300000));
        integers.put(Integers.TICK_DELAY, inConfig.getInt("commands.tick-delay", 20));

        booleans.put(Booleans.HIKARI_ALLOW_POOL_SUSPENSION, inConfig.getBoolean("database.hikari.pool.allow-suspension", false));

        addMessage(
                Messages.SERVER_PREFIX,
                "#555555[#55ff55E#5555ffL#555555] "
        );
        addMessage(
                Messages.MOVED,
                "#aaaaaaVocê se moveu, por isso seu comando foi cancelado#555555."
        );
        addMessage(
                Messages.BLOCK_BRAKED,
                "#aaaaaaVocê quebrou um bloco, por isso seu comando foi cancelado#555555."
        );
        addMessage(
                Messages.JUMPED,
                "#aaaaaaVocê pulou, por isso seu comando foi cancelado#555555."
        );
        addMessage(
                Messages.SNEAKED,
                "#aaaaaaVocê se agachou, por isso seu comando foi cancelado#555555."
        );
        addMessage(
                Messages.ATTACKED,
                "#aaaaaaVocê atacou, por isso seu comando foi cancelado#555555."
        );
        addMessage(
                Messages.COMMAND_CANCELLED,
                "#aaaaaaSeu comando foi cancelado#555555."
        );
        addMessage(
                Messages.CONFIG_RELOADED,
                "#aaaaaaConfiguração {0} recarregada#555555.",
                CONFIG_NAME_NOTE
        );
        addMessage(
                Messages.CONFIG_INVALID,
                "#aaaaaaNão foi encontrado nenhuma configuração com o nome #00aaaa{0}#555555.",
                CONFIG_NAME_NOTE
        );
        addMessage(
                Messages.CONFIG_BLOCKED,
                "#aaaaaaA configuração #00aaaa{0}#aaaaaa não pode ser recarregada#555555.",
                CONFIG_NAME_NOTE
        );
        addMessage(
                Messages.CONFIG_ADVICE,
                "#aaaaaaEssa é uma configuração crítica, para recarregar adicione #00aaaa:t#aaaaaa ao final do comando#555555."
        );
        addMessage(
                Messages.CONFIG_ADVICE,
                "#aaaaaaOs seguintes códigos de log foram encontrados: #00aaaa{0}#555555.",
                ERROR_LIST_NOTE
        );
        addMessage(
                Messages.TIME_MESSAGE,
                "#aaaaaa{0}#555555. #aaaaaaTempo de restante#555555: #00aaaa{1}#555555."
        );
        addMessage(
                Messages.CONFIRMED_COMMAND_MESSAGE,
                "#aaaaaaAceite ou negue a execução do comando digitando #ffaa00/aceitar#aaaaaa ou #ffaa00/negar#555555."
        );
        addMessage(
                Messages.ACCEPT_NO_COMMAND,
                "#aaaaaaNenhum comando para ser confirmado#555555."
        );
        addMessage(
                Messages.DENY_NO_COMMAND,
                "#aaaaaaNenhum comando para ser negado#555555."
        );
        addMessage(
                Messages.ACCEPTED_COMMAND,
                "#aaaaaaComando aceito#555555."
        );
        addMessage(
                Messages.DENIED_COMMAND,
                "#aaaaaaComando negado#555555."
        );

        addCommandLocale(Commands.ETERNIA, new CommandLocale(
                "eternia",
                " <página>",
                " Receba ajuda para as configurações internas dos plugins 'Eternia'",
                "eternia.settings",
                "eternialib"
        ));
        addCommandLocale(Commands.ETERNIA_RELOAD, new CommandLocale(
                "reload",
                " <configuração>",
                " Reinicie algum módulo de algum plugin",
                "eternia.settings.reload",
                null
        ));
        addCommandLocale(Commands.ETERNIA_LOGS, new CommandLocale(
                "elogs",
                null,
                " Verifique os logs do plugin EterniaLib",
                "eternia.settings.logs",
                null

        ));
        addCommandLocale(Commands.ACCEPT, new CommandLocale(
                "accept|aceitar|confirmar",
                null,
                " Confirme a execução um comando",
                "eternia.user",
                null
        ));
        addCommandLocale(Commands.DENY, new CommandLocale(
                "deny|negar",
                null,
                " Negue a execução um comando",
                "eternia.user",
                null
        ));

        outConfig.options().setHeader(List.of(
                "Tipos de database disponíveis: MYSQL, MARIADB, POSTGRESQL",
                "Available type of database: MYSQL, MARIADB, POSTGRESQL",
                "AVISO: Todas as configurações hikari são criticas, não mexa em nada que você não sabe!",
                "WARNING: All hikari configurations are CRITICAL, don't change anything that you don't know!"
        ));

        outConfig.set("database.type", strings.get(Strings.DATABASE_TYPE));
        outConfig.set("database.host", strings.get(Strings.DATABASE_HOST));
        outConfig.set("database.port", strings.get(Strings.DATABASE_PORT));
        outConfig.set("database.database", strings.get(Strings.DATABASE_DATABASE));
        outConfig.set("database.user", strings.get(Strings.DATABASE_USER));
        outConfig.set("database.password", strings.get(Strings.DATABASE_PASSWORD));
        outConfig.set("database.player-uuid.tableName", strings.get(Strings.PLAYER_UUID_TABLE_NAME));
        outConfig.set("const.link-color", strings.get(Strings.CONST_LINK_COLOR));
        outConfig.set("const.color-pattern-regex", strings.get(Strings.CONST_COLOR_PATTERN));
        outConfig.set("const.is-colored-pattern-regex", strings.get(Strings.CONST_IS_COLORED));

        outConfig.set("database.hikari.pool.min-size", integers.get(Integers.HIKARI_MIN_POOL_SIZE));
        outConfig.set("database.hikari.pool.max-size", integers.get(Integers.HIKARI_MAX_POOL_SIZE));
        outConfig.set("database.hikari.max-life-time", integers.get(Integers.HIKARI_MAX_LIFE_TIME));
        outConfig.set("database.hikari.connection-timeout", integers.get(Integers.HIKARI_CONNECTION_TIME_OUT));
        outConfig.set("database.hikari.leak-threshold", integers.get(Integers.HIKARI_LEAK_THRESHOLD));
        outConfig.set("commands.tick-delay", integers.get(Integers.TICK_DELAY));

        outConfig.set("database.hikari.pool.allow-suspension", booleans.get(Booleans.HIKARI_ALLOW_POOL_SUSPENSION));
    }

    @Override
    public void executeCritical() {
        chatCommonsConsumer.accept(new ChatCommonsImpl(strings));

        HikariSourceConfiguration hikariConfiguration = new HikariSourceConfiguration(strings, integers, booleans);
        SQLDatabase sqlDatabase = new SQLDatabase(hikariConfiguration);

        databaseConsumer.accept(sqlDatabase);
    }
}
