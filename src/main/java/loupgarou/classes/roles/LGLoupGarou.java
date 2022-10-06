package loupgarou.classes.roles;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;

import loupgarou.App;
import loupgarou.classes.Game;
import loupgarou.classes.LGPlayer;

public class LGLoupGarou extends LGRole {
    public LGLoupGarou()
    {
        super();
        this.setCodeCouleur("§c§l");
        this.setName("Loup-Garou");
        this.setDescription(String.format("Tu gagnes avec les %sLoups-Garous§f. Tous les soir c'est festin avec les sanchz, enfin si vous arrivez à vous mettre d'accord...",this.getCodeCouleur()));
        this.setShortDescription(String.format("Tu gagnes avec les %sLoups-Garous",this.getCodeCouleur()));
        this.setNightOrder(1);
    }

    @Override
    public String toString()
    {
        return this.getName();
    }

    @Override
    public void onNightTurn(List<LGPlayer> players) {
        super.onNightTurn(players);
        Bukkit.getLogger().info("debut du tour des loups");
        for(LGPlayer player : players)
        {
            Bukkit.getLogger().info(String.format("activation vue %s", player.getName()));
            for(LGPlayer showPlayer : Game.getInGame())
            {
                player.getPlayer().showPlayer(App.getInstance(),showPlayer.getPlayer());
            }
            player.getPlayer().sendMessage("Salut à toutes et à tous les petits LG !\nGo choisir le repas de la nuit ;)");
            player.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
        }

        Game.vote(30, () -> {
            Game.setKilledByWolf();
        });
    }
}
