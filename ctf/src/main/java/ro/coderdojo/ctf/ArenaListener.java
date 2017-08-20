package ro.coderdojo.ctf;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaListener implements Listener {

	FlagHandler redFlagHandler;
	FlagHandler blueFlagHandler;

	public ArenaListener() {
		redFlagHandler = new FlagHandler(new Location(CaptureTheFlagPlugin.arena, 77.437, 72, -162.478), FlagHandler.Color.RED);
		redFlagHandler.createFlag();

		blueFlagHandler = new FlagHandler(new Location(CaptureTheFlagPlugin.arena, -41.533, 71, -146.482), FlagHandler.Color.BLUE);
		blueFlagHandler.createFlag();
		
		redFlagHandler.setOther(blueFlagHandler);
		blueFlagHandler.setOther(redFlagHandler);
	}

	@EventHandler
	public void onPlayerMoveAddLightAndFlag(PlayerMoveEvent event) {

//		FlagHandler f =  new FlagHandler(new Location(lobby, 19, 231, 38), FlagHandler.Color.RED);
//		f.attachFlagToPlayer(event.getPlayer(), lobby);
//		
//		if (!ScoresAndTeams.isBlue(event.getPlayer()) && !ScoresAndTeams.isRed(event.getPlayer())) {
//			return;
//		}
		redFlagHandler.takeFlagIfNecessary(event.getPlayer());
		blueFlagHandler.takeFlagIfNecessary(event.getPlayer());

		redFlagHandler.moveLightIfCarryTheFlag(event);
		blueFlagHandler.moveLightIfCarryTheFlag(event);

	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
		player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET, 1));
		player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
		player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
		player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS, 1));
		player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
		AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		healthAttribute.setBaseValue(10.00);
		if (!ScoresAndTeams.isInArena(event.getPlayer())) {
			return;
		}
		if (ScoresAndTeams.arenaRedPlayers.contains(event.getPlayer())) {
			event.setRespawnLocation(new Location(CaptureTheFlagPlugin.arena, 57.896, 77, -163.485));
		}
		if (ScoresAndTeams.arenaBluePlayers.contains(event.getPlayer())) {
			event.setRespawnLocation(new Location(CaptureTheFlagPlugin.arena, -22.099, 77, -145.093));
		}
	}
}
