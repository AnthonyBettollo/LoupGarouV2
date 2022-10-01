package loupgarou.classes;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;

import lombok.Getter;
import lombok.Setter;
import loupgarou.App;
import loupgarou.classes.roles.LGRole;
import loupgarou.classes.roles.RolesConfig;
import loupgarou.classes.wrappers.WrapperPlayServerPlayerInfo;

public class LGPlayer {

    @Getter
    private Player player;
    @Getter
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
    @Getter
    @Setter
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
            Bukkit.getLogger().info(String.format("%s vote pour %s", getPlayer().getName(), newVotedPlayer.getName()));
            if (this.votedPlayer == null || !this.votedPlayer.getName().equals(newVotedPlayer.getName())) {
                if (this.votedPlayer != null) {
                    this.votedPlayer.setVote(this.votedPlayer.getVote() - 1);
                }
                newVotedPlayer.setVote(newVotedPlayer.getVote() + 1);
                this.setVotedPlayer(newVotedPlayer);
                this.hasVoted = true;
                this.getPlayer().sendMessage(String.format("Tu vote pour %s",
                        newVotedPlayer.equals(this) ? "toi-même" : newVotedPlayer.getName()));
            }
        }
    }

    public void updateSkin() {
        if (Game.isStarted()) {
            Bukkit.getLogger().info("Update all skins");
            for (LGPlayer lgp : Game.getInGame()) {
                if (lgp == this) {
                    WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
                    ArrayList<PlayerInfoData> infos = new ArrayList<PlayerInfoData>();
                    info.setAction(PlayerInfoAction.ADD_PLAYER);
                    infos.add(new PlayerInfoData(new WrappedGameProfile(getPlayer().getUniqueId(), getName()), 0,
                            NativeGameMode.ADVENTURE, WrappedChatComponent.fromText(getName())));
                    info.setData(infos);
                    info.sendPacket(getPlayer());
                } else if (!isDead()) {
                    getPlayer().hidePlayer(App.getInstance(), lgp.getPlayer());
                    getPlayer().showPlayer(App.getInstance(), lgp.getPlayer());
                    getPlayer().spigot().respawn();
                }
            }
        }
    }

    public void updateOwnSkin() {
        Bukkit.getLogger().info("Update own skin");
        if (Game.isStarted()) {
            // On change son skin avec un packet de PlayerInfo (dans le tab)
            getPlayer().spigot().respawn();
            WrapperPlayServerPlayerInfo infos = new WrapperPlayServerPlayerInfo();
            infos.setAction(PlayerInfoAction.REMOVE_PLAYER);
            WrappedGameProfile gameProfile = new WrappedGameProfile(getPlayer().getUniqueId(), getPlayer().getName());
            infos.setData(Arrays.asList(new PlayerInfoData(gameProfile, 10, NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText(getPlayer().getName()))));
            infos.sendPacket(getPlayer());

            infos.setAction(PlayerInfoAction.ADD_PLAYER);
            infos.sendPacket(getPlayer());
            getPlayer().spigot().respawn();

            // vide
            getPlayer().teleport(getPlayer().getLocation());
            float speed = getPlayer().getWalkSpeed();
            getPlayer().setWalkSpeed(0.2f);
            new BukkitRunnable() {

                @Override
                public void run() {
                    getPlayer().updateInventory();
                    getPlayer().setWalkSpeed(speed);
                }
            }.runTaskLater(App.getInstance(), 5);
        }
    }

    public void remove() {
        this.player = null;
    }
}
