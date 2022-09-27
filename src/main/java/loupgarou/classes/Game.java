package loupgarou.classes;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import lombok.Getter;
import lombok.Setter;
import loupgarou.App;
import loupgarou.classes.roles.LGRole;
import loupgarou.classes.roles.RolesConfig;
import loupgarou.classes.utils.SortLGPlayerByVote;

public class Game {
    @Getter
    private static ArrayList<LGPlayer> lgPlayers = new ArrayList<>();
    @Getter
    private static ArrayList<LGPlayer> inGame = new ArrayList<>();

    @Getter
    private static LGPlayer mayor;

    public void setMayor(LGPlayer mayor) {
        Game.mayor = mayor;
    }

    @Getter
    private static ArrayList<LGRole> roles = new ArrayList<>();

    @Getter
    private static boolean started;
    @Getter
    private static int night = 0;
    @Getter
    private static boolean day;
    @Getter
    @Setter
    private static int waitTicks;

    public static void start(List<Player> players, List<Location> spawnList, List<RolesConfig> roles) {
        Collections.shuffle(roles);
        int indexRole = 0;
        for (Player player : players) {
            LGPlayer newPlayer = new LGPlayer(player, roles.get(indexRole));
            lgPlayers.add(newPlayer);
            inGame.add(newPlayer);
            player.teleport(spawnList.get(indexRole));
            indexRole++;
        }

        wait(5, () -> {
            Game.broadcastMessage(
                    "Bienvenue dans cette game mes petits fratello\nPour commencer on on va choisir un maire, un leader, un boss bref celui qu'on écoute (donc pas loyo)\nVous avez 30 secondes pour faire un choix !");
            Game.vote(30, () -> {
                Game.setMayor();
            });
        });

        started = true;
    }

    public static void broadcastTitle(String title, String subTitle, Integer fadeIn, Integer stay, Integer fadeOut) {
        for (LGPlayer lgp : Game.lgPlayers)
            lgp.getPlayer().sendTitle(title, subTitle, fadeIn, stay, fadeOut);
    }

    public static void broadcastMessage(String msg) {
        for (LGPlayer lgp : Game.lgPlayers)
            lgp.getPlayer().sendMessage(msg);
    }

    public static void broadcastSpacer() {
        for (LGPlayer lgp : Game.lgPlayers)
            lgp.getPlayer().sendMessage("\n");
    }

    public static void broadcastPacket(){
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        try {
            for (LGPlayer lgp : Game.lgPlayers)
            {
                Bukkit.getLogger().info(String.format("Sending PLAYER_INFO to %s", lgp.getName()));
                manager.sendServerPacket(lgp.getPlayer(), new PacketContainer(PacketType.Play.Server.PLAYER_INFO));
                lgp.getPlayer().hidePlayer(App.getInstance(),Game.mayor.getPlayer());
                lgp.getPlayer().showPlayer(App.getInstance(),Game.mayor.getPlayer());
            }

            
        } catch (InvocationTargetException e) {
            Bukkit.getLogger().warning(String.format("Le joueur %s est déconnecté", e.getMessage()));
        }
        
    }

    public static LGPlayer getLgPlayer(Player player) {
        LGPlayer lgPlayer = null;

        for (LGPlayer lPlayer : Game.getInGame()) {
            if (lPlayer.getPlayer().equals(player)) {
                lgPlayer = lPlayer;
                break;
            }
        }

        return lgPlayer;
    }

    public static void setMayor() {
        List<LGPlayer> mayorList = new ArrayList<LGPlayer>();
        List<LGPlayer> playersList = Game.getInGame();
        Collections.sort(playersList, new SortLGPlayerByVote());
        Integer maxVote = playersList.get(0).getVote();

        for (LGPlayer player : playersList) {
            if (player.getVote() == maxVote) {
                mayorList.add(player);
            }
        }

        Collections.shuffle(mayorList);
        Game.mayor = mayorList.get(0);
        Game.broadcastMessage(String.format("%s est élu, bonne chance à lui Inch", Game.mayor.getName()));
        Game.broadcastPacket();
    }

    public static interface TextGenerator {
        public String generate(LGPlayer player, int secondsLeft);
    }

    private static BukkitTask waitTask;

    public static void cancelWait() {
        if (waitTask != null) {
            waitTask.cancel();
            waitTask = null;
        }
    }

    public static void wait(int seconds, Runnable callback) {
        Game.cancelWait();
        Game.setWaitTicks(seconds * 20);
        waitTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (LGPlayer player : getInGame()) {
                    player.getPlayer().setLevel((short) (Math.floorDiv(waitTicks, 20) + 1));
                }
                if (waitTicks == 0) {
                    waitTask = null;
                    cancel();
                    callback.run();
                }
                waitTicks--;
            }
        }.runTaskTimer(App.getInstance(), 0, 1);
    }

    public static Integer allVoted() {
        Integer nbVote = 0;
        for (LGPlayer player : getInGame()) {
            if (player.isHasVoted()) {
                nbVote++;
            }
        }
        return nbVote;
    }

    public static void vote(int seconds, Runnable callback) {
        Game.cancelWait();
        Game.setWaitTicks(seconds * 20);
        Game.GlobalSetAllowVote(true);
        waitTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (LGPlayer player : getInGame()) {
                    player.getPlayer().setLevel((short) (Math.floorDiv(waitTicks, 20) + 1));
                }
                if (Game.allVoted() == getInGame().size() && waitTicks > (10*20)) {
                    waitTicks = (10*20);
                }
                if (waitTicks == 0) {
                    waitTask = null;
                    cancel();
                    Game.GlobalSetAllowVote(false);
                    callback.run();
                }
                waitTicks--;
            }
        }.runTaskTimer(App.getInstance(), 0, 1);
    }

    public static void stop() {
        Game.broadcastTitle("Partie annulée !", "", 10, 40, 20);
        Game.broadcastMessage("La partie est annulé !");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
        GlobalSetAllowVote(false);
        inGame.clear();
        lgPlayers.clear();
        mayor = null;
        started = false;
    }

    public static void GlobalSetAllowVote(boolean value) {
        for (LGPlayer lgPlayer : Game.getInGame()) {
            Bukkit.getLogger().info(String.format("%s : %s votes", lgPlayer.getName(),lgPlayer.getVote()));
            lgPlayer.setAllowVote(value);
        }
    }
}
