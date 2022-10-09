package loupgarou.classes;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
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
import loupgarou.classes.utils.SortLGRolesByNightOrder;
import loupgarou.classes.wrappers.WrapperPlayServerUpdateTime;

public class Game {
    @Getter
    private static ArrayList<LGPlayer> lgPlayers = new ArrayList<>();
    @Getter
    private static ArrayList<LGPlayer> inGame = new ArrayList<>();
    @Getter
    private static ArrayList<LGPlayer> lgPlayersAlive = new ArrayList<>();
    @Getter
    private static LGPlayer mayor;

    public void setMayor(LGPlayer mayor) {
        Game.mayor = mayor;
    }

    @Getter
    private static ArrayList<LGRole> roles = new ArrayList<>();

    @Getter
    private static ArrayList<LGRole> rolesAlives = new ArrayList<>();

    @Getter
    private static boolean started;
    @Getter
    private static int night = 0;
    private static int indexNightRole = 0;
    @Getter
    private static boolean day;
    @Getter
    @Setter
    private static int waitTicks;

    public static void start(List<Player> players, List<Location> spawnList, List<RolesConfig> roles) {
        Collections.shuffle(roles);
        int indexRole = 0;
        for (Player player : players) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            LGPlayer newPlayer = new LGPlayer(player, roles.get(indexRole));
            getRoles().add(newPlayer.getRole());
            lgPlayers.add(newPlayer);
            inGame.add(newPlayer);
            lgPlayersAlive.add(newPlayer);
            player.teleport(spawnList.get(indexRole));
            indexRole++;
        }
        Collections.sort(getRoles(), new SortLGRolesByNightOrder());
        for(LGRole lgRole : getRoles())
        {
            rolesAlives.add(lgRole);
        }
        wait(5, () -> {
            WrapperPlayServerUpdateTime time = new WrapperPlayServerUpdateTime();
                            time.setAgeOfTheWorld(0);
                            time.setTimeOfDay((long)(6000));
                            time.broadcastPacket();
            Game.broadcastMessage(
                    "Bienvenue dans cette game mes petits fratello\nPour commencer on on va choisir un maire, un leader, un boss bref celui qu'on écoute (donc pas loyo)\nVous avez 30 secondes pour faire un choix !");
            Game.vote(getInGame(),30, () -> {
                Game.setMayor();
                new BukkitRunnable() {
                    int timeoutLeft = 5*20;
                    @Override
                    public void run() {
                        if(--timeoutLeft <= 20+20*2) {
                            if(timeoutLeft == 20)
                            {
                                Game.nextNight();
                                cancel();
                            }
                            WrapperPlayServerUpdateTime time = new WrapperPlayServerUpdateTime();
                            time.setAgeOfTheWorld(0);
                            time.setTimeOfDay((long)(18000-(timeoutLeft-20D)/(20*2D)*12000D));
                            for(LGPlayer lgp : getInGame())
                                time.sendPacket(lgp.getPlayer());
                        }
                    }
                }.runTaskTimer(App.getInstance(), 1, 1);
                
            });
        });

