package ro.coderdojo.ctf;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public class CaptureTheFlagPlugin extends JavaPlugin {

	World lobby;

	@Override
	public void onEnable() {
		System.out.println("***********************  CAPTURE THE FLAG PLUGIN ***********************");
		loadWorld();
		getServer().getPluginManager().registerEvents(new PlayersListener(this, lobby), this);
	}

	public void loadWorld() {
		for (World world : Bukkit.getServer().getWorlds()) {
			if (world.getName().equals("world_lobby")) {
				System.out.println("Lobby World already loaded!");
				return;
			}
		}
		lobby = Bukkit.getServer().createWorld(new WorldCreator("world_lobby"));
		System.out.println("----------------------------------");
		for (World world : Bukkit.getServer().getWorlds()) {
			System.out.println("World: " + world.getName() + " - " + world.getUID());
		}
	}

	@Override
	public void onDisable() {
		getLogger().log(Level.INFO, "{0}.onDisable()", this.getClass().getName());
	}
}
