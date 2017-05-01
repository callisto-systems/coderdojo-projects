/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.coderdojo.ctf;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author mihai
 */
public class Players {

	public static boolean isMatchStarted = false;

	private static List<Player> lobbyNoTeamPlayers = new ArrayList<>();
	private static List<Player> lobbyRedPlayers = new ArrayList<>();
	private static List<Player> lobbyBluePlayers = new ArrayList<>();

	public static List<Player> arenaRedPlayers = new ArrayList<>();
	public static List<Player> arenaBluePlayers = new ArrayList<>();
	
	public static int redKills = 0;
	public static int blueKills = 0;
	
	private static Scoreboard lobyBoard;
	static {
		lobyBoard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		lobyBoard.registerNewTeam("blueTeam");
		lobyBoard.getTeam("blueTeam").setPrefix(ChatColor.BLUE + "[ALBASTRU] " + ChatColor.WHITE + "[");
		lobyBoard.getTeam("blueTeam").setSuffix("]");
		lobyBoard.getTeam("blueTeam").setDisplayName("Echipa Albastră");
		lobyBoard.getTeam("blueTeam").setCanSeeFriendlyInvisibles(true);
		lobyBoard.getTeam("blueTeam").setAllowFriendlyFire(false);

		lobyBoard.registerNewTeam("redTeam");
		lobyBoard.getTeam("redTeam").setPrefix(ChatColor.RED + "[ROȘU] " + ChatColor.WHITE + "[");
		lobyBoard.getTeam("redTeam").setSuffix("]");
		lobyBoard.getTeam("redTeam").setDisplayName("Echipa Roșie");
		lobyBoard.getTeam("redTeam").setCanSeeFriendlyInvisibles(true);
		lobyBoard.getTeam("redTeam").setAllowFriendlyFire(false);
	}

	static void playerLeave(Player player) {
		if (isBlue(player)) {
			lobyBoard.getTeam("blueTeam").removeEntry(player.getName());
		}
		if (isRed(player)) {
			lobyBoard.getTeam("redTeam").removeEntry(player.getName());
		}
		player.getScoreboard().getObjective(player.getName() + "_deaths").unregister();
		
		lobbyRedPlayers.remove(player);
		lobbyBluePlayers.remove(player);
		lobbyNoTeamPlayers.remove(player);
		arenaRedPlayers.remove(player);
		arenaBluePlayers.remove(player);

		refreshLobbyBoards(player);
	}

	public static void addNoTeamPlayerLobby(Player player) {
		if (lobbyNoTeamPlayers.contains(player)) {
			return;
		}
		lobbyRedPlayers.remove(player);
		lobbyBluePlayers.remove(player);
		lobbyNoTeamPlayers.add(player);

		Objective deathsObjective = lobyBoard.registerNewObjective(player.getName() + "_deaths", "playerKillCount");
		deathsObjective.setDisplayName("Kills");
		deathsObjective.getScore("kills").setScore(0);
		deathsObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

		player.sendMessage(ChatColor.GOLD + " Încă nu ai intrat în nici o echipă");
		refreshLobbyBoards(player);
	}

	public static void addRedToLobby(Player player) {
		lobyBoard.getTeam("redTeam").addEntry(player.getName());
		if (lobbyRedPlayers.contains(player)) {
			return;
		}
		lobbyRedPlayers.add(player);
		lobbyBluePlayers.remove(player);
		lobbyNoTeamPlayers.remove(player);

		player.sendMessage(ChatColor.RED + " Esti in echipa rosie!");
		CaptureTheFlagPlugin.plugin.getServer().broadcastMessage("Jucătorul " + player.getName() + " a trecut în echipa " + ChatColor.RED + "roșie");
		refreshLobbyBoards(player);
	}

	public static void addBlueToLobby(Player player) {
		lobyBoard.getTeam("blueTeam").addEntry(player.getName());
		if (lobbyBluePlayers.contains(player)) {
			return;
		}
		lobbyRedPlayers.remove(player);
		lobbyBluePlayers.add(player);
		lobbyNoTeamPlayers.remove(player);
		player.sendMessage(ChatColor.BLUE + " Esti in echipa albastra!");
		CaptureTheFlagPlugin.plugin.getServer().broadcastMessage("Jucătorul " + player.getName() + " a trecut în echipa " + ChatColor.BLUE + "albastră");
		refreshLobbyBoards(player);
	}