        started = true;
    }

    public static void nextNight()
    {
        night++;
        Game.broadcastMessage("La nuit va commencer bonne chance les reufs (go kill Maelo)");
        broadcastMessage("§9----------- §lNuit n°"+night+"§9 -----------");
		broadcastMessage("§8§oLa nuit tombe sur le village...");
		// for(LGPlayer player : getLgPlayersAlive())
        // {
        //     player.leaveChat();
        // }
		
		for(LGPlayer lgPlayer : getInGame()) {
            WrapperPlayServerUpdateTime time = new WrapperPlayServerUpdateTime();
            time.setAgeOfTheWorld(0);
            time.setTimeOfDay((long)(14500));
            time.sendPacket(lgPlayer.getPlayer());
            for(LGPlayer hideLGPlayer : getInGame()) {
                if(hideLGPlayer == lgPlayer)
                {
                    continue;
                }
                lgPlayer.getPlayer().hidePlayer(App.getInstance(), hideLGPlayer.getPlayer());
            }
            lgPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,6000,9));
			lgPlayer.getPlayer().stopSound(Sound.MUSIC_DISC_MELLOHI);
			lgPlayer.getPlayer().playSound(lgPlayer.getPlayer().getLocation(), Sound.ENTITY_SKELETON_DEATH, SoundCategory.AMBIENT, 50, 0);
			lgPlayer.getPlayer().playSound(lgPlayer.getPlayer().getLocation(), Sound.MUSIC_DISC_MALL, SoundCategory.AMBIENT, 50, 0);
		}

        wait(5,() -> {
            InvokeNextRole();
        });
    }

    public static void InvokeNextRole()
    {
        if(indexNightRole >= getRolesAlives().size())
        {
            Game.NextDay();
        }
        else
        {
            LGRole currentRole = getRolesAlives().get(indexNightRole);
            Bukkit.getLogger().info(String.format("role : %s, index: %s", currentRole.getName(),indexNightRole));
            indexNightRole++;
            currentRole.onNightTurn(getLgPlayersByRole(currentRole.getName()));
        }
    }

    public static void NextDay()
    {
        for(LGPlayer player : getInGame())
        {
            WrapperPlayServerUpdateTime time = new WrapperPlayServerUpdateTime();
            time.setAgeOfTheWorld(0);
            time.setTimeOfDay((long)(6000));
            time.sendPacket(player.getPlayer());
            player.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
            for(LGPlayer showPlayer : getInGame())
            {
                if(!player.isDead())
                {
                    player.getPlayer().showPlayer(App.getInstance(), showPlayer.getPlayer());
                }
            }
            if(player.isDead())
            {
                Game.broadcastMessage(String.format("Cette nuit, %s est mort (comme une merde) RIP\nIl était %s%s !", player.getName(),player.getRole().getCodeCouleur(),player.getRole().getName()));
            }
        }
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
                lgp.updateSkin();
                lgp.updateOwnSkin();
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

    public static List<LGPlayer> getLgPlayersByRole(String roleName)
    {
        List<LGPlayer> players = new ArrayList<LGPlayer>();

        for(LGPlayer lgPlayer : getInGame())
        {
            if(lgPlayer.getRole().getName().equals(roleName))
            {
                players.add(lgPlayer);
            }
        }

        return players;
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

    public static void setKilledByWolf()
    {
        List<LGPlayer> killedList = new ArrayList<LGPlayer>();
        List<LGPlayer> playersList = Game.getInGame();
        Collections.sort(playersList, new SortLGPlayerByVote());
        Integer maxVote = playersList.get(0).getVote();

        for (LGPlayer player : playersList) {
            if (player.getVote() == maxVote) {
                killedList.add(player);
            }
        }

        Collections.shuffle(killedList);

        Bukkit.getLogger().info(String.format("Mort de %s", killedList.get(0).getName()));

        killedList.get(0).setDead(true);
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

    public static void vote(List<LGPlayer> players,int seconds, Runnable callback) {
        Game.cancelWait();
        Game.setWaitTicks(seconds * 20);
        Game.GlobalSetAllowVote(players,true);
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
                    Game.GlobalSetAllowVote(players,false);
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
        GlobalSetAllowVote(getLgPlayers(),false);
        inGame.clear();
        lgPlayers.clear();
        mayor = null;
        started = false;
    }

    public static void GlobalSetAllowVote(List<LGPlayer> players,boolean value) {
        for (LGPlayer lgPlayer : players) {
            lgPlayer.setVotedPlayer(null);
            lgPlayer.setHasVoted(false);
            lgPlayer.setVote(0);
            Bukkit.getLogger().info(String.format("%s : %s votes", lgPlayer.getName(),lgPlayer.getVote()));
            lgPlayer.setAllowVote(value);
        }
    }
}
