package loupgarou.classes.utils;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import loupgarou.App;

public class SpawnHandler {
    public static void handleSpawn(CommandSender sender, String[] args, List<Location> locations, String action,
            App mainClass) {
        Integer spawnCount = locations.size();
        if (args.length == 1) {
            sender.sendMessage(String.format("Il y a actuellement %s lieu(x) de spawn dans la config.",
                    spawnCount.toString()));
        } else {
            if (!Utils.isInteger(args[1])) {
                sender.sendMessage("Tu dois saisir un chiffre chef");
            }
            Integer index = Integer.parseInt(args[1]) - 1;
            if (index < 0 || index >= spawnCount) {
                sender.sendMessage("Pas de spawn à cet index");
            } else {
                switch (action) {
                    case "check":
                        Location spawnToCheck = locations.get(index);
                        Player playerToTeleport = (Player) sender;
                        playerToTeleport.teleport(spawnToCheck);
                        break;
                    case "delete":
                        locations.remove(locations.get(index));
                        mainClass.saveConfig();
                        mainClass.reloadConfig();
                        sender.sendMessage("La position a bien été supprimée !");
                        break;
                }
            }

        }
    }
}
