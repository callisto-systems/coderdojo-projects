/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.coderdojo.ctf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBanner;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author mihai
 */
public class FlagHandler {

	//guardian_idle2.ogg
	//levelup.ogg
	
	World world;
	Location originalFlagLocation;
	CraftBanner banner;
	Color color;

	Player flagCarrier;
	BukkitRunnable rotateFlagRunnable;

	BlockState[] replacedBlocksByLantern;

	public FlagHandler other;
	
	public static enum Color {
		RED(new Pattern[]{new Pattern(DyeColor.PINK, PatternType.BORDER), new Pattern(DyeColor.WHITE, PatternType.SKULL)}, DyeColor.RED),
		BLUE(new Pattern[]{new Pattern(DyeColor.LIGHT_BLUE, PatternType.BORDER), new Pattern(DyeColor.WHITE, PatternType.FLOWER)}, DyeColor.BLUE);

		List<Pattern> patterns;
		DyeColor color;

		Color(Pattern[] patterns, DyeColor color) {
			this.patterns = Arrays.asList(patterns);
			this.color = color;
		}
	}

	FlagHandler(Location location, Color color) {
		world = location.getWorld();
		this.originalFlagLocation = location;
		this.color = color;
	}
	
	public void setOther(FlagHandler other) {
		this.other = other;
	}

	public void moveLightIfCarryTheFlag(PlayerMoveEvent event) {
		if (!isCarryingTheFlag(event.getPlayer())) {
			return;
		}
		Block from = event.getFrom().getBlock().getRelative(BlockFace.DOWN);
		Block to = event.getTo().getBlock().getRelative(BlockFace.DOWN);
		Block aboveTo = to.getLocation().add(0, 1, 0).getBlock();

		if (BlockUtil.isSameBlock(to, from)) {
			return;
		}

		if (!to.getType().isSolid()) {
			return;
		}
		//restore last bloack
		if (replacedBlocksByLantern != null) {
			replacedBlocksByLantern[0].update(true);
			replacedBlocksByLantern[1].update(true);
		}
		//save current block
		replacedBlocksByLantern = new BlockState[]{to.getState(), aboveTo.getState()};

		//replace current with glowstone and carpet
		to.setType(Material.SEA_LANTERN);
		aboveTo.setType(Material.CARPET);
		if (ScoresAndTeams.isBlue(event.getPlayer())) {
			aboveTo.setData((byte) (11 & 0xFF));//blue
		}
		if (ScoresAndTeams.isRed(event.getPlayer())) {
			aboveTo.setData((byte) (14 & 0xFF));//red
		}

	}

	public void takeFlagIfNecessary(Player player) {
//		System.out.println(flagCarrier);
		if (flagCarrier != null) {
			System.out.println(player.getLocation().getBlock().getLocation());
			System.out.println(other.originalFlagLocation);
			if(player.getLocation().equals(other.originalFlagLocation)) {		
				System.err.println("AAAAAAAAAAAAAAAAAAAAAAIIIIIIIIIIIIII Punctat");
			}
		}
		
		if (!ScoresAndTeams.isInArena(player)) {
			return;
		}
		if (!BlockUtil.isSameBlock(originalFlagLocation.getBlock(), player.getLocation().getBlock())) {
			return;
		}
		if (ScoresAndTeams.isRed(player) && color == Color.RED) {
			return;
		}
		if (ScoresAndTeams.isBlue(player) && color == Color.BLUE) {
			return;
		}
		//if(BlockUtil.isSameBlock(originalFlagLocation.getBlock(), player.getLocation().getBlock())){}
		rotateFlagRunnable.cancel();

		new BukkitRunnable() {
			public void run() {
				originalFlagLocation.getBlock().setType(Material.AIR);
			}
		}.runTask(CaptureTheFlagPlugin.plugin);

		attachFlagToPlayer(player);
		//addSparks(player);
	}

	/*private void addSparks(final Player flagCarrier) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player anArenaPlayer : ScoresAndTeams.getAllArenaPlayers()) {
					for (int a = 4; a < 24; a++) {
						//params: particula,dacă e enable, locația, offset-ul, viteza, numar particule
						PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
								EnumParticle.VILLAGER_HAPPY, true,
								flagCarrier.getLocation().getBlockX(), flagCarrier.getLocation().getBlockY() + a, flagCarrier.getLocation().getBlockZ(),
								1, 1, 1, 1, 3);
						((CraftPlayer) anArenaPlayer).getHandle().playerConnection.sendPacket(packet);
					}
				}
			}
		}.runTaskTimer(CaptureTheFlagPlugin.plugin, 0, 20);
	}*/

	public void attachFlagToPlayer(Player player) {
		flagCarrier = player;
		ItemStack banner = new ItemStack(Material.BANNER, 1);
		BannerMeta meta = (BannerMeta) banner.getItemMeta();
		meta.setBaseColor(color.color);
		meta.setPatterns(color.patterns);
		banner.setItemMeta(meta);
		player.getInventory().setHelmet(banner);
	}

	public boolean isCarryingTheFlag(Player player) {
		return flagCarrier == player;
	}

	public void createFlag() {
		Block block = originalFlagLocation.getBlock();
		block.setType(Material.STANDING_BANNER);

		//color
		banner = (CraftBanner) block.getState();
		banner.setBaseColor(color.color);

		//set patterns
		banner.setPatterns(color.patterns);
		//position
		org.bukkit.material.Banner materialBanner = (org.bukkit.material.Banner) banner.getData();
		materialBanner.setFacingDirection(BlockFace.EAST);

		//update block
		banner.update();
		rotateFlag();
	}

	private void rotateFlag() {
		rotateFlagRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				org.bukkit.material.Banner materialBanner = (org.bukkit.material.Banner) banner.getData();
				int currentRotationNum = Arrays.binarySearch(BlockFace.values(), materialBanner.getFacing());
				BlockFace newFace;
				if (currentRotationNum == BlockFace.values().length - 1) {
					newFace = BlockFace.SOUTH;
				} else {
					newFace = BlockFace.values()[currentRotationNum + 1];
				}
				materialBanner.setFacingDirection(newFace);
				banner.update();
			}
		};
		rotateFlagRunnable.runTaskTimer(CaptureTheFlagPlugin.plugin, 0, 20);
	}

}
