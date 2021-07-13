package br.com.eterniaserver.eternialib.objects;

import co.aikar.commands.BaseCommand;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Conditions;


import org.bukkit.GameMode;
import org.bukkit.entity.Player;


@CommandAlias("%command")
public class CommandClass extends BaseCommand {

    @Default
    @CommandCompletion("@completion")
    public void test(Player player, @Optional @Conditions("pa_test") String completion) {
        if (completion != null) {
            player.sendMessage(completion);
        }
        player.sendMessage("OK");
        player.setGameMode(GameMode.SPECTATOR);
    }

    @CommandAlias("user")
    public void user(User user) {
        user.setGamemode();
    }

}
