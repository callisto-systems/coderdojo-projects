package ro.coderdojo.ctf;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaListener implements Listener {

	JavaPlugin plugin;
	World arena;

	public ArenaListener(JavaPlugin plugin, World arena) {
		this.plugin = plugin;
		this.arena = arena;
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if(!Players.isInArena(event.getPlayer())) {
			return;
		}
		if(Players.arenaRedPlayers.contains(event.getPlayer())) {
			event.setRespawnLocation(new Location(arena, -22.099, 77, -145.093));
		}
		if(Players.arenaBluePlayers.contains(event.getPlayer())) {
			event.setRespawnLocation(new Location(arena, 57.896, 77, -163.485));
		}
	}

	
}
