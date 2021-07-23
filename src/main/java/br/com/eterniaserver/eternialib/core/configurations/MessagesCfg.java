package br.com.eterniaserver.eternialib.core.configurations;

import br.com.eterniaserver.eternialib.Constants;
import br.com.eterniaserver.eternialib.core.enums.ConfigurationCategory;
import br.com.eterniaserver.eternialib.core.enums.Messages;
import br.com.eterniaserver.eternialib.core.interfaces.ReloadableConfiguration;
import br.com.eterniaserver.eternialib.core.baseobjects.CustomizableMessage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessagesCfg implements ReloadableConfiguration {

    private final CustomizableMessage[] messages;
    private final FileConfiguration config;

    public MessagesCfg(final CustomizableMessage[] messages) {
        this.messages = messages;
        this.config = YamlConfiguration.loadConfiguration(new File(Constants.MESSAGES_FILE_PATH));
    }

    @Override
    public ConfigurationCategory category() {
        return ConfigurationCategory.GENERIC;
    }

    @Override
    public void executeConfig() throws IOException {

        addDefault(Messages.LOAD_CACHE,
                "Carregados $3{0}$7 arquivos de jogadores$8.",
                "0: quantos arquivos foram carregados");
        addDefault(Messages.ERROR,
                "Erro ao se conectar a database$8.",
                null);
        addDefault(Messages.FILE_CREATED,
                "Criando arquivo SQLite$8.",
                null);
        addDefault(Messages.USING_MYSQL,
                "Conexão $3MySQL $7feita com sucesso$8.",
                null);
        addDefault(Messages.USING_SQLITE,
                "Conexão $3SQLite $7feita com sucesso$8.",
                null);
        addDefault(Messages.CONFIG_INVALID,
                "Configuração inválida$8.",
                null);
        addDefault(Messages.CONFIG_RELOADED,
                "Configuração $3{0} $7reiniciada com sucesso$8.",
                "0: config");
        addDefault(Messages.CONFIG_WARNING,
                "Reiniciar esse arquivo pode causar problemas ou pode causar lag$8, $7para confirmar colocar $3:t$7 no final do nome da configuração$8.",
                null);
        addDefault(Messages.COMMAND_INVALID,
                "Você não possui nenhum comando para confirmar$8.",
                null);
        addDefault(Messages.COMMAND_DENIED,
                "Você cancelou a execução desse comando$8.",
                null);

        for (final Messages entry : Messages.values()) {
            final CustomizableMessage defaultMsg = messages[entry.ordinal()];

            if (defaultMsg.getNotes() == null) {
                defaultMsg.setNotes("Basic message");
            }

            final String messageStr = config.getString(entry.name() + ".text", defaultMsg.text);
            final String formattedStr = messageStr.replace('$', (char) 0x00A7);
            final String noteStr = config.getString(entry.name() + ".notes", defaultMsg.getNotes());

            config.set(entry.name() + ".notes", noteStr);
            config.set(entry.name() + ".text", messageStr);

            messages[entry.ordinal()] = new CustomizableMessage(formattedStr, noteStr);
        }

        config.save(Constants.MESSAGES_FILE_PATH);
    }

    @Override
    public void executeCritical() {
        throw new UnsupportedOperationException();
    }

    private void addDefault(final Messages entry, final String text, final String notes) {
        messages[entry.ordinal()] = new CustomizableMessage(text, notes);
    }


}
