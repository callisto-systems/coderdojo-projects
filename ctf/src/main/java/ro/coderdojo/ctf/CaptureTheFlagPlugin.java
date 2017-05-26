package ro.coderdojo.ctf;

import java.util.logging.Level;
import static net.minecraft.server.v1_11_R1.Block.w;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class CaptureTheFlagPlugin extends JavaPlugin {

	public static World lobby;
	public static World arena;

	public static JavaPlugin plugin;

	@Override
	public void onEnable() {
		System.out.println("***********************  CAPTURE THE FLAG PLUGIN ***********************");
		plugin = this;

		loadLobbyWorld();
		getServer().getPluginManager().registerEvents(new LobbyListener(), this);
		loadArenaWorld();
		CaptureTheFlagPlugin.plugin.getServer().getPluginManager().registerEvents(new ArenaListener(), this);
		
		killAllMobs();
	}

	public void loadLobbyWorld() {
		lobby = Bukkit.getServer().createWorld(new WorldCreator("world_lobby"));
		lobby.setGameRuleValue("doMobSpawning", "false");
		lobby.setDifficulty(Difficulty.HARD);
		lobby.setBiome(0, 0, Biome.VOID);
	}

	private void loadArenaWorld() {
		arena = Bukkit.getServer().createWorld(new WorldCreator("world_arena"));
		arena.setGameRuleValue("doMobSpawning", "false");
		arena.setDifficulty(Difficulty.HARD);
	}

	private void killAllMobs() {
		for (Entity e : lobby.getEntities()) {
			e.remove();
		}
		for (Entity e : arena.getEntities()) {
			e.remove();
		}
	}

	@Override
	public void onDisable() {
		getLogger().log(Level.INFO, "{0}.onDisable()", this.getClass().getName());
	}
}
