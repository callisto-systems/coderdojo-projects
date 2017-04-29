package ro.coderdojo.ctf;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public class CaptureTheFlagPlugin extends JavaPlugin {

	World lobby;
	
	public static JavaPlugin plugin;

	@Override
	public void onEnable() {
		System.out.println("***********************  CAPTURE THE FLAG PLUGIN ***********************");
		loadLobbyWorld();
		plugin = this;
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
