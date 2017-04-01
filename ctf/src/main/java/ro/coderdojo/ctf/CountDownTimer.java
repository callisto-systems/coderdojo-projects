/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.coderdojo.ctf;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author mihai
 */
public class CountDownTimer extends BukkitRunnable {

	LobbyListener lobbyListener;
	World arena;

	private int counter = 10 * 20;

	public CountDownTimer(LobbyListener lobbyListener) {
		this.lobbyListener = lobbyListener;
	}

	@Override
	public void run() {
		if (counter > 0) {
			if (counter % 20 == 0) {
				lobbyListener.plugin.getServer().broadcastMessage(ChatColor.WHITE + "Jocul porneste in " + ChatColor.RED + counter / 20 + ChatColor.WHITE + " secunde");
			}
			counter = counter - 1;
		} else {
			lobbyListener.plugin.getServer().broadcastMessage("Start!");
			this.cancel();
			startGame();
		}
	}

	private void startGame() {
//		PlayerJoinEvent.getHandlerList().unregister(lobbyListener.plugin);
//		PlayerRespawnEvent.getHandlerList().unregister(lobbyListener.plugin);
//		PlayerMoveEvent.getHandlerList().unregister(lobbyListener.plugin);
//		PlayerInteractEvent.getHandlerList().unregister(lobbyListener.plugin);

		loadArenaWorld();
		lobbyListener.plugin.getServer().getPluginManager().registerEvents(new ArenaListener(lobbyListener.plugin, arena), lobbyListener.plugin);
		Players.isMatchStarted = true;
		
		System.out.println("Arena loaded");
		Iterator<Player> blueIterator = Players.lobbyBluePlayers.iterator();
		Players.arenaBluePlayers.clear();
		while (blueIterator.hasNext()) {
			Player bluePlayer = blueIterator.next();
			Players.arenaBluePlayers.add(bluePlayer);
			blueIterator.remove();
			System.out.println("teleport blue " + bluePlayer.getDisplayName());
			bluePlayer.teleport(new Location(arena, 57.896, 77, -163.485));
			bluePlayer.getInventory().clear();
			bluePlayer.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
			AttributeInstance healthAttribute = bluePlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			healthAttribute.setBaseValue(20.00);
		}

		Iterator<Player> redIterator = Players.lobbyRedPlayers.iterator();
		Players.arenaRedPlayers.clear();
		while (redIterator.hasNext()) {
			Player redPlayer = redIterator.next();
			Players.arenaRedPlayers.add(redPlayer);
			redIterator.remove();
			System.out.println("teleport red " + redPlayer.getDisplayName());
			redPlayer.teleport(new Location(arena, -22.099, 77, -145.093));
			redPlayer.getInventory().clear();
			redPlayer.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
			AttributeInstance healthAttribute = redPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			healthAttribute.setBaseValue(20.00);
		}

		String players = "";
		for (Player noTeamPlayer : Players.lobbyNoTeamPlayers) {
			noTeamPlayer.sendMessage("Ai ramas in lobby. Nu ti-ai ales echipa!");
			players += noTeamPlayer.getDisplayName() + " ";
		}
		lobbyListener.plugin.getServer().broadcastMessage(
				ChatColor.WHITE + "Jucatori ramasi in lobby: " + ChatColor.RED + Players.lobbyNoTeamPlayers.size() +
				ChatColor.WHITE + ". Jucatori: " + ChatColor.RED + players);
	}

	private void loadArenaWorld() {
		arena = Bukkit.getServer().createWorld(new WorldCreator("world_arena"));
		arena.setGameRuleValue("doMobSpawning", "false");
		arena.setDifficulty(Difficulty.HARD);
	}

}
