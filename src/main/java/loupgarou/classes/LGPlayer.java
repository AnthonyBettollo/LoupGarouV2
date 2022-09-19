package loupgarou.classes;

import org.bukkit.entity.Player;

import lombok.Getter;
import loupgarou.classes.roles.LGRole;

public class LGPlayer {

    @Getter
    private Player player;
    private LGRole role;
    private String name;

    public LGPlayer(Player player, LGRole role) {
        this.player = player;
        this.name = player.getDisplayName();
        this.role = role;
        this.player.sendMessage("Bravo " + this.name + "Tu es " + this.role + ", GL FH !");
    }

    public void remove() {
        this.player = null;
    }
}
