package ro.coderdojo.ctf;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_11_R1.Chunk;
import net.minecraft.server.v1_11_R1.EnumSkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class LightSourceEx {
	/*
	 * MINI README
	 * 
	 * This is free and you can use it/change it all you want.
	 * 
	 * There is a bukkit forum post on for this code:
	 * http://forums.bukkit.org/threads/resource-server-side-lighting-no-it-isnt-just-client-side.154503/
	 */
	
	/**
	 * Create light with level at a location. Players can be added to make them only see it.
	 * @param l
	 * @param level
	 * @param players
	 */
	public static void createLightSource (Location l, int level, Player... players) {
		CraftWorld w = (CraftWorld) l.getWorld();
		int oLevel = l.getBlock().getLightLevel();
		
//		net.minecraft.server.v1_11_R1.IBlockData blockData = c.getBlockData(new net.minecraft.server.v1_11_R1.BlockPosition(b.getX(), b.getY(), b.getZ()));
//		net.minecraft.server.v1_11_R1.BlockPosition blockPos = new net.minecraft.server.v1_11_R1.BlockPosition(b.getX(), b.getY(), b.getZ());
		
		//Sets the light source at the location to the level
		w.getHandle()
				.b(EnumSkyBlock.BLOCK, l.getBlockX(), l.getBlockY(), l.getBlockZ(), level);
		
		//Send packets to the area telling players to see this level
		updateChunk(l, players);
		
		//If you comment this out it is more likely to get light sources you can't remove
		// but if you do comment it, light is consistent on relog and what not.
		w.getHandle().b(EnumSkyBlock.BLOCK, l.getBlockX(), l.getBlockY(), l.getBlockZ(), oLevel);
	}
	
	/**
	 * Updates the block making the light source return to what it actually is
	 * @param l
	 */
	public static void deleteLightSource (Location l, Player... players) {
		int t = l.getBlock().getTypeId();
		l.getBlock().setTypeId(t == 1 ? 2 : 1);
		
		updateChunk(l, players);
		
		l.getBlock().setTypeId(t);
	}
	
	/**
	 * Gets all the chunks touching/diagonal to the chunk the location is in and updates players with them.
	 * @param l
	 */
	private static void updateChunk (Location l, Player... players) {
		List<Chunk> cs = new ArrayList<Chunk>();
		
		for(int x=-1; x<=1; x++) {
			for(int z=-1; z<=1; z++) {
				cs.add(((CraftChunk)l.clone().add(16 * x, 0, 16 * z).getChunk()).getHandle());
			}
		}

		Packet56MapChunkBulk packet = new Packet56MapChunkBulk(cs);
		int t = l.clone().add(0, 1, 0).getBlock().getTypeId();
		l.clone().add(0, 1, 0).getBlock().setTypeId(t == 1 ? 2 : 1);
		
		Player[] playerArray = ((players != null && players.length > 0) ? players : l.getWorld().getPlayers().toArray(new Player[l.getWorld().getPlayers().size()]));
			
		for(Player p1 : playerArray) {
			if(p1.getLocation().distance(l) <= Bukkit.getServer().getViewDistance()*16) {
				((CraftPlayer)p1).getHandle().playerConnection.sendPacket(packet);
			}
		}
			
		l.clone().add(0, 1, 0).getBlock().setTypeId(t);
	}
}