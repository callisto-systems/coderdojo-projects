package ro.coderdojo.ctf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.server.v1_11_R1.EnumParticle;
import net.minecraft.server.v1_11_R1.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import static ro.coderdojo.ctf.LobbyListener.lastBlock;

public class ArenaListener implements Listener {

	JavaPlugin plugin;
	World arena;

	public ArenaListener(JavaPlugin plugin, World arena) {
		this.plugin = plugin;
		this.arena = arena;
	}

	private void addSparksOnCaptureTheFlag(Player player) {
		if (ScoresAndTeams.lobbyNoTeamPlayers.size() == 1) {
			new BukkitRunnable() {
//				//EnumParticle.PORTAL // mov
//				//EnumParticle.REDSTONE // toaate culorile
				EnumParticle particle = EnumParticle.VILLAGER_HAPPY;

				@Override
				public void run() {
					//params: particula,dacă e enable, locația, offset-ul, viteza, numar particule
					PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, player.getLocation().getBlockX(), player.getLocation().getBlockY() + 2, player.getLocation().getBlockZ(), 1, 5, 1, 1, 30);
					for (Player online : Bukkit.getOnlinePlayers()) {
						((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
					}
				}
			}.runTaskTimer(CaptureTheFlagPlugin.plugin, 0, 1);
		}
	}

	@EventHandler
	public void onPlayerMoveAddLight(PlayerMoveEvent event) {
		Block from = event.getFrom().getBlock().getRelative(BlockFace.DOWN);
		Block to = event.getTo().getBlock().getRelative(BlockFace.DOWN);
		Block aboveTo = to.getLocation().add(0, 1, 0).getBlock();

		if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
			return;
		}

		//restore last bloack
		if (lastBlock.get(event.getPlayer().getName()) != null) {
			lastBlock.get(event.getPlayer().getName())[0].update(true);
			lastBlock.get(event.getPlayer().getName())[1].update(true);
		}

		System.out.println();

		System.out.println(
				"*************** MOVE ********************");

		Block from1 = event.getFrom().getBlock().getRelative(BlockFace.UP);
		Block to1 = event.getTo().getBlock().getRelative(BlockFace.UP);

		System.out.println("---------------------------------");
		System.out.println("from" + from1.getType() + " x:" + from1.getX() + " y:" + from1.getY() + " z:" + from1.getX());
		System.out.println("to" + to1.getType() + " x:" + to1.getX() + " y:" + to1.getY() + " z:" + to1.getX());

		from1 = event.getFrom().getBlock();
		to1 = event.getTo().getBlock();

		System.out.println("---------------------------------");
		System.out.println("from" + from1.getType() + " x:" + from1.getX() + " y:" + from1.getY() + " z:" + from1.getX());
		System.out.println("to" + to1.getType() + " x:" + to1.getX() + " y:" + to1.getY() + " z:" + to1.getX());

		from1 = event.getFrom().getBlock().getRelative(BlockFace.DOWN);
		to1 = event.getTo().getBlock().getRelative(BlockFace.DOWN);

		System.out.println("---------------------------------");
		System.out.println("from" + from1.getType() + " x:" + from1.getX() + " y:" + from1.getY() + " z:" + from1.getX());
		System.out.println("to" + to1.getType() + " x:" + to1.getX() + " y:" + to1.getY() + " z:" + to1.getX());

		System.out.println("Material: " + to.getType().isSolid());
		if (to.getType().isSolid()) { //&& !(to.getType() != Material.AIR || to.getType() != Material.WATER)
			//save current block
			lastBlock.put(event.getPlayer().getName(), new BlockState[]{to.getState(), aboveTo.getState()});
			//replace current wIth glowstone
			to.setType(Material.SEA_LANTERN);
			aboveTo.setType(Material.CARPET);
			aboveTo.setData((byte) (14 & 0xFF));//red
			aboveTo.setData((byte) (11 & 0xFF));//blue

//			aboveTo.setType(Material.STAINED_GLASS_PANE);
//			Material glass = Material.STAINED_GLASS_PANE;
//			System.out.println("");
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!ScoresAndTeams.isInArena(event.getPlayer())) {
			return;
		}
		if (ScoresAndTeams.arenaRedPlayers.contains(event.getPlayer())) {
			event.setRespawnLocation(new Location(arena, -22.099, 77, -145.093));
		}
		if (ScoresAndTeams.arenaBluePlayers.contains(event.getPlayer())) {
			event.setRespawnLocation(new Location(arena, 57.896, 77, -163.485));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
//		event.getPlayer().getInventory().addItem(pokeballBanner());
		event.getPlayer().sendMessage(ChatColor.YELLOW + " Nu poti sparge " + ChatColor.RED + "arena!");
		event.setCancelled(true);
	}

}
