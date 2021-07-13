package br.com.eterniaserver.eternialib.objects;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public record User(Player player) {

    public void setGamemode() {
        player.setGameMode(GameMode.ADVENTURE);
    }

}
