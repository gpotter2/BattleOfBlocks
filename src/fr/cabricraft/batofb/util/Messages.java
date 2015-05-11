/*
 *  Copyright (C) 2015 Gabriel POTTER (gpotter2)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package fr.cabricraft.batofb.util;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import fr.cabricraft.batofb.BattleOfBlocks;

public class Messages {
	BattleOfBlocks battleOfBlocks;
	public Messages(BattleOfBlocks battleOfBlocks){
		this.battleOfBlocks = battleOfBlocks;
		if(battleOfBlocks.controlname == null) battleOfBlocks.controlname = "BattleOfBlocks";
		this.PNC = ChatColor.RED + "[" + battleOfBlocks.controlname + "] " + ChatColor.RESET;
		PERMISSION_DENIED = PNC + "&cYou don't have permission to do that !";
		KILLED_BY = PNC + "&cYou were killed by %killer% ! Respawn !";
		OTHER_DIE = PNC + "&cYou die ! Respawn !";
		YOU_JOINED_THE_GAME = PNC + "&aYou joined the game ! To leave it, type &e/leave &a!";
		OTHER_JOIN_THE_GAME = PNC + "&aThe player &r %player% &ajoined the game !";
		OTHER_LEFT_THE_GAME = PNC + "&cThe player &r %player% &cleft the game !";
		BLUE_WIN = PNC + "&9The BLUE team has won !!!";
		RED_WIN = PNC + "&cThe RED team has won !!!";
		TOO_MANY_PEOPLE = PNC + "&cToo many people connected !";
		ALREADY_IN_GAME = PNC + "&cYou are already in game !";
		TEAM_FULL = PNC + "&cThis team is full !";
		GAME_FULL = PNC + "&cThe game is full !";
		YOU_TEAM_BLUE = PNC + "&aYou are in BLUE team !";
		YOU_TEAM_RED = PNC + "&aYou are in RED team !";
		YOUR_TEAM_BLOCK = PNC + "&cYou cannot destroy your team's blocks !";
		BLUE_LIVES = PNC + "&9Blues lose a life, they keep %lives% lives !";
		RED_LIVES = PNC + "&cReds lose a life, they keep %lives% lives !";
		NO_COMMANDS_IN_GAME = PNC + "&cYou cannot use commands in game ! To leave, type &e/leave &c!";
		GAME_START_IN_X_SECONDS = PNC + "&aThe game start in %secs% seconds !";
		GAME_ALREADY_STARTED = PNC + "&cGame already started !";
		GAME_NOT_STARTED = PNC + "&cGame not started !";
		SLOW = PNC + "&aSlow your ennemies !";
		REDSTONE_POWER = PNC + "&aInstant teleport in 7 blocks where you are looking at !";
		JUMP = PNC + "&aHave a jump boots for 10 secs !";
		IRON_MAN = PNC + "&aGo on an iron golem for 10 sec !";
		FORCEFIELD = PNC + "&aHave a forefield for 7 secs !";
		KNOCKBACK = PNC + "&aHave a knockback X stick for 15 secs !";
		FIREBALLS = PNC + "&aShot fireballs with your stick for 10 secs !";
		BERSERK = PNC + "&aBecome a berserk for 10 sec !";
		TNTBOOM = PNC + "&aKill your ennemies !";
		BE_VIP = PNC + "&cYou must be VIP to bypass this restriction !";
		POWERUP = PNC + "&aThe player %player% activate %powerup% !";
		POWERUP_END = PNC + "&aYour %powerup% is now off !";
		HERE_YOU_ARE = PNC + "&aHere you are !";
		NOT_ENOUGH_MONEY = PNC + "&cYou don't have enough money !";
		ALREADY_BOUGHT = PNC + "&cYou already bougth that !";
		SUCCEFULLY_BOUGHT = PNC + "&aYou succefully bought the perk %perk% !";
		DIDNT_BOUGHT_YET = PNC + "&cYou didn't buy this perk !";
		IRON_GOLEM_DIE = PNC + "&cYour iron golem die !";
		MONEY_EARN = PNC + "&aYou earn a total of %money% !";
		MONEY_REMOVE = PNC + "&aYou spend a total of %money% !";
		MONEY_NOW = PNC + "&aYou have now %money% !";
		FUNCTION_DISABLED = PNC + "&cThis function is disabled !";
		NEED = "Need";
		config = battleOfBlocks.conffile("messages.yml");
	}
	
	public void reloadPNC(){
		this.PNC = ChatColor.RED + "[" + battleOfBlocks.controlname + "] " + ChatColor.RESET;
	}
	
	public String PNC;
	public FileConfiguration config;
	
	//IN ARENAS
	public String PERMISSION_DENIED;
	public String KILLED_BY;
	public String OTHER_DIE;
	public String YOU_JOINED_THE_GAME;
	public String OTHER_JOIN_THE_GAME;
	public String OTHER_LEFT_THE_GAME;
	public String BLUE_WIN;
	public String RED_WIN;
	public String TOO_MANY_PEOPLE;
	public String ALREADY_IN_GAME;
	public String TEAM_FULL;
	public String YOU_TEAM_BLUE;
	public String YOU_TEAM_RED;
	public String YOUR_TEAM_BLOCK;
	public String BLUE_LIVES;
	public String RED_LIVES;
	public String GAME_FULL;
	public String NO_COMMANDS_IN_GAME;
	public String GAME_START_IN_X_SECONDS;
	public String GAME_ALREADY_STARTED;
	public String GAME_NOT_STARTED;
	
	//Kits
	public String SLOW;
	public String REDSTONE_POWER;
	public String JUMP;
	public String IRON_MAN;
	public String FORCEFIELD;
	public String KNOCKBACK;
	public String FIREBALLS;
	public String BERSERK;
	public String TNTBOOM;
	
	//IN SIGN_UTILITY
	public String BE_VIP;
	
	//IN POWERUPS
	public String POWERUP;
	public String POWERUP_END;
	public String HERE_YOU_ARE;
	public String NOT_ENOUGH_MONEY;
	public String ALREADY_BOUGHT;
	public String SUCCEFULLY_BOUGHT;
	public String DIDNT_BOUGHT_YET;
	
	//IN  IRON_GOLEM_CONTROL
	public String IRON_GOLEM_DIE;
	
	//IN ECONOMY_MANAGER
	public String MONEY_EARN;
	public String MONEY_REMOVE;
	public String MONEY_NOW;
	
	//IN BARAPI
	public String NEED;
	
	//OTHER
	public String FUNCTION_DISABLED;
	
	public String structurate(String ToStructurate, Player concerned, Player killer){
		String DoneStructurate = ToStructurate;
		if(killer != null) DoneStructurate = DoneStructurate.replace("%killer%", killer.getDisplayName());
		if(concerned != null) DoneStructurate = DoneStructurate.replace("%player%", concerned.getDisplayName());
		return DoneStructurate;
	}
	public String structurateLives(String ToStructurate, int lives){
		String DoneStructurate = ToStructurate;
		DoneStructurate = DoneStructurate.replace("%lives%", Integer.toString(lives));
		return DoneStructurate;
	}
	public String structuratePerks(String ToStructurate, String perk){
		String DoneStructurate = ToStructurate;
		DoneStructurate = DoneStructurate.replace("%perk%", perk);
		return DoneStructurate;
	}
	public String structuratePowerups(String ToStructurate,String Powerup, Player player){
		String DoneStructurate = ToStructurate;
		if(Powerup != null) DoneStructurate = DoneStructurate.replace("%powerup%", Powerup);
		if(player != null) DoneStructurate = DoneStructurate.replace("%player%", player.getDisplayName());
		return DoneStructurate;
	}
	public String structurateMoney(String ToStructurate, String Money){
		String DoneStructurate = ToStructurate;
		DoneStructurate = DoneStructurate.replace("%money%", Money);
		return DoneStructurate;
	}
	public String stucturateTime(String ToStructurate, int time){
		String DoneStructurate = ToStructurate;
		DoneStructurate = DoneStructurate.replace("%secs%", String.valueOf(time));
		return DoneStructurate;
	}
	public String putColor(String brut){
		String Colordone = brut;
		Colordone = Colordone.replace("&4", ChatColor.DARK_RED + "");
		Colordone = Colordone.replace("&c", ChatColor.RED + "");
		Colordone = Colordone.replace("&6", ChatColor.GOLD + "");
		Colordone = Colordone.replace("&e", ChatColor.YELLOW + "");
		Colordone = Colordone.replace("&2", ChatColor.DARK_GREEN + "");
		Colordone = Colordone.replace("&a", ChatColor.GREEN + "");
		Colordone = Colordone.replace("&b", ChatColor.AQUA + "");
		Colordone = Colordone.replace("&3", ChatColor.DARK_AQUA + "");
		Colordone = Colordone.replace("&1", ChatColor.DARK_BLUE + "");
		Colordone = Colordone.replace("&9", ChatColor.BLUE + "");
		Colordone = Colordone.replace("&d", ChatColor.LIGHT_PURPLE + "");
		Colordone = Colordone.replace("&5", ChatColor.DARK_PURPLE + "");
		Colordone = Colordone.replace("&f", ChatColor.WHITE + "");
		Colordone = Colordone.replace("&7", ChatColor.GRAY + "");
		Colordone = Colordone.replace("&8", ChatColor.DARK_GRAY + "");
		Colordone = Colordone.replace("&0", ChatColor.BLACK + "");
		Colordone = Colordone.replace("&r", ChatColor.RESET + "");
		Colordone = Colordone.replace("&m", ChatColor.STRIKETHROUGH + "");
		Colordone = Colordone.replace("&o", ChatColor.ITALIC + "");
		Colordone = Colordone.replace("&k", ChatColor.MAGIC + "");
		Colordone = Colordone.replace("&l", ChatColor.BOLD + "");
		Colordone = Colordone.replace("&n", ChatColor.UNDERLINE + "");
		return Colordone;
	}
	public void load(){
		try {
			if(config.getConfigurationSection("Messages") != null){
				ConfigurationSection cs = config.getConfigurationSection("Messages");
				if(cs.getString("ALREADY_IN_GAME") != null) ALREADY_IN_GAME = PNC + cs.getString("ALREADY_IN_GAME");
				if(cs.getString("GAME_FULL") != null) GAME_FULL = PNC + cs.getString("GAME_FULL");
				if(cs.getString("BE_VIP") != null) BE_VIP = PNC + cs.getString("BE_VIP");
				if(cs.getString("BLUE_LIVES") != null) BLUE_LIVES = PNC + cs.getString("BLUE_LIVES");
				if(cs.getString("BLUE_WIN") != null) BLUE_WIN = PNC + cs.getString("BLUE_WIN");
				if(cs.getString("MONEY_EARN") != null) MONEY_EARN = PNC + cs.getString("MONEY_EARN");
				if(cs.getString("HERE_YOU_ARE") != null) HERE_YOU_ARE = PNC + cs.getString("HERE_YOU_ARE");
				if(cs.getString("IRON_GOLEM_DIE") != null) IRON_GOLEM_DIE = PNC + cs.getString("IRON_GOLEM_DIE");
				if(cs.getString("KILLED_BY") != null) KILLED_BY = PNC + cs.getString("KILLED_BY");
				if(cs.getString("MONEY_NOW") != null) MONEY_NOW = PNC + cs.getString("MONEY_NOW");
				if(cs.getString("NO_COMMANDS_IN_GAME") != null) NO_COMMANDS_IN_GAME = PNC + cs.getString("NO_COMMANDS_IN_GAME");
				if(cs.getString("NOT_ENOUGH_MONEY") != null) NOT_ENOUGH_MONEY = PNC + cs.getString("NOT_ENOUGH_MONEY");
				if(cs.getString("OTHER_DIE") != null) OTHER_DIE = PNC + cs.getString("OTHER_DIE");
				if(cs.getString("PERMISSION_DENIED") != null) PERMISSION_DENIED = PNC + cs.getString("PERMISSION_DENIED");
				if(cs.getString("POWERUP") != null) POWERUP = PNC + cs.getString("POWERUP");
				if(cs.getString("POWERUP_END") != null) POWERUP_END = PNC + cs.getString("POWERUP_END");
				if(cs.getString("RED_LIVES") != null) RED_LIVES = PNC + cs.getString("RED_LIVES");
				if(cs.getString("RED_WIN") != null) RED_WIN = PNC + cs.getString("RED_WIN");
				if(cs.getString("TEAM_FULL") != null) TEAM_FULL = PNC + cs.getString("TEAM_FULL");
				if(cs.getString("TOO_MANY_PEOPLE") != null) TOO_MANY_PEOPLE = PNC + cs.getString("TOO_MANY_PEOPLE");
				if(cs.getString("YOU_JOINED_THE_GAME") != null) YOU_JOINED_THE_GAME = PNC + cs.getString("YOU_JOINED_THE_GAME");
				if(cs.getString("YOU_TEAM_BLUE") != null) YOU_TEAM_BLUE = PNC + cs.getString("YOU_TEAM_BLUE");
				if(cs.getString("YOU_TEAM_RED") != null) YOU_TEAM_RED = PNC + cs.getString("YOU_TEAM_RED");
				if(cs.getString("YOUR_TEAM_BLOCK") != null) YOUR_TEAM_BLOCK = PNC + cs.getString("YOUR_TEAM_BLOCK");
				if(cs.getString("DIDNT_BOUGHT_YET") != null) DIDNT_BOUGHT_YET = PNC + cs.getString("DIDNT_BOUGHT_YET");
				if(cs.getString("ALREADY_BOUGHT") != null) ALREADY_BOUGHT = PNC + cs.getString("ALREADY_BOUGHT");
				if(cs.getString("SUCCEFULLY_BOUGHT") != null) SUCCEFULLY_BOUGHT = PNC + cs.getString("SUCCEFULLY_BOUGHT");
				if(cs.getString("NEED") != null) NEED = cs.getString("NEED");
				if(cs.getString("OTHER_JOIN_THE_GAME") != null) OTHER_JOIN_THE_GAME = PNC + cs.getString("OTHER_JOIN_THE_GAME");
				if(cs.getString("GAME_START_IN_X_SECONDS") != null) GAME_START_IN_X_SECONDS = PNC + cs.getString("GAME_START_IN_X_SECONDS");
				if(cs.getString("GAME_ALREADY_STARTED") != null) GAME_ALREADY_STARTED = PNC + cs.getString("GAME_ALREADY_STARTED");
				if(cs.getString("GAME_NOT_STARTED") != null) GAME_NOT_STARTED = PNC + cs.getString("GAME_NOT_STARTED");
				if(cs.getString("FUNCTION_DISABLED") != null) FUNCTION_DISABLED = cs.getString("FUNCTION_DISABLED");
				if(cs.getString("SLOW") != null) SLOW = cs.getString("SLOW");
				if(cs.getString("REDSTONE_POWER") != null) REDSTONE_POWER = cs.getString("REDSTONE_POWER");
				if(cs.getString("JUMP") != null) JUMP = cs.getString("JUMP");
				if(cs.getString("IRON_MAN") != null) IRON_MAN = cs.getString("IRON_MAN");
				if(cs.getString("FORCEFIELD") != null) FORCEFIELD = cs.getString("FORCEFIELD");
				if(cs.getString("KNOCKBACK") != null) KNOCKBACK = cs.getString("KNOCKBACK");
				if(cs.getString("FIREBALLS") != null) FIREBALLS = cs.getString("FIREBALLS");
				if(cs.getString("BERSERK") != null) BERSERK = cs.getString("BERSERK");
				if(cs.getString("TNTBOOM") != null) TNTBOOM = cs.getString("TNTBOOM");
			}
			save(config);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	public void save(FileConfiguration config){
		String section = "Messages.";
		config.set(section + "ALREADY_IN_GAME", ALREADY_IN_GAME.replace(PNC, ""));
		config.set(section + "GAME_FULL", GAME_FULL.replace(PNC, ""));
		config.set(section + "BE_VIP",BE_VIP.replace(PNC, ""));
		config.set(section + "BLUE_LIVES",BLUE_LIVES.replace(PNC, ""));
		config.set(section + "BLUE_WIN",BLUE_WIN.replace(PNC, ""));
		config.set(section + "MONEY_EARN",MONEY_EARN.replace(PNC, ""));
		config.set(section + "HERE_YOU_ARE",HERE_YOU_ARE.replace(PNC, ""));
		config.set(section + "IRON_GOLEM_DIE",IRON_GOLEM_DIE.replace(PNC, ""));
		config.set(section + "KILLED_BY",KILLED_BY.replace(PNC, ""));
		config.set(section + "MONEY_NOW",MONEY_NOW.replace(PNC, ""));
		config.set(section + "NO_COMMANDS_IN_GAME",NO_COMMANDS_IN_GAME.replace(PNC, ""));
		config.set(section + "NOT_ENOUGH_MONEY",NOT_ENOUGH_MONEY.replace(PNC, ""));
		config.set(section + "OTHER_DIE",OTHER_DIE.replace(PNC, ""));
		config.set(section + "PERMISSION_DENIED",PERMISSION_DENIED.replace(PNC, ""));
		config.set(section + "POWERUP",POWERUP.replace(PNC, ""));
		config.set(section + "POWERUP_END",POWERUP_END.replace(PNC, ""));
		config.set(section + "RED_LIVES",RED_LIVES.replace(PNC, ""));
		config.set(section + "RED_WIN",RED_WIN.replace(PNC, ""));
		config.set(section + "TEAM_FULL",TEAM_FULL.replace(PNC, ""));
		config.set(section + "TOO_MANY_PEOPLE",TOO_MANY_PEOPLE.replace(PNC, ""));
		config.set(section + "YOU_JOINED_THE_GAME",YOU_JOINED_THE_GAME.replace(PNC, ""));
		config.set(section + "YOU_TEAM_BLUE",YOU_TEAM_BLUE.replace(PNC, ""));
		config.set(section + "YOU_TEAM_RED",YOU_TEAM_RED.replace(PNC, ""));
		config.set(section + "YOUR_TEAM_BLOCK",YOUR_TEAM_BLOCK.replace(PNC, ""));
		config.set(section + "DIDNT_BOUGHT_YET",DIDNT_BOUGHT_YET.replace(PNC, ""));
		config.set(section + "ALREADY_BOUGHT",ALREADY_BOUGHT.replace(PNC, ""));
		config.set(section + "SUCCEFULLY_BOUGHT",SUCCEFULLY_BOUGHT.replace(PNC, ""));
		config.set(section + "NEED", NEED);
		config.set(section + "OTHER_JOIN_THE_GAME", OTHER_JOIN_THE_GAME.replace(PNC, ""));
		config.set(section + "GAME_START_IN_X_SECONDS", GAME_START_IN_X_SECONDS.replace(PNC, ""));
		config.set(section + "GAME_ALREADY_STARTED",GAME_ALREADY_STARTED.replace(PNC, ""));
		config.set(section + "GAME_NOT_STARTED",GAME_NOT_STARTED.replace(PNC, ""));
		config.set(section + "FUNCTION_DISABLED",FUNCTION_DISABLED.replace(PNC, ""));
		config.set(section + "SLOW", SLOW.replace(PNC, ""));
		config.set(section + "REDSTONE_POWER", REDSTONE_POWER.replace(PNC, ""));
		config.set(section + "JUMP", JUMP.replace(PNC, ""));
		config.set(section + "IRON_MAN", IRON_MAN.replace(PNC, ""));
		config.set(section + "FORCEFIELD", FORCEFIELD.replace(PNC, ""));
		config.set(section + "KNOCKBACK", KNOCKBACK.replace(PNC, ""));
		config.set(section + "FIREBALLS", FIREBALLS.replace(PNC, ""));
		config.set(section + "BERSERK", BERSERK.replace(PNC, ""));
		config.set(section + "TNTBOOM", TNTBOOM.replace(PNC, ""));
		try {
			config.save(battleOfBlocks.getDataFolder() + "/messages.yml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
