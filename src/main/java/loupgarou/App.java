package loupgarou;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import loupgarou.classes.Game;
import loupgarou.classes.roles.RolesConfig;
import loupgarou.classes.utils.SpawnHandler;
import loupgarou.classes.utils.Utils;
import loupgarou.listeners.CancelListener;

public class App extends JavaPlugin {
	private static App instance;

	public static App getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		instance = this;
		getLogger().info("Wesh on load le LG trkl le couz!");
		FileConfiguration config = getConfig();
		if (!new File(getDataFolder(), "config.yml").exists()) {// Créer la config
			config.set("spawns", new ArrayList<List<Location>>());
			config.set("roles", RolesConfig.GetDefaultConfig());

			saveConfig();
		}
		RolesConfig.setRoles(RolesConfig.parseConfig((List<String>) getConfig().getList("roles")));
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		Bukkit.getPluginManager().registerEvents(new CancelListener(), this);

		protocolManager.addPacketListener(
				new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.UPDATE_HEALTH) {
					@Override
					public void onPacketSending(PacketEvent event) {
						for (Player player : Bukkit.getOnlinePlayers()) {
							player.setFoodLevel(20);
						}
					}
				});

		protocolManager.addPacketListener(
				new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						if (event.getPacket().getSoundEffects().read(0).equals(Sound.ENTITY_PLAYER_ATTACK_NODAMAGE)) {
							event.setCancelled(true);

						}
					}
				});

		protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.PLAYER_INFO) {
			@Override
			public void onPacketSending(PacketEvent event) {
				Bukkit.getLogger().info("PLAYER INFO SENDING");
				// WrapperPlayServerPlayerInfo wrapper = new WrapperPlayServerPlayerInfo(event.getPacket());
				// List<PlayerInfoData> playerInfoDataList = wrapper.getData();
				// List<PlayerInfoData> newPlayerInfoDataList = new ArrayList<PlayerInfoData>();

				// Player currentPlayer = event.getPlayer();

				// if (wrapper.getAction() != PlayerInfoAction.ADD_PLAYER  || !Game.isStarted()) {
                //     return;
                // }

				// for (PlayerInfoData playerInfoData : playerInfoDataList) {

				// 	if (playerInfoData == null || playerInfoData.getProfile() == null) {
                //         newPlayerInfoDataList.add(playerInfoData);
                //         continue;
                //     }

				// 	if(playerInfoData.getProfile().getName().equals(Game.getMayor().getName()))
				// 	{
				// 		Bukkit.getLogger().info("ON MAJ LE SKIN DU MAIRE");
				// 		WrappedGameProfile profile = playerInfoData.getProfile();
				// 		profile.getProperties().put("textures", LGSkin.MAYOR.getProperty());
				// 		PlayerInfoData newPlayerInfoData = new PlayerInfoData(profile, playerInfoData.getLatency(), playerInfoData.getGameMode(), playerInfoData.getDisplayName());
				// 		newPlayerInfoDataList.add(newPlayerInfoData);
				// 	}

				// 	Bukkit.getLogger().info(String.format("CHECK %s INFOS", playerInfoData.getProfile().getName()));
				// 	WrappedGameProfile profile = playerInfoData.getProfile();
				// 	profile.getProperties().removeAll("textures");
				// 	profile.getProperties().put("textures", LGSkin.MAYOR.getProperty());
				// 	PlayerInfoData newPlayerInfoData = new PlayerInfoData(profile, playerInfoData.getLatency(),
				// 			playerInfoData.getGameMode(), playerInfoData.getDisplayName());
				// 	newPlayerInfoDataList.add(newPlayerInfoData);
				// 	break;
				// }
				// wrapper.setData(newPlayerInfoDataList);
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command commande, String label, String[] args) {
		FileConfiguration config = getConfig();
		ArrayList<String> commands = new ArrayList<>((Arrays.asList("addSpawn", "checkSpawn", "delSpawn", "start",
				"end", "nextNight", "nextDay", "reloadConfig", "roles", "reloadPack", "joinAll")));
		if (label.equalsIgnoreCase("lg")) {
			if (!sender.hasPermission("loupgarou.admin")) {
				sender.sendMessage("Sry t'as pas les droits mon sanch :/");
				return true;
			}
			if (args.length > 0 && commands.contains(args[0])) {
				switch (args[0]) {
					case "addSpawn":
						Player currentPlayer = (Player) sender;
						Location loc = currentPlayer.getLocation();
						List<Location> spawns = (List<Location>) getConfig().getList("spawns");
						spawns.add(new Location(Bukkit.getWorld("world"), (double) loc.getBlockX(), loc.getY(),
								(double) loc.getBlockZ(), loc.getYaw(), loc.getPitch()));
						saveConfig();
						reloadConfig();
						sender.sendMessage("La position a bien été ajoutée !");
						break;
					case "checkSpawn":
						SpawnHandler.handleSpawn(sender, args, (List<Location>) getConfig().getList("spawns"), "check",
								this);
						break;
					case "delSpawn":
						SpawnHandler.handleSpawn(sender, args, (List<Location>) getConfig().getList("spawns"), "delete",
								this);
						break;
					case "start":
						List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
						List<Location> spawnList = (List<Location>) getConfig().getList("spawns");
						List<RolesConfig> roles = RolesConfig.getRoles();
						Integer countRoles = 0;
						for (RolesConfig role : roles) {
							countRoles += role.getCount();
						}
						if (spawnList.size() < players.size()) {
							sender.sendMessage("Pas assez de position pour le nombre de joueurs !");
							return true;
						}

						if (countRoles != players.size()) {
							sender.sendMessage("Pas assez de joueurs pour le nombre de rôles ou inversement !");
							return true;
						}

						Game.start(players, spawnList, roles);
						break;
					case "end":
						Game.stop();
						break;
					case "nextNight":
						break;
					case "nextDay":
						break;
					case "reloadConfig":
						break;
					case "roles":
						if (args.length > 1) {
							switch (args[1]) {
								case "set":
									if (args.length == 2) {
										sender.sendMessage("Liste des rôles disponibles :");
										sender.sendMessage(
												String.format("%s", Utils.customJoin('\n',
														new ArrayList<String>(RolesConfig.GetRolesNames()))));
									} else {
										if (args.length == 3) {
											sender.sendMessage("Envoi un chiffre tu paieras pas plus cher le 100");
										} else {
											if (RolesConfig.GetRolesNames().contains(args[2])) {
												try {
													Integer count = Integer.parseInt(args[3]);
													RolesConfig.UpdateRole(args[2], count);
													sender.sendMessage("Modificiation enregistré !");
												} catch (NumberFormatException e) {
													sender.sendMessage("Envoi un chiffre frérot tu foooooorces...");
												}
											} else {
												sender.sendMessage("Le rôle existe pas frérot...");
											}
										}
									}
									break;
								case "reset":
									config.set("roles", RolesConfig.GetDefaultConfig());
									saveConfig();
									sender.sendMessage("Les rôles sont remis à zéro :)");
									break;
							}
						} else {
							ArrayList<String> displayRolesName = new ArrayList<String>();
							ListIterator<RolesConfig> roleIterator = RolesConfig.getRoles().listIterator();
							while (roleIterator.hasNext()) {
								displayRolesName.add(roleIterator.next().Definition());
							}
							sender.sendMessage("Liste des rôles pour la partie :");
							sender.sendMessage(String.format("%s", Utils.customJoin('\n', displayRolesName)));
						}
						break;
					case "reloadPack":
						break;
					case "joinAll":
						break;
				}
			} else {
				sender.sendMessage("Liste des commandes :");
				sender.sendMessage(String.format("/lg %s", Utils.customJoin(',', commands)));
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> commands = new ArrayList<String>();

		if (cmd.getName().equalsIgnoreCase("lg") && args.length > 0) {
			if (sender instanceof Player) {
				if (args.length == 1) {
					commands = new ArrayList<>((Arrays.asList("addSpawn", "checkSpawn", "delSpawn", "start", "end",
							"nextNight", "nextDay", "reloadConfig", "roles", "reloadPack", "joinAll")));
				}
				if (args.length == 2) {
					switch (args[0]) {
						case "checkSpawn":
							commands = new ArrayList<>((Arrays.asList("1")));
							break;
						case "delSpawn":
							commands = new ArrayList<>((Arrays.asList("1")));
							break;
						case "roles":
							commands = new ArrayList<>((Arrays.asList("set")));
							break;
					}
				}
				if (args.length == 3) {
					switch (args[1]) {
						case "set":
							commands = RolesConfig.GetRolesNames();
							break;
					}
				}
				if (args.length == 4 && args[1].equals("set")) {
					if (RolesConfig.GetRolesNames().contains(args[2])) {
						commands = new ArrayList<>((Arrays.asList("1")));
					}
				}
				if (args.length > 4) {
					commands = new ArrayList<String>();
				}
				return commands;
			}
		}
		return commands;
	}

	@Override
	public void onDisable() {
		getLogger().info("Ciao le sanch");
		FileConfiguration config = getConfig();
		config.set("roles", RolesConfig.getConfig());
		saveConfig();
		ProtocolLibrary.getProtocolManager().removePacketListeners(this);
	}
}
