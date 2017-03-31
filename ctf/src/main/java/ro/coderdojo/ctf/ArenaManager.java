package ro.coderdojo.ctf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// NOT THREAD SAFE!
public class ArenaManager {
	// Singleton instance

	private static ArenaManager am;

	// Player data
	private final Map<UUID, Location> locs = new HashMap<UUID, Location>();
	private final Map<UUID, ItemStack[]> inv = new HashMap<UUID, ItemStack[]>();
	private final Map<UUID, ItemStack[]> armor = new HashMap<UUID, ItemStack[]>();

	// Hmm, what do you think THIS might be?
	private final List<Arena> arenas = new ArrayList<Arena>();
	// Keep track of the current arena ID
	private int arenaSize = 0;

	private ArenaManager() {
	} // Prevent instantiation

	// Singleton accessor with lazy initialization
	public static ArenaManager getManager() {
		if (am == null) {
			am = new ArenaManager();
		}

		return am;
	}

	/**
	 * Acquires an arena based on its ID number
	 *
	 * @param i the ID to search the arenas for
	 * @return the arena possessing the specified ID
	 */
	public Arena getArena(int i) {
		for (Arena a : this.arenas) {
			if (a.getId() == i) {
				return a;
			}
		}

		return null; // Not found
	}

	/**
	 * Adds the player to an arena
	 *
	 * <p>
	 * Gets the arena by ID, checks that it exists, and check the player isn't already in a game.</p>
	 *
	 * @param p the player to add
	 * @param i the arena ID. A check will be done to ensure its validity.
	 */
	public void addPlayer(Player p, int i) {
		Arena a = this.getArena(i);
		if (a == null) {
			p.sendMessage("Invalid arena!");
			return;
		}

		if (this.isInGame(p)) {
			p.sendMessage("Cannot join more than 1 game!");
			return;
		}

		// Adds the player to the arena player list
		a.getPlayers().add(p.getUniqueId());

		// Save the inventory and armor
		inv.put(p.getUniqueId(), p.getInventory().getContents());
		armor.put(p.getUniqueId(), p.getInventory().getArmorContents());

		// Clear inventory and armor
		p.getInventory().setArmorContents(null);
		p.getInventory().clear();

		// Save the players's last location before joining,
		// then teleporting them to the arena spawn
		locs.put(p.getUniqueId(), p.getLocation());
		p.teleport(a.spawn);
	}

	/**
	 * Removes the player from their current arena.
	 *
	 * <p>
	 * The player is allowed to not be in game, a check will be performed to ensure the validity of the arena</p>
	 *
	 * @param p the player to remove from the arena
	 */
	public void removePlayer(Player p) {
		Arena a = null;

		// Searches each arena for the player
		for (Arena arena : this.arenas) {
			if (arena.getPlayers().contains(p.getUniqueId())) {
				a = arena;
			}
		}

		// Check arena validity
		if (a == null) {
			p.sendMessage("Invalid operation!");
			return;
		}

		// Remove from arena player lost
		a.getPlayers().remove(p.getName());

		// Remove inventory acquired during the game
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);

		// Restore inventory and armor
		p.getInventory().setContents(inv.get(p.getName()));
		p.getInventory().setArmorContents(armor.get(p.getName()));

		// Remove player data entries
		inv.remove(p.getUniqueId());
		armor.remove(p.getUniqueId());

		// Teleport to original location, remove it too
		p.teleport(locs.get(p.getUniqueId()));
		locs.remove(p.getUniqueId());

		// Heh, you're safe now :)
		p.setFireTicks(0);
	}

	/**
	 * Creates an arena at the specified location
	 *
	 * @param l the location for arena spawn
	 * @return the arena created
	 */
	public Arena createArena(Location l) {
		this.arenaSize++;

		Arena a = new Arena(l, this.arenaSize);
		this.arenas.add(a);

		return a;
	}

	/**
	 * Checks if the player is currently in an arena
	 *
	 * @param p the player to check
	 * @return {@code true} if the player is in game
	 */
	public boolean isInGame(Player p) {
		for (Arena a : this.arenas) {
			if (a.getPlayers().contains(p.getUniqueId())) {
				return true;
			}
		}
		return false;
	}

	// UTILITY METHODS
	public String serializeLoc(Location l) {
		return l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
	}

	public Location deserializeLoc(String s) {
		String[] st = s.split(",");
		return new Location(Bukkit.getWorld(st[0]), Integer.parseInt(st[1]), Integer.parseInt(st[2]), Integer.parseInt(st[3]));
	}
}
