package loupgarou.classes;

import org.bukkit.entity.Player;

import lombok.Getter;
import loupgarou.classes.roles.LGRole;
import loupgarou.classes.roles.RolesConfig;

public class LGPlayer {

    @Getter
    private Player player;
    private LGRole role;
    private String name;

    public LGPlayer(Player player, RolesConfig role) {
        this.player = player;
        this.name = player.getDisplayName();
        this.role = LGRole.ParseRole(role.getName());
        this.player.sendMessage("Bravo " + this.name + " Tu es " + this.role.getName() + ", GL FH !");
    }

    public void remove() {
        this.player = null;
    }
}
