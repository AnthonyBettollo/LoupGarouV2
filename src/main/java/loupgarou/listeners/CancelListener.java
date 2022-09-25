package loupgarou.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import loupgarou.classes.Game;
import loupgarou.classes.LGPlayer;
import loupgarou.classes.utils.Utils;

public class CancelListener implements Listener {
	@EventHandler
	public void onPluie(WeatherChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (Game.isStarted() && e.getFrom().distanceSquared(e.getTo()) > 0.001)
			e.setTo(e.getFrom());
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		e.setFoodLevel(20);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(e.getPlayer().getLocation());
	}

	@EventHandler
	public void onRespawn(PlayerDeathEvent e) {
		e.setDeathMessage("");
		e.setKeepInventory(true);
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onClick(PlayerAnimationEvent e) {
		if(e.getAnimationType() == PlayerAnimationType.ARM_SWING)
		{
			LGPlayer source = Game.getLgPlayer(e.getPlayer());
			Location loc = e.getPlayer().getLocation();
			if(loc.getPitch() > 60)
				source.vote(source);
			for(int i = 0;i<50;i++) {
				loc.add(loc.getDirection());
				for(LGPlayer player : Game.getInGame()) {
					if((!player.isDead()) && Utils.distanceSquaredXZ(loc, player.getPlayer().getLocation()) < 0.35 && Math.abs(loc.getY()-player.getPlayer().getLocation().getY()) < 2) {
						source.vote(player);
					}
				}
			}
		}
			
	}
}
