/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.coderdojo.ctf;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
	private int counter = 10 * 20;

	@Override
	public void run() {
		if (counter > 0) {
			if (counter % 20 == 0) {
				CaptureTheFlagPlugin.plugin.getServer().broadcastMessage(ChatColor.WHITE + "Jocul porneste in " + ChatColor.RED + counter / 20 + ChatColor.WHITE + " secunde");
			}
			counter = counter - 1;
		} else {
			CaptureTheFlagPlugin.plugin.getServer().broadcastMessage("Start!");
			this.cancel();
			startGame();
		}
	}

	private void startGame() {
		ScoresAndTeams.isMatchStarted = true;
		
		System.out.println("Arena loaded");
		List<String> playersLeftInLobby = new ArrayList<>();
		for(Player player : ScoresAndTeams.getAllLobyPlayers()) {
			if(ScoresAndTeams.hasNoTeamInLobby(player)){
				player.sendMessage("Ai ramas in lobby. Nu ti-ai ales echipa!");
				playersLeftInLobby.add(player.getName());
				continue;
			}
			ScoresAndTeams.moveToArena(player);
			if(ScoresAndTeams.isRed(player)) {
				player.teleport(new Location(CaptureTheFlagPlugin.arena, 57.896, 77, -163.485));
			}
			if(ScoresAndTeams.isBlue(player)) {
				player.teleport(new Location(CaptureTheFlagPlugin.arena, -22.099, 77, -145.093));
			}
			player.getInventory().clear();
			player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
			AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			healthAttribute.setBaseValue(20.00);
		}
		
		CaptureTheFlagPlugin.plugin.getServer().broadcastMessage(
				ChatColor.WHITE + "Jucatori ramasi in lobby: " + ChatColor.RED + playersLeftInLobby.size() +
				ChatColor.WHITE + ". Jucatori: " + ChatColor.RED + String.join(", ", playersLeftInLobby));
//		arenaListener.restartGame();
		
	}

}
