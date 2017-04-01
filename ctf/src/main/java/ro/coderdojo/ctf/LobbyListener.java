package ro.coderdojo.ctf;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;

public class LobbyListener implements Listener {

	JavaPlugin plugin;
	World lobby;



	public LobbyListener(JavaPlugin plugin, World lobby) {
		this.plugin = plugin;
		this.lobby = lobby;
	}

	@EventHandler
	public void PlayersListener(PlayerJoinEvent event) throws Exception {
		System.out.println("PlayerJoinEvent");
		event.getPlayer().setGameMode(GameMode.ADVENTURE);
		event.getPlayer().getInventory().clear();
		AttributeInstance healthAttribute = event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH); 
		healthAttribute.setBaseValue(20.00);
		event.getPlayer().teleport(new Location(lobby, 19.589, 231, 21.860));
		Players.lobbyNoTeamPlayers.add(event.getPlayer());
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if(!Players.isInLobby(event.getPlayer())) {
			return;
		}
		event.setRespawnLocation(new Location(lobby, 19.589, 231, 21.860));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		event.getPlayer().sendMessage(ChatColor.YELLOW + " Nu poti sparge " + ChatColor.RED + "arena!");
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if(!Players.isInLobby(event.getPlayer())) {
			return;
		}
		Block walkingBlock = event.getTo().getBlock().getRelative(BlockFace.DOWN);
		if (walkingBlock.getType() == Material.WOOL) {
			Wool wool = (Wool) walkingBlock.getState().getData();
			if (wool.getColor() == DyeColor.RED) {
				if (Players.lobbyRedPlayers.contains(event.getPlayer())) {
					return;
				}
				event.getPlayer().sendMessage(ChatColor.RED + " Esti in echipa rosie!");
				Players.lobbyRedPlayers.add(event.getPlayer());
				Players.lobbyBluePlayers.remove(event.getPlayer());
				Players.lobbyNoTeamPlayers.remove(event.getPlayer());
			}
			if (wool.getColor() == DyeColor.BLUE) {
				if (Players.lobbyBluePlayers.contains(event.getPlayer())) {
					return;
				}
				event.getPlayer().sendMessage(ChatColor.BLUE + " Esti in echipa albastra!");
				Players.lobbyBluePlayers.add(event.getPlayer());
				Players.lobbyRedPlayers.remove(event.getPlayer());
				Players.lobbyNoTeamPlayers.remove(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!Players.isInLobby(event.getPlayer())) {
			return;
		}
		Player player = event.getPlayer();
		Action action = event.getAction();
		Material material = event.getClickedBlock().getState().getType();
		Location location = event.getClickedBlock().getState().getLocation();
		if (action == Action.RIGHT_CLICK_BLOCK && material == Material.STONE_BUTTON) {
			if(Players.isMatchStarted) {
				player.sendMessage("Meciul este deja pornit! Asteapta sa se termine!");
				return;
			}
			Location b1 = new Location(lobby, 21.0, 233.0, 21.0, location.getYaw(), location.getPitch());
			Location b2 = new Location(lobby, 19.0, 233.0, 19.0, location.getYaw(), location.getPitch());
			Location b3 = new Location(lobby, 17.0, 233.0, 21.0, location.getYaw(), location.getPitch());
			Location b4 = new Location(lobby, 19.0, 233.0, 23.0, location.getYaw(), location.getPitch());
			if (location.equals(b1) || location.equals(b2) || location.equals(b3) || location.equals(b4)) {
				timer();
				player.sendMessage("Ai pornit meciul!");
				plugin.getServer().broadcastMessage("Meciul a fost pornit!");
			}
		}
	}
	
	private void timer() {
		CountDownTimer timer = new CountDownTimer(this);
		timer.runTaskTimer(plugin, 3, 1);
	}
}
