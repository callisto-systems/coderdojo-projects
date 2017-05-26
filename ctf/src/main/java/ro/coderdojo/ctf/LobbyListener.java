package ro.coderdojo.ctf;

import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventPriority;

import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.material.Wool;

public class LobbyListener implements Listener {

	@EventHandler
	public void playerJoined(PlayerJoinEvent event) throws Exception {
		Player player = event.getPlayer();
		player.setGameMode(GameMode.ADVENTURE);
		player.getInventory().clear();
		player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
		AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		healthAttribute.setBaseValue(20.00);
		ScoresAndTeams.addNoTeamPlayerLobby(player);

		player.teleport(getSpawnLocation(player));
		
	}

	private Location getSpawnLocation(Player player) {
		if (ScoresAndTeams.hasNoTeamInLobby(player)) {
			int random = new Random().nextInt();
			if (random % 2 == 0) {
				return new Location(CaptureTheFlagPlugin.lobby, 19.456, 231, 2.703, -0.3f, 1.2f);
			} else {
				return new Location(CaptureTheFlagPlugin.lobby, 19.456, 231, 38.733, -180, 0.4f);
			}
		}
		if (ScoresAndTeams.isBlueInLobby(player)) {
			return new Location(CaptureTheFlagPlugin.lobby, 38.788, 232.06250, 21.643, 90.1f, 4.2f);
		} else {
			return new Location(CaptureTheFlagPlugin.lobby, -0.7, 232, 21.716, -90.06f, 3.5f);
		}
	}

	@EventHandler
	public void playerLeave(PlayerQuitEvent event) throws Exception {
		ScoresAndTeams.playerLeave(event.getPlayer());
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!ScoresAndTeams.isInLobby(event.getPlayer())) {
			return;
		}
		event.setRespawnLocation(getSpawnLocation(event.getPlayer()));
	}

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		if (!ScoresAndTeams.isInLobby(event.getEntity())) {
			return;
		}
		Player killer = event.getEntity().getKiller();

		if (killer == null) {
			return;
		}
		if (ScoresAndTeams.isRed(killer)) {
			ScoresAndTeams.redKills++;
		}

		if (ScoresAndTeams.isBlue(killer)) {
			ScoresAndTeams.blueKills++;
		}

		ScoresAndTeams.refreshLobbyBoards(event.getEntity());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		event.getPlayer().sendMessage(ChatColor.YELLOW + " Nu poti sparge " + ChatColor.RED + "arena!");
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
		event.getEntity().remove();
	}

	@EventHandler
	public void onPlayerMoveChangeTeam(PlayerMoveEvent event) {
		if (!ScoresAndTeams.isInLobby(event.getPlayer())) {
			return;
		}
		Block walkingBlock = event.getTo().getBlock().getRelative(BlockFace.DOWN);
		if (walkingBlock.getType() == Material.WOOL) {
			Wool wool = (Wool) walkingBlock.getState().getData();
			if (wool.getColor() == DyeColor.RED) {
				ScoresAndTeams.addRedToLobby(event.getPlayer());
			}
			if (wool.getColor() == DyeColor.BLUE) {
				ScoresAndTeams.addBlueToLobby(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!ScoresAndTeams.isInLobby(event.getPlayer())) {
			return;
		}
		Player player = event.getPlayer();
		Action action = event.getAction();
		if (event.getClickedBlock() == null) {
			return;
		}
		Material material = event.getClickedBlock().getState().getType();
		Location location = event.getClickedBlock().getState().getLocation();
		if (action == Action.RIGHT_CLICK_BLOCK && material == Material.STONE_BUTTON) {
			System.out.println("Click: " + location);
			if (ScoresAndTeams.isMatchStarted) {
				player.sendMessage("Meciul este deja pornit! Asteapta sa se termine!");
				return;
			}
			Location button1 = new Location(CaptureTheFlagPlugin.lobby, 19.0, 233.0, 42.0, location.getYaw(), location.getPitch());
			Location button2 = new Location(CaptureTheFlagPlugin.lobby, 19.0, 233.0, 0, location.getYaw(), location.getPitch());
			if (location.equals(button1) || location.equals(button2)) {
				timer();
				player.sendMessage("Ai pornit meciul!");
				CaptureTheFlagPlugin.plugin.getServer().broadcastMessage("Meciul a fost pornit!");
				for(Player aPlayer : ScoresAndTeams.getAllLobyPlayers()) {
					if(ScoresAndTeams.hasNoTeamInLobby(aPlayer)) {
						player.sendMessage(ChatColor.RED + "PORNEȘTE MECIUL ȘI NU EȘTI ÎN NICI O ECHIPĂ! AI 10 SECUNDE SĂ ALEGI!");
					}
				}
			}
		}
	}

	private void timer() {
		CountDownTimer timer = new CountDownTimer();
		timer.runTaskTimer(CaptureTheFlagPlugin.plugin, 3, 1);
	}
}