	public static void refreshLobbyBoards(Player player) {
		//set score board to player (usefull if player is new)
		if (player != null) {
			if (player.getScoreboard().getObjective("players") == null) {
				player.setScoreboard(lobyBoard);
			}
		}
		
		Objective objective = lobyBoard.getObjective("players");
		
		if(objective != null) {
			objective.unregister();
		}
		
		Objective playersObjective = lobyBoard.registerNewObjective("players", "dummy");
		playersObjective.setDisplayName("Killuri");
		playersObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
						
		System.out.println("Set Objectives...");
		 
		
		lobyBoard.getObjective("players").getScore(ChatColor.GOLD + String.format("%1$-16s:%2$4d", "Jucători Fără echipă", lobbyNoTeamPlayers.size())).setScore(-1);
		lobyBoard.getObjective("players").getScore(ChatColor.BLUE + String.format("%1$-16s:%2$4d", "Jucători Albaștrii", lobbyBluePlayers.size())).setScore(-2);
		lobyBoard.getObjective("players").getScore(ChatColor.RED + String.format("%1$-16s:%2$4d", "Jucători Roșii", lobbyRedPlayers.size())).setScore(-3);
		
		
		lobyBoard.getObjective("players").getScore(ChatColor.WHITE + " ").setScore(-4);
		lobyBoard.getObjective("players").getScore(ChatColor.WHITE + " Număr de jucători").setScore(-5);
		
		
		lobyBoard.getObjective("players").getScore(ChatColor.GOLD + String.format("%1$-16s:%2$4d", "Killuri Roșii", redKills)).setScore(-6);
		lobyBoard.getObjective("players").getScore(ChatColor.GOLD + String.format("%1$-16s:%2$4d", "Killuri Albaștrii", blueKills)).setScore(-7);

		//16
		
	}

	public static List<Player> getAllLobyPlayers() {
		List<Player> allPlayers = new ArrayList<>();
		allPlayers.addAll(lobbyNoTeamPlayers);
		allPlayers.addAll(lobbyBluePlayers);
		allPlayers.addAll(lobbyRedPlayers);
		return allPlayers;
	}

	public static boolean isInLobby(Player player) {
		if (lobbyNoTeamPlayers.contains(player) || lobbyRedPlayers.contains(player) || lobbyBluePlayers.contains(player)) {
			return true;
		}
		return false;
	}

	public static boolean isInArena(Player player) {
		if (arenaRedPlayers.contains(player) || arenaBluePlayers.contains(player)) {
			return true;
		}
		return false;
	}

	public static boolean isRedInArena(Player player) {
		return arenaRedPlayers.contains(player);
	}

	public static boolean isBlueInArena(Player player) {
		return arenaBluePlayers.contains(player);
	}

	public static boolean isRed(Player player) {
		return isRedInArena(player) || isRedInLobby(player);
	}
	
	public static boolean isBlue(String playerName) {
		if (lobyBoard.getTeam("blueTeam").hasEntry(playerName)) {
			return true;
		}
		return false;
	}

	public static boolean isBlue(Player player) {
		return isBlueInArena(player) || isBlueInLobby(player);
	}

	public static boolean isRedInLobby(Player player) {
		return lobbyRedPlayers.contains(player);
	}

	public static boolean isBlueInLobby(Player player) {
		return lobbyBluePlayers.contains(player);
	}

	public static boolean hasNoTeamInLobby(Player player) {
		return lobbyNoTeamPlayers.contains(player);
	}

	static void moveToArena(Player player) {
		if (lobbyRedPlayers.contains(player)) {
			lobbyRedPlayers.remove(player);
			arenaRedPlayers.add(player);
			System.out.println("Teleported RED player to ARENA: " + player.getName());
			return;
		}
		if (lobbyBluePlayers.contains(player)) {
			lobbyBluePlayers.remove(player);
			arenaBluePlayers.add(player);
			System.out.println("Teleported BLUE player to ARENA: " + player.getName());
			return;
		}
		throw new RuntimeException("Cannot move player to arena. In not blue or red.");

	}

}
