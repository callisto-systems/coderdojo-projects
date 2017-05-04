package ro.coderdojo.ctf;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

public class CaptureTheFlagPlugin extends JavaPlugin {

	World lobby;
	
	public static JavaPlugin plugin;

	@Override
	public void onEnable() {
		System.out.println("***********************  CAPTURE THE FLAG PLUGIN ***********************");
		loadLobbyWorld();
		plugin = this;
		
		Location location = new Location(lobby, 19.281, 231, 38.522);
		Block block = location.getBlock();
		block.setType(Material.STANDING_BANNER);
		Banner banner = (Banner)block.getState();
		banner.setBaseColor(DyeColor.RED);
		
		banner.update();
		
		getServer().getPluginManager().registerEvents(new LobbyListener(lobby), this);
	}

	public void loadLobbyWorld() {
		lobby = Bukkit.getServer().createWorld(new WorldCreator("world_lobby"));
		lobby.setGameRuleValue("doMobSpawning", "false");
		lobby.setDifficulty(Difficulty.HARD);
	}

	@Override
	public void onDisable() {
		getLogger().log(Level.INFO, "{0}.onDisable()", this.getClass().getName());
	}
}
