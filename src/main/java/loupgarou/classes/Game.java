package loupgarou.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import lombok.Setter;
import loupgarou.App;
import loupgarou.classes.roles.LGRole;
import loupgarou.classes.roles.RolesConfig;

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

    public static LGPlayer getLgPlayer(Player player)
    {
        LGPlayer lgPlayer = null;

        for(LGPlayer lPlayer : Game.getInGame())
        {
            if(lPlayer.getPlayer().equals(player))
            {
                lgPlayer = lPlayer;
                break;
            }
        }

        return lgPlayer;
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
        waitTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (LGPlayer player : getInGame()) {
                    player.getPlayer().setLevel((short) (Math.floorDiv(waitTicks, 20) + 1));
                }
                if (waitTicks == 0 || Game.allVoted() == Game.getInGame().size()) {
                    waitTask = null;
                    cancel();
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
        lgPlayers.clear();
        started = false;
    }
}
