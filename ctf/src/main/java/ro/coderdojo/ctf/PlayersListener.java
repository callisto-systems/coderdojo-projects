package ro.coderdojo.ctf;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.material.Wool;
import org.bukkit.plugin.Plugin;

public class PlayersListener implements Listener {

	Plugin plugin;
	World lobby;
	
	public static List<Player> redPlayers = new ArrayList<>();
	public static List<Player> bluePlayers = new ArrayList<>();

	public PlayersListener(Plugin plugin, World lobby) {
		this.plugin = plugin;
		this.lobby = lobby;
	}

	@EventHandler
	public void PlayersListener(PlayerJoinEvent event) throws Exception {
		System.out.println("PlayerJoinEvent");
		event.getPlayer().setGameMode(GameMode.SURVIVAL);
		event.getPlayer().teleport(new Location(lobby, 19.589, 231, 21.860));
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		System.out.println("PlayerRespawnEvent");
		event.setRespawnLocation(new Location(lobby, 19.589, 231, 21.860));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		event.getPlayer().sendMessage(ChatColor.YELLOW + " Nu poti sparge " + ChatColor.RED + "arena!");
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Block walkingBlock = event.getTo().getBlock().getRelative(BlockFace.DOWN);
		if (walkingBlock.getType() == Material.WOOL) {
			Wool wool = (Wool) walkingBlock.getState().getData();
			if(wool.getColor() == DyeColor.RED) {
				if(redPlayers.contains(event.getPlayer())) {
					return;
				}
				event.getPlayer().sendMessage(ChatColor.RED + " Esti in echipa rosie!");
				redPlayers.add(event.getPlayer());
				bluePlayers.remove(event.getPlayer());
			}
			if(wool.getColor() == DyeColor.BLUE) {
				if(bluePlayers.contains(event.getPlayer())) {
					return;
				}
				event.getPlayer().sendMessage(ChatColor.BLUE + " Esti in echipa albastra!");
				bluePlayers.add(event.getPlayer());
				redPlayers.remove(event.getPlayer());
			}
		}
	}

}
