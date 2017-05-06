/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.coderdojo.ctf;

import java.util.ArrayList;
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
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author mihai
 */
public class FlagHandler {
	
	World world;
	Location location;
	CraftBanner banner;
	Color color;
	
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
		this.location = location;
		this.color = color;
	}

	

	public void createFlag() {
		Block block = location.getBlock();
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
		new BukkitRunnable() {
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
		}.runTaskTimer(CaptureTheFlagPlugin.plugin, 0, 20);
	}

}
