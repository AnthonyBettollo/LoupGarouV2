package loupgarou.classes;

import org.bukkit.entity.Player;

import lombok.Getter;
import loupgarou.classes.roles.LGRole;
import loupgarou.classes.roles.RolesConfig;

public class LGPlayer {

    @Getter
    private Player player;
    private LGRole role;
    @Getter
    private String name;

    public LGPlayer(Player player, RolesConfig role) {
        this.player = player;
        this.name = player.getDisplayName();
        this.role = LGRole.ParseRole(role.getName());
        this.player.sendMessage(String.format("Tu es %s%s", this.role.getCodeCouleur(),this.role.getName()));
        this.player.sendMessage(this.role.getShortDescription());
        this.player.sendMessage(this.role.getDescription());
    }

    public void remove() {
        this.player = null;
    }
}
