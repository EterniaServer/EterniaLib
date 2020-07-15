package br.com.eterniaserver.eternialib;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import java.io.File;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.FileConfiguration;

public class EFiles
{
    private final FileConfiguration fileConfiguration;

    public static File fileLoad(final Plugin plugin, final String fileName) {
        final File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        return file;
    }

    public EFiles(final FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }

    public void broadcastMessage(final String message) {
        this.broadcastMessage(message, null, null, null, null, null, null);
    }

    public void broadcastMessage(final String message, final String from, final Object to) {
        this.broadcastMessage(message, from, to, null, null, null, null);
    }

    public void broadcastMessage(final String message, final String from, final Object to, final String from_2, final Object to_2) {
        this.broadcastMessage(message, from, to, from_2, to_2, null, null);
    }

    public void broadcastMessage(final String message, final String from, final Object to, final String from_2, final Object to_2, final String from_3, final Object to_3) {
        Bukkit.broadcastMessage(this.formatWithPrefix(message, from, to, from_2, to_2, from_3, to_3));
    }

    public void sendConsole(final String message) {
        this.sendConsole(message, null, null, null, null, null, null);
    }

    public void sendConsole(final String message, final String from, final Object to) {
        this.sendConsole(message, from, to, null, null, null, null);
    }

    public void sendConsole(final String message, final String from, final Object to, final String from_2, final Object to_2) {
        this.sendConsole(message, from, to, from_2, to_2, null, null);
    }

    public void sendConsole(final String message, final String from, final Object to, final String from_2, final Object to_2, final String from_3, final Object to_3) {
        Bukkit.getConsoleSender().sendMessage(this.formatWithPrefix(message, from, to, from_2, to_2, from_3, to_3));
    }

    public void sendMessage(final String message, final CommandSender sender) {
        this.sendMessage(message, null, null, null, null, null, null, sender);
    }

    public void sendMessage(final String message, final String from, final Object to, final CommandSender sender) {
        this.sendMessage(message, from, to, null, null, null, null, sender);
    }

    public void sendMessage(final String message, final String from, final Object to, final String from_2, final Object to_2, final CommandSender sender) {
        this.sendMessage(message, from, to, from_2, to_2, null, null, sender);
    }

    public void sendMessage(final String message, final String from, final Object to, final String from_2, final Object to_2, final String from_3, final Object to_3, final CommandSender sender) {
        sender.sendMessage(this.formatWithPrefix(message, from, to, from_2, to_2, from_3, to_3));
    }

    private String formatWithPrefix(String message, final String from, final Object to, final String from_2, final Object to_2, final String from_3, final Object to_3) {
        message = this.getMessageWithPrefix(message);
        if (from != null) {
            message = message.replace(from, String.valueOf(to));
        }
        if (from_2 != null) {
            message = message.replace(from_2, String.valueOf(to_2));
        }
        if (from_3 != null) {
            message = message.replace(from_3, String.valueOf(to_3));
        }
        return message;
    }

    public String getMessageWithPrefix(final String valor) {
        return this.getColor(this.getString("server.prefix") + this.getString(valor));
    }

    public String getMessage(final String valor) {
        return this.getColor(this.getString(valor));
    }

    public String getColor(final String valor) {
        return ChatColor.translateAlternateColorCodes('&', valor);
    }

    public String getString(final String valor) {
        return this.fileConfiguration.getString(valor, "&7Erro&8: &7String &3" + valor + " &7n\u00e3o encontrada&8.");
    }
}