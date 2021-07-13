package br.com.eterniaserver.eternialib.objects;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public record User(Player player) {

    public void setGameMode() {
        player.setGameMode(GameMode.ADVENTURE);
    }

}
