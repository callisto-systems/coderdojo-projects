package ro.coderdojo.ctf;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.v1_11_R1.EnumParticle;
import net.minecraft.server.v1_11_R1.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaListener implements Listener {

	FlagHandler redFlagHandler;
	FlagHandler blueFlagHandler;

	public ArenaListener() {
		redFlagHandler = new FlagHandler(new Location(CaptureTheFlagPlugin.arena, 77.437, 72, -162.478), FlagHandler.Color.RED);
		redFlagHandler.createFlag();

		blueFlagHandler = new FlagHandler(new Location(CaptureTheFlagPlugin.arena, -41.533, 72, -146.482), FlagHandler.Color.BLUE);
		blueFlagHandler.createFlag();
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
	public void onPlayerRespawn(PlayerRespawnEvent event) {
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
