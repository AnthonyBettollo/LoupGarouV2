package loupgarou;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import loupgarou.utils.Utils;

public class App extends JavaPlugin {
	@Override
	public void onEnable() {
		getLogger().info("Wesh on load le LG trkl le couz!");
		if (!new File(getDataFolder(), "config.yml").exists()) {// Créer la config
			FileConfiguration config = getConfig();
			config.set("spawns", new ArrayList<List<Location>>());
			// for(String role : roles.keySet())//Nombre de participant pour chaque rôle
			// config.set("role."+role, 1);

			saveConfig();
		}
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command commande, String label, String[] args) {
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
						if(spawnList.size() < players.size())
						{
							sender.sendMessage("Pas assez de position pour le nombre de joueurs !");
							return true;
						}
						for (Player player : players) {
							player.teleport(spawnList.get(players.indexOf(player)));
						}
						break;
					case "end":
						break;
					case "nextNight":
						break;
					case "nextDay":
						break;
					case "reloadConfig":
						break;
					case "roles":
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
	public void onDisable() {
		getLogger().info("Ciao le sanch");
		ProtocolLibrary.getProtocolManager().removePacketListeners(this);
	}
}
