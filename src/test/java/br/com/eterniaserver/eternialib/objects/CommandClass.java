package br.com.eterniaserver.eternialib.objects;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;

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

}
