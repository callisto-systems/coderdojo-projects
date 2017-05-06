package ro.coderdojo.ctf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.material.Wool;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftBanner;

public class LobbyListener implements Listener {

	World lobby;

	public LobbyListener(World lobby) {
		this.lobby = lobby;
//		setFlagRed();
//		setFlagBlue();
	
		//cd 3
		Location location = new Location(lobby, 19, 231, 8);
		location.getBlock().setType(Material.AIR);


	}

	@EventHandler
	public void playerJoined(PlayerJoinEvent event) throws Exception {
		Player player = event.getPlayer();
		//ch 1
//		player.setGameMode(GameMode.ADVENTURE);
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
		AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		healthAttribute.setBaseValue(20.00);
//		player.teleport(new Location(lobby, 19.589, 231, 21.860));
		player.teleport(new Location(lobby, 18, 231, 3));
		ScoresAndTeams.addNoTeamPlayerLobby(player);

		
//		block.setType(Material.STANDING_BANNER);
//		BlockState state =  block.getState();
//		block.setData((byte) (4 & 0xFF));
//		block.setMetadata(metadataKey, newMetadataValue);
//		BlockState state = block.getState();
		
//		block.setType(Material.STANDING_BANNER);
//1		block.setData((byte) (4 & 0xFF));


//		BlockState bs = block.getState();
//		BannerMeta banner = (BannerMeta) bs.getData();
//		banner.setBaseColor(DyeColor.BLUE);
//		bs.setData((MaterialData) banner);
//		bs.update();

	}

//	blockstate craftBannerState;
//
//	private void setFlagRed() {
//		Location location = new Location(lobby, 18, 231, 3);
//		Block block = location.getBlock();
//
////		BlockState stateX = block.getState();
////		stateX.setType(Material.STANDING_BANNER);
//		craftBannerState = (CraftBanner) block.getState();
//
//		MaterialData metadata = craftBannerState.getData();
//		metadata.setData((byte) (4 & 0xFF));
//
//		craftBannerState.setType(Material.STANDING_BANNER);
//		craftBannerState.setBaseColor(DyeColor.RED);
//
//		List<Pattern> patterns = new ArrayList<Pattern>(); //Create a new List called 'patterns'
//
//		patterns.add(new Pattern(DyeColor.WHITE, PatternType.SKULL));
//
//		craftBannerState.setPatterns(patterns);
//		craftBannerState.update();
//
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				MaterialData metadata = craftBannerState.getData();
//				byte newData = (byte) (metadata.getData() + ((byte) 1));
//				if ((newData & 0xFF) > (15 & 0xFF)) {
//					newData = 0 & 0xFF;
//				}
//				metadata.setData(newData);
//				craftBannerState.update();
//			}
//		}.runTaskTimer(CaptureTheFlagPlugin.plugin, 0, 4);
//
//	}

//	CraftBlockState crafBlueBannerState;
//	private void setFlagBlue() {
//		Location location = new Location(lobby, 21, 231, 1);
//		Block block = location.getBlock();
//		
//		BlockState stateX = block.getState();
//		stateX.setType(Material.STANDING_BANNER);
//		stateX.update();
//		
//		crafBlueBannerState = (CraftBlockState) (CraftBlockState) stateX;
//
//		MaterialData metadata = crafBlueBannerState.getData();
//		metadata.setData((byte) (4 & 0xFF));
//
//		crafBlueBannerState.setBaseColor(DyeColor.BLUE);
//
//		List<Pattern> patterns = new ArrayList<Pattern>(); //Create a new List called 'patterns'
//
////		patterns.add(new Pattern(DyeColor.RED, PatternType.HALF_HORIZONTAL));
////		patterns.add(new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE));
////		patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_TOP));
////		patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
////		patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
////		patterns.add(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE));
//		patterns.add(new Pattern(DyeColor.WHITE, PatternType.CREEPER));
////		patterns.add(new Pattern(DyeColor.MAGENTA, PatternType.BORDER));
//
//		crafBlueBannerState.setPatterns(patterns);
//		crafBlueBannerState.update();
//		
//		new BukkitRunnable() {
//				@Override
//				public void run() {
//					MaterialData metadata = crafBlueBannerState.getData();
//					byte newData = (byte) (metadata.getData() + ((byte)1));
//					if((newData & 0xFF) > (15 & 0xFF)) {
//						newData = 0 & 0xFF;
//					}
//					metadata.setData(newData);
//					crafBlueBannerState.update();
//				}
//			}.runTaskTimer(CaptureTheFlagPlugin.plugin, 0, 4);
//
//	}
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
		//cd 2
//		event.setCancelled(true);

		//cd 4
		//spown
		Location location = new Location(lobby, 19, 231, 8);
		Block block = location.getBlock();
		block.setType(Material.STANDING_BANNER);

		//color
		CraftBanner blockstate = (CraftBanner)block.getState();
		blockstate.setBaseColor(DyeColor.RED);
		
		//set patterns
		List<Pattern> patterns = new ArrayList<Pattern>();
		patterns.add(new Pattern(DyeColor.BROWN, PatternType.TRIANGLE_TOP));
		patterns.add(new Pattern(DyeColor.WHITE, PatternType.SKULL));
		blockstate.setPatterns(patterns);
		//position
		org.bukkit.material.Banner materialBanner = (org.bukkit.material.Banner) blockstate.getData();
		materialBanner.setFacingDirection(BlockFace.EAST);
		
		//update block
		blockstate.update();
		
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
