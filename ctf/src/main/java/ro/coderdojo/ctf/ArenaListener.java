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
