package br.com.eterniaserver.eternialib.core.configs;

import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.configuration.CommandLocale;
import br.com.eterniaserver.eternialib.configuration.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.configuration.ReloadableConfiguration;
import br.com.eterniaserver.eternialib.core.enums.Booleans;
import br.com.eterniaserver.eternialib.core.enums.Commands;
import br.com.eterniaserver.eternialib.core.enums.Integers;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import br.com.eterniaserver.eternialib.core.enums.Strings;
import br.com.eterniaserver.eternialib.database.impl.SQLDatabase;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class CoreCfg implements ReloadableConfiguration {

    private static final String EMPTY_NOTE = "";
    private static final String ERROR_LIST_NOTE = "Lista de erros";
    private static final String CONFIG_NAME_NOTE = "Nome da configuração";

    private static final String PLUGIN_PATH = "plugins" + File.separator + "EterniaLib";
    private static final String FOLDER_PATH = PLUGIN_PATH + File.separator + "config";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "core.yml";

    private final EterniaLib plugin;

    private final FileConfiguration inConfig;
    private final FileConfiguration outConfig;
    private final String[] messages;
    private final String[] strings;
    private final int[] integers;
    private final boolean[] booleans;

    private final CommandLocale[] commandLocales = new CommandLocale[Commands.values().length];

    public CoreCfg(EterniaLib plugin, String[] messages, String[] strings, int[] integers, boolean[] booleans) {
        this.inConfig = YamlConfiguration.loadConfiguration(new File(getFilePath()));
        this.outConfig = new YamlConfiguration();
        this.plugin = plugin;
        this.messages = messages;
        this.strings = strings;
        this.integers = integers;
        this.booleans = booleans;
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
    public String[] messages() {
        return messages;
    }

    @Override
    public CommandLocale[] commandsLocale() {
        return commandLocales;
    }

    @Override
    public ConfigurationCategory category() {
        return ConfigurationCategory.BLOCKED;
    }

    @Override
    public void executeConfig() {
        strings[Strings.PLUGIN_PREFIX.ordinal()] = inConfig.getString("server.plugin-prefix", "<color:#555555>[<color:#55ff55>E<color:#5555ff>L<color:#555555>] ");
        strings[Strings.DATABASE_TYPE.ordinal()] = inConfig.getString("database.type", "SQLITE");
        strings[Strings.DATABASE_HOST.ordinal()] = inConfig.getString("database.host", "sqlite.db");
        strings[Strings.DATABASE_PORT.ordinal()] = inConfig.getString("database.port", "");
        strings[Strings.DATABASE_DATABASE.ordinal()] = inConfig.getString("database.database", "");
        strings[Strings.DATABASE_USER.ordinal()] = inConfig.getString("database.user", "");
        strings[Strings.DATABASE_PASSWORD.ordinal()] = inConfig.getString("database.password", "");
        strings[Strings.PLAYER_UUID_TABLE_NAME.ordinal()] = inConfig.getString("database.player-uuid.tableName", "el_cache_uuid");
        integers[Integers.HIKARI_MIN_POOL_SIZE.ordinal()] = inConfig.getInt("database.hikari.pool.min-size", 10);
        integers[Integers.HIKARI_MAX_POOL_SIZE.ordinal()] = inConfig.getInt("database.hikari.pool.max-size", 10);
        integers[Integers.HIKARI_MAX_LIFE_TIME.ordinal()] = inConfig.getInt("database.hikari.max-life-time", 850000);
        integers[Integers.HIKARI_CONNECTION_TIME_OUT.ordinal()] = inConfig.getInt("database.hikari.connection-timeout", 300000);
        integers[Integers.HIKARI_LEAK_THRESHOLD.ordinal()] = inConfig.getInt("database.hikari.leak-threshold", 300000);
        integers[Integers.COMMANDS_TICK_DELAY.ordinal()] = inConfig.getInt("commands.tick-delay", 20);
        booleans[Booleans.HIKARI_ALLOW_POOL_SUSPENSION.ordinal()] = inConfig.getBoolean("database.hikari.pool.allow-suspension", false);

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

        outConfig.set("server.plugin-prefix", strings[Strings.PLUGIN_PREFIX.ordinal()]);
        outConfig.set("database.type", strings[Strings.DATABASE_TYPE.ordinal()]);
        outConfig.set("database.host", strings[Strings.DATABASE_HOST.ordinal()]);
        outConfig.set("database.port", strings[Strings.DATABASE_PORT.ordinal()]);
        outConfig.set("database.database", strings[Strings.DATABASE_DATABASE.ordinal()]);
        outConfig.set("database.user", strings[Strings.DATABASE_USER.ordinal()]);
        outConfig.set("database.password", strings[Strings.DATABASE_PASSWORD.ordinal()]);
        outConfig.set("database.player-uuid.tableName", strings[Strings.PLAYER_UUID_TABLE_NAME.ordinal()]);
        outConfig.set("database.hikari.pool.min-size", integers[Integers.HIKARI_MIN_POOL_SIZE.ordinal()]);
        outConfig.set("database.hikari.pool.max-size", integers[Integers.HIKARI_MAX_POOL_SIZE.ordinal()]);
        outConfig.set("database.hikari.max-life-time", integers[Integers.HIKARI_MAX_LIFE_TIME.ordinal()]);
        outConfig.set("database.hikari.connection-timeout", integers[Integers.HIKARI_CONNECTION_TIME_OUT.ordinal()]);
        outConfig.set("database.hikari.leak-threshold", integers[Integers.HIKARI_LEAK_THRESHOLD.ordinal()]);
        outConfig.set("commands.tick-delay", integers[Integers.COMMANDS_TICK_DELAY.ordinal()]);
        outConfig.set("database.hikari.pool.allow-suspension", booleans[Booleans.HIKARI_ALLOW_POOL_SUSPENSION.ordinal()]);
    }

    @Override
    public void executeCritical() {
        for (Commands command : Commands.values()) {
            CommandLocale commandLocale = commandsLocale()[command.ordinal()];
            EterniaLib.getCmdManager().getCommandReplacements().addReplacements(
                    command.name().toLowerCase(), commandLocale.name(),
                    command.name().toLowerCase() + "_description", commandLocale.description(),
                    command.name().toLowerCase() + "_perm", commandLocale.perm(),
                    command.name().toLowerCase() + "_syntax", commandLocale.syntax()
            );
        }

        SQLDatabase.HikariConnection hikariConnection = new SQLDatabase.HikariConnection(this.plugin);
        SQLDatabase sqlDatabase = new SQLDatabase(
                hikariConnection.getDataSource(),
                hikariConnection.getSGBDInterface()
        );

        this.plugin.setDatabase(sqlDatabase);
    }
}
