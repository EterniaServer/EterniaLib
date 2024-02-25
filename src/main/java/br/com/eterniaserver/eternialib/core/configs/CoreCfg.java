package br.com.eterniaserver.eternialib.core.configs;

import br.com.eterniaserver.eternialib.EterniaLib;
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
import java.util.List;
import java.util.function.Consumer;

public class CoreCfg implements ReloadableConfiguration, MsgConfiguration<Messages>, CmdConfiguration<Commands> {

    private static final String ERROR_LIST_NOTE = "Lista de erros";
    private static final String CONFIG_NAME_NOTE = "Nome da configuração";

    private static final String PLUGIN_PATH = "plugins" + File.separator + "EterniaLib";
    private static final String FOLDER_PATH = PLUGIN_PATH + File.separator + "config";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "core.yml";

    private final EterniaLib plugin;

    private final Consumer<ChatCommons> chatCommonsConsumer;
    private final Consumer<Database> databaseConsumer;

    private final MessageMap<Messages, String> messages;

    private final FileConfiguration inConfig;
    private final FileConfiguration outConfig;

    public CoreCfg(Consumer<ChatCommons> chatConsumer, Consumer<Database> dbConsumer, EterniaLib plugin) {
        this.chatCommonsConsumer = chatConsumer;
        this.databaseConsumer = dbConsumer;

        this.plugin = plugin;

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
        plugin.getStrings().put(Strings.DATABASE_TYPE, inConfig.getString("database.type", "SQLITE"));
        plugin.getStrings().put(Strings.DATABASE_HOST, inConfig.getString("database.host", "sqlite.db"));
        plugin.getStrings().put(Strings.DATABASE_PORT, inConfig.getString("database.port", ""));
        plugin.getStrings().put(Strings.DATABASE_DATABASE, inConfig.getString("database.database", ""));
        plugin.getStrings().put(Strings.DATABASE_USER, inConfig.getString("database.user", ""));
        plugin.getStrings().put(Strings.DATABASE_PASSWORD, inConfig.getString("database.password", ""));
        plugin.getStrings().put(Strings.PLAYER_UUID_TABLE_NAME, inConfig.getString("database.player-uuid.tableName", "el_cache_uuid"));
        plugin.getStrings().put(Strings.CONST_LINK_COLOR, inConfig.getString("const.link-color", "#926CEB"));
        plugin.getStrings().put(Strings.CONST_COLOR_PATTERN, inConfig.getString("const.color-pattern-regex", "#[a-fA-F\\d]{6}|&[a-f]"));

        plugin.getIntegers().put(Integers.HIKARI_MIN_POOL_SIZE, inConfig.getInt("database.hikari.pool.min-size", 10));
        plugin.getIntegers().put(Integers.HIKARI_MAX_POOL_SIZE, inConfig.getInt("database.hikari.pool.max-size", 10));
        plugin.getIntegers().put(Integers.HIKARI_MAX_LIFE_TIME, inConfig.getInt("database.hikari.max-life-time", 850000));
        plugin.getIntegers().put(Integers.HIKARI_CONNECTION_TIME_OUT, inConfig.getInt("database.hikari.connection-timeout", 300000));
        plugin.getIntegers().put(Integers.HIKARI_LEAK_THRESHOLD, inConfig.getInt("database.hikari.leak-threshold", 300000));
        plugin.getIntegers().put(Integers.TICK_DELAY, inConfig.getInt("commands.tick-delay", 20));

        plugin.getBooleans().put(Booleans.HIKARI_ALLOW_POOL_SUSPENSION, inConfig.getBoolean("database.hikari.pool.allow-suspension", false));

        addMessage(
                Messages.SERVER_PREFIX,
                "<color:#555555>[<color:#55ff55>E<color:#5555ff>L<color:#555555>]"
        );
        addMessage(
                Messages.MOVED,
                "<color:#aaaaaa>Você se moveu, por isso seu comando foi cancelado<color:#555555>."
        );
        addMessage(
                Messages.BLOCK_BRAKED,
                "<color:#aaaaaa>Você quebrou um bloco, por isso seu comando foi cancelado<color:#555555>."
        );
        addMessage(
                Messages.JUMPED,
                "<color:#aaaaaa>Você pulou, por isso seu comando foi cancelado<color:#555555>."
        );
        addMessage(
                Messages.SNEAKED,
                "<color:#aaaaaa>Você se agachou, por isso seu comando foi cancelado<color:#555555>."
        );
        addMessage(
                Messages.ATTACKED,
                "<color:#aaaaaa>Você atacou, por isso seu comando foi cancelado<color:#555555>."
        );
        addMessage(
                Messages.COMMAND_CANCELLED,
                "<color:#aaaaaa>Seu comando foi cancelado<color:#555555>."
        );
        addMessage(
                Messages.CONFIG_RELOADED,
                "<color:#aaaaaa>Configuração {0} recarregada<color:#555555>.",
                CONFIG_NAME_NOTE
        );
        addMessage(
                Messages.CONFIG_INVALID,
                "<color:#aaaaaa>Não foi encontrado nenhuma configuração com o nome <color:#00aaaa>{0}<color:#555555>.",
                CONFIG_NAME_NOTE
        );
        addMessage(
                Messages.CONFIG_BLOCKED,
                "<color:#aaaaaa>A configuração <color:#00aaaa>{0}<color:#aaaaaa> não pode ser recarregada<color:#555555>.",
                CONFIG_NAME_NOTE
        );
        addMessage(
                Messages.CONFIG_ADVICE,
                "<color:#aaaaaa>Essa é uma configuração crítica, para recarregar adicione <color:#00aaaa>:t<color:#aaaaaa> ao final do comando<color:#555555>."
        );
        addMessage(
                Messages.LOG_EMPTY,
                "<color:#aaaaaa>Nenhum código de log encontrada no plugin EterniaLib<color:#555555>."
        );
        addMessage(
                Messages.LOG_LIST_OF_LOGS,
                "<color:#aaaaaa>Os seguintes códigos de log foram encontrados<color:#555555>: <color:#aaaaaa>{0}<color:#555555>."
        );
        addMessage(
                Messages.CONFIG_ADVICE,
                "<color:#aaaaaa>Os seguintes códigos de log foram encontrados: <color:#00aaaa>{0}<color:#555555>.",
                ERROR_LIST_NOTE
        );
        addMessage(
                Messages.TIME_MESSAGE,
                "<color:#aaaaaa>{0}<color:#555555>. <color:#aaaaaa>Tempo de restante<color:#555555>: <color:#00aaaa>{1}<color:#555555>."
        );
        addMessage(
                Messages.CONFIRMED_COMMAND_MESSAGE,
                "<color:#aaaaaa>Aceite ou negue a execução do comando digitando <color:#ffaa00>/aceitar<color:#aaaaaa> ou <color:#ffaa00>/negar<color:#555555>."
        );
        addMessage(
                Messages.ACCEPT_NO_COMMAND,
                "<color:#aaaaaa>Nenhum comando para ser confirmado<color:#555555>."
        );
        addMessage(
                Messages.DENY_NO_COMMAND,
                "<color:#aaaaaa>Nenhum comando para ser negado<color:#555555>."
        );
        addMessage(
                Messages.ACCEPTED_COMMAND,
                "<color:#aaaaaa>Comando aceito<color:#555555>."
        );
        addMessage(
                Messages.DENIED_COMMAND,
                "<color:#aaaaaa>Comando negado<color:#555555>."
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

        outConfig.set("database.type", plugin.getStrings().get(Strings.DATABASE_TYPE));
        outConfig.set("database.host", plugin.getStrings().get(Strings.DATABASE_HOST));
        outConfig.set("database.port", plugin.getStrings().get(Strings.DATABASE_PORT));
        outConfig.set("database.database", plugin.getStrings().get(Strings.DATABASE_DATABASE));
        outConfig.set("database.user", plugin.getStrings().get(Strings.DATABASE_USER));
        outConfig.set("database.password", plugin.getStrings().get(Strings.DATABASE_PASSWORD));
        outConfig.set("database.player-uuid.tableName", plugin.getStrings().get(Strings.PLAYER_UUID_TABLE_NAME));
        outConfig.set("const.link-color", plugin.getStrings().get(Strings.CONST_LINK_COLOR));
        outConfig.set("const.color-pattern-regex", plugin.getStrings().get(Strings.CONST_COLOR_PATTERN));

        outConfig.set("database.hikari.pool.min-size", plugin.getIntegers().get(Integers.HIKARI_MIN_POOL_SIZE));
        outConfig.set("database.hikari.pool.max-size", plugin.getIntegers().get(Integers.HIKARI_MAX_POOL_SIZE));
        outConfig.set("database.hikari.max-life-time", plugin.getIntegers().get(Integers.HIKARI_MAX_LIFE_TIME));
        outConfig.set("database.hikari.connection-timeout", plugin.getIntegers().get(Integers.HIKARI_CONNECTION_TIME_OUT));
        outConfig.set("database.hikari.leak-threshold", plugin.getIntegers().get(Integers.HIKARI_LEAK_THRESHOLD));
        outConfig.set("commands.tick-delay", plugin.getIntegers().get(Integers.TICK_DELAY));

        outConfig.set("database.hikari.pool.allow-suspension", plugin.getBooleans().get(Booleans.HIKARI_ALLOW_POOL_SUSPENSION));
    }

    @Override
    public void executeCritical() {
        chatCommonsConsumer.accept(new ChatCommonsImpl(plugin));

        HikariSourceConfiguration hikariConfiguration = new HikariSourceConfiguration(plugin);
        SQLDatabase sqlDatabase = new SQLDatabase(hikariConfiguration);

        databaseConsumer.accept(sqlDatabase);
    }
}
