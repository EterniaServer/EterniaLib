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

    public MessagesCfg(final CustomizableMessage[] messages) {
        this.messages = messages;
    }

    @Override
    public ConfigurationCategory category() {
        return ConfigurationCategory.GENERIC;
    }

    @Override
    public void executeConfig() {

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

        // Load and save the configuration
        final FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Constants.MESSAGES_FILE_PATH));

        for (final Messages entry : Messages.values()) {
            CustomizableMessage defaultMsg = messages[entry.ordinal()];

            if (defaultMsg == null) {
                defaultMsg = new CustomizableMessage("Mensagem faltando para $3" + entry.name() + "$8.", null);
            }

            String messageStr = config.getString(entry.name() + ".text", defaultMsg.text);
            config.set(entry.name() + ".text", messageStr);
            messageStr = messageStr.replace('$', (char) 0x00A7);

            String noteStr = null;
            if (defaultMsg.getNotes() != null) {
                noteStr = config.getString(entry.name() + ".notes", defaultMsg.getNotes());
                config.set(entry.name() + ".notes", noteStr);
            }

            messages[entry.ordinal()] = new CustomizableMessage(messageStr, noteStr);

        }

        try {
            config.save(Constants.MESSAGES_FILE_PATH);
        } catch (IOException ignored) { }

    }

    private void addDefault(final Messages entry, final String text, final String notes) {
        messages[entry.ordinal()] = new CustomizableMessage(text, notes);
    }


}
