package ro.coderdojo.ctf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_11_R1.EnumParticle;
import net.minecraft.server.v1_11_R1.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;

import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyListener implements Listener {

	World lobby;
	FlagHandler redFlagHandler;
	FlagHandler blueFlagHandler;

	public LobbyListener(World lobby) {
		this.lobby = lobby;
		redFlagHandler = new FlagHandler(new Location(lobby, 19, 231, 38), FlagHandler.Color.RED);
		redFlagHandler.createFlag();

//		blueFlagHandler = new FlagHandler(new Location(lobby, 19, 231, 15), FlagHandler.Color.BLUE);
//		blueFlagHandler.createFlag();
	}

	@EventHandler
	public void playerJoined(PlayerJoinEvent event) throws Exception {
		Player player = event.getPlayer();
		player.setGameMode(GameMode.ADVENTURE);
		player.getInventory().clear();
		player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
		AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		healthAttribute.setBaseValue(20.00);
		player.teleport(new Location(lobby, 18, 231, 3));
		ScoresAndTeams.addNoTeamPlayerLobby(player);

		addSparks(event.getPlayer());
	}

	@EventHandler
	public void playerLeave(PlayerQuitEvent event) throws Exception {
		ScoresAndTeams.playerLeave(event.getPlayer());
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!ScoresAndTeams.isInLobby(event.getPlayer())) {
			return;
		}
		event.setRespawnLocation(new Location(lobby, 19.589, 231, 21.860));
	}

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		Player killed = event.getEntity();
		Player killer = event.getEntity().getKiller();

		if (killer == null) {
			return;
		}
		if (ScoresAndTeams.isRed(killer)) {
			ScoresAndTeams.redKills++;
		}

		if (ScoresAndTeams.isBlue(killer)) {
			ScoresAndTeams.blueKills++;
		}

		ScoresAndTeams.refreshLobbyBoards(event.getEntity());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		event.getPlayer().sendMessage(ChatColor.YELLOW + " Nu poti sparge " + ChatColor.RED + "arena!");
		event.setCancelled(true);
	}

	static List<Player> players = new ArrayList<>();

	private void addSparks(Player player) {
		players.add(player);
		if (ScoresAndTeams.lobbyNoTeamPlayers.size() == 1) {
			new BukkitRunnable() {
//				//EnumParticle.PORTAL // mov
//				//EnumParticle.REDSTONE // toaate culorile
				//EnumParticle.VILLAGER_HAPPY//verde
				EnumParticle particle = EnumParticle.REDSTONE;

				@Override
				public void run() {
					//params: particula,dacă e enable, locația, offset-ul, viteza, numar particule
					for (Player player : players) {
						PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, player.getLocation().getBlockX(), player.getLocation().getBlockY() + 4, player.getLocation().getBlockZ(), 1, 1, 1, 1, 3);
						for (Player online : Bukkit.getOnlinePlayers()) {
							((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
						}
					}
				}
			}.runTaskTimer(CaptureTheFlagPlugin.plugin, 0, 1);
		}
	}

	@EventHandler
	public void onPlayerMoveChangeTeam(PlayerMoveEvent event) {
		if (!ScoresAndTeams.isInLobby(event.getPlayer())) {
			return;
		}
		Block walkingBlock = event.getTo().getBlock().getRelative(BlockFace.DOWN);
		if (walkingBlock.getType() == Material.WOOL) {
			Wool wool = (Wool) walkingBlock.getState().getData();
			if (wool.getColor() == DyeColor.RED) {
				ScoresAndTeams.addRedToLobby(event.getPlayer());
			}
			if (wool.getColor() == DyeColor.BLUE) {
				ScoresAndTeams.addBlueToLobby(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerMoveAddLightAndFlag(PlayerMoveEvent event) {
		if (!ScoresAndTeams.isBlue(event.getPlayer()) && !ScoresAndTeams.isRed(event.getPlayer())) {
			return;
		}
		
		redFlagHandler.takeFlagIfNecessary(event.getPlayer());
				
		Block from = event.getFrom().getBlock().getRelative(BlockFace.DOWN);
		Block to = event.getTo().getBlock().getRelative(BlockFace.DOWN);
		Block aboveTo = to.getLocation().add(0, 1, 0).getBlock();

		if(BlockUtil.isSameBlock(to, from)) {
			return;
		}

		//restore last bloack
		if (lastBlock.get(event.getPlayer().getName()) != null) {
			lastBlock.get(event.getPlayer().getName())[0].update(true);
			lastBlock.get(event.getPlayer().getName())[1].update(true);
		}

		if (to.getType().isSolid()) { //&& !(to.getType() != Material.AIR || to.getType() != Material.WATER)
			//save current block
			lastBlock.put(event.getPlayer().getName(), new BlockState[]{to.getState(), aboveTo.getState()});
			//replace current wIth glowstone
			to.setType(Material.SEA_LANTERN);
			aboveTo.setType(Material.CARPET);
			if (ScoresAndTeams.isBlue(event.getPlayer())) {
				aboveTo.setData((byte) (11 & 0xFF));//blue
			}
			if (ScoresAndTeams.isRed(event.getPlayer())) {
				aboveTo.setData((byte) (14 & 0xFF));//red
			}

		}
	}
	
	static Map<String, BlockState[]> lastBlock = new HashMap<>();

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!ScoresAndTeams.isInLobby(event.getPlayer())) {
			return;
		}
		Player player = event.getPlayer();
		Action action = event.getAction();
		if (event.getClickedBlock() == null) {
			return;
		}
		Material material = event.getClickedBlock().getState().getType();
		Location location = event.getClickedBlock().getState().getLocation();
		if (action == Action.RIGHT_CLICK_BLOCK && material == Material.STONE_BUTTON) {
			if (ScoresAndTeams.isMatchStarted) {
				player.sendMessage("Meciul este deja pornit! Asteapta sa se termine!");
				return;
			}
			Location b1 = new Location(lobby, 21.0, 233.0, 21.0, location.getYaw(), location.getPitch());
			Location b2 = new Location(lobby, 19.0, 233.0, 19.0, location.getYaw(), location.getPitch());
			Location b3 = new Location(lobby, 17.0, 233.0, 21.0, location.getYaw(), location.getPitch());
			Location b4 = new Location(lobby, 19.0, 233.0, 23.0, location.getYaw(), location.getPitch());
			if (location.equals(b1) || location.equals(b2) || location.equals(b3) || location.equals(b4)) {
				timer();
				player.sendMessage("Ai pornit meciul!");
				CaptureTheFlagPlugin.plugin.getServer().broadcastMessage("Meciul a fost pornit!");
			}
		}
	}

	private void timer() {
		CountDownTimer timer = new CountDownTimer(this);
		timer.runTaskTimer(CaptureTheFlagPlugin.plugin, 3, 1);
	}
}
