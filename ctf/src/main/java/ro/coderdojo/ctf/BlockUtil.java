/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.coderdojo.ctf;

import org.bukkit.block.Block;

/**
 *
 * @author mihai
 */
public class BlockUtil {

	public static boolean isSameBlock(Block block1, Block block2) {
		return (block1.getX() == block2.getX() && block1.getY() == block2.getY() && block1.getZ() == block2.getZ());
	}
}
