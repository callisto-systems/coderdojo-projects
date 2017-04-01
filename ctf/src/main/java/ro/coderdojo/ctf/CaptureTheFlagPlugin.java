package ro.coderdojo.ctf;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public class CaptureTheFlagPlugin extends JavaPlugin {

	World lobby;

	@Override
	public void onEnable() {
		System.out.println("***********************  CAPTURE THE FLAG PLUGIN ***********************");
		loadLobbyWorld();
		getServer().getPluginManager().registerEvents(new LobbyListener(this, lobby), this);
	}

	public void loadLobbyWorld() {
		lobby = Bukkit.getServer().createWorld(new WorldCreator("world_lobby"));
		lobby.setGameRuleValue("doMobSpawning", "false");
		lobby.setDifficulty(Difficulty.PEACEFUL);
	}

	@Override
	public void onDisable() {
		getLogger().log(Level.INFO, "{0}.onDisable()", this.getClass().getName());
	}
}
