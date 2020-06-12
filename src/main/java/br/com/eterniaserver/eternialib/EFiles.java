package br.com.eterniaserver.eternialib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class EFiles {

    public static File fileLoad(final Plugin plugin, final String fileName) {

        final File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) plugin.saveResource(fileName, false);
        return file;

    }

    private final FileConfiguration fileConfiguration;

    public EFiles(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }

    public void broadcastMessage(String message) {
        broadcastMessage(message, null, null, null, null, null, null);
    }

    public void broadcastMessage(String message, String from, Object to) {
        broadcastMessage(message, from, to, null, null, null, null);
    }

    public void broadcastMessage(String message, String from, Object to, String from_2, Object to_2) {
        broadcastMessage(message, from, to, from_2, to_2, null, null);
    }

    public void broadcastMessage(String message, String from, Object to, String from_2, Object to_2, String from_3, Object to_3) {
        Bukkit.broadcastMessage(formatWithPrefix(message, from, to, from_2, to_2, from_3, to_3));
    }

    public void sendConsole(String message) {
        sendConsole(message, null, null, null, null, null, null);
    }

    public void sendConsole(String message, String from, Object to) {
        sendConsole(message, from, to, null, null, null, null);
    }

    public void sendConsole(String message, String from, Object to, String from_2, Object to_2) {
        sendConsole(message, from, to, from_2, to_2, null, null);
    }

    public void sendConsole(String message, String from, Object to, String from_2, Object to_2, String from_3, Object to_3) {
        Bukkit.getConsoleSender().sendMessage(formatWithPrefix(message, from, to, from_2, to_2, from_3, to_3));
    }

    public void sendMessage(String message, CommandSender sender) {
        sendMessage(message, null, null, null, null, null, null, sender);
    }

    public void sendMessage(String message, String from, Object to, CommandSender sender) {
        sendMessage(message, from, to, null, null, null, null, sender);
    }

    public void sendMessage(String message, String from, Object to, String from_2, Object to_2, CommandSender sender) {
        sendMessage(message, from, to, from_2, to_2, null, null, sender);
    }

    public void sendMessage(String message, String from, Object to, String from_2, Object to_2, String from_3, Object to_3, CommandSender sender) {
        sender.sendMessage(formatWithPrefix(message, from, to, from_2, to_2, from_3, to_3));
    }

    private String formatWithPrefix(String message, String from, Object to, String from_2, Object to_2, String from_3, Object to_3) {
        message = getMessageWithPrefix(message);
        if (from != null) message = message.replace(from, String.valueOf(to));
        if (from_2 != null) message = message.replace(from_2, String.valueOf(to_2));
        if (from_3 != null) message = message.replace(from_3, String.valueOf(to_3));
        return message;
    }

    public String getMessageWithPrefix(String valor) {
        return getColor(getString("server.prefix") + getString(valor));
    }

    public String getMessage(String valor) {
        return getColor(getString(valor));
    }

    public String getColor(String valor) {
        return ChatColor.translateAlternateColorCodes('&', valor);
    }

    public String getString(String valor) {
        return fileConfiguration.getString(valor, "&7Erro&8: &7String &3" + valor + " &7não encontrada&8.");
    }

}
