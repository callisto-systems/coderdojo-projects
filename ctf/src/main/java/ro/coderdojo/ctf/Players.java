/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.coderdojo.ctf;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author mihai
 */
public class Players {
	
	public static boolean isMatchStarted = false;
	
	public static List<Player> lobbyNoTeamPlayers = new ArrayList<>();
	public static List<Player> lobbyRedPlayers = new ArrayList<>();
	public static List<Player> lobbyBluePlayers = new ArrayList<>();
	
	public static List<Player> arenaRedPlayers = new ArrayList<>();
	public static List<Player> arenaBluePlayers = new ArrayList<>();
	
	public static boolean isInLobby(Player player) {
		if(lobbyNoTeamPlayers.contains(player) || lobbyRedPlayers.contains(player) || lobbyBluePlayers.contains(player)) {
			return true;
		}
		return false;
	}
	
	public static boolean isInArena(Player player) {
		if(arenaRedPlayers.contains(player) || arenaBluePlayers.contains(player)) {
			return true;
		}
		return false;
	}
	
}
