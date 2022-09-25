package loupgarou.classes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.Getter;
import lombok.Setter;
import loupgarou.classes.roles.LGRole;
import loupgarou.classes.roles.RolesConfig;

public class LGPlayer {

    @Getter
    private Player player;
    private LGRole role;
    @Getter
    private String name;
    @Getter
    private boolean dead;
    @Getter
    @Setter
    private boolean allowVote = false;
    @Getter
    @Setter
    private boolean hasVoted = false;
    @Getter @Setter
    private LGPlayer votedPlayer;
    @Getter
    @Setter
    private Integer vote = 0;

    public LGPlayer(Player player, RolesConfig role) {
        this.player = player;
        this.name = player.getDisplayName();
        this.role = LGRole.ParseRole(role.getName());
        this.player.sendTitle(String.format("Tu es %s%s", this.role.getCodeCouleur(), this.role.getName()),
                this.role.getShortDescription(), 10, 50, 20);
        this.player.sendMessage(String.format("Tu es %s%s", this.role.getCodeCouleur(), this.role.getName()));
        // this.player.sendMessage(this.role.getShortDescription());
        this.player.sendMessage(this.role.getDescription());
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 2, false, false));
    }

    public void vote(LGPlayer newVotedPlayer) {
        if (allowVote) {
            Bukkit.getLogger().info(newVotedPlayer.getName());
            if(this.votedPlayer != null)
            {
                this.votedPlayer.setVote(this.votedPlayer.getVote()-1);
            }
            if(this.votedPlayer == null || !this.votedPlayer.equals(newVotedPlayer))
            {
                newVotedPlayer.setVote(newVotedPlayer.getVote() + 1);
                this.setVotedPlayer(votedPlayer);
                this.hasVoted = true;
                this.getPlayer().sendMessage(String.format("Tu vote pour %s", newVotedPlayer.equals(this) ? "toi-même" : newVotedPlayer.getName()));
            }
        }
    }

    public void remove() {
        this.player = null;
    }
}
