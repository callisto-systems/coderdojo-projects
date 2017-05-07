/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.coderdojo.ctf;

import java.util.Arrays;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftBanner;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author mihai
 */
public class FlagHandler {

	World world;
	Location originalFlagLocation;
	CraftBanner banner;
	Color color;
	
	Player flagCarrier;

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

	public void takeFlagIfNecessary(Player player) {
		if (!BlockUtil.isSameBlock(originalFlagLocation.getBlock(), player.getLocation().getBlock())) {
			return;
		}
		if (ScoresAndTeams.hasNoTeamInLobby(player)) {
			return;
		}
		if (ScoresAndTeams.isRed(player) && color == Color.RED) {
			return;
		}
		if (ScoresAndTeams.isBlue(player) && color == Color.BLUE) {
			return;
		}
		rotateFlagRunnable.cancel();
		banner.getBlock().setType(Material.AIR);
		flagCarrier = player;
		attachFlagToPlayer(player.getLocation(), player.getLocation().add(0, 1, 0));
	}

	public void attachFlagToPlayer(Location to, Location from) {
		from.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
		Block upBlock = from.getBlock().getRelative(BlockFace.UP);
		upBlock.setType(Material.STANDING_BANNER);
		CraftBanner craftBanner = (CraftBanner)			;
		craftBanner.setBaseColor(DyeColor.RED);
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

	BukkitRunnable rotateFlagRunnable;

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
