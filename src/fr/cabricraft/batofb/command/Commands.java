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

package fr.cabricraft.batofb.command;


import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.cabricraft.batofb.BattleOfBlocks;
import fr.cabricraft.batofb.Updater;
import fr.cabricraft.batofb.Updater.UpdateResult;
import fr.cabricraft.batofb.arenas.Arena;
import fr.cabricraft.batofb.signs.SignUtility;

public class Commands implements CommandExecutor {
	
	BattleOfBlocks battleOfBlocks;
	  String PNC = ChatColor.RED + "[BattleOfBlocks] " + ChatColor.RED;
	
	public Commands(BattleOfBlocks battleOfBlocks){
		this.battleOfBlocks = battleOfBlocks;
	}

@SuppressWarnings({ "unused", "static-access" })
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("battleofblocks")){
		    			if(!(battleOfBlocks.hasPermission(sender, "battleofblocks.setup"))){
				    		sender.sendMessage(PNC + "You don't have permission for that !");
							return true;
						}
			    		try {
			    			String test = args[0];
						} catch (Exception e) {
							help(sender, 1);
							return true;
						}
			    		try {
			    			int test = new Integer(args[0]);
			    			if(test < 4){
			    				help(sender, test);
			    				return true;
			    			} else {
			    				sender.sendMessage(ChatColor.RED + "This page does not exist !");
			    				return true;
			    			}
						} catch (Exception e) {}
			    		
				    	if(args[0].equalsIgnoreCase("setstart")){
				    		if(!testPlayer(sender)) return true;
				    		try {
				    			String test = args[1];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		if(args[1].equalsIgnoreCase("blue")){
					    		try {
					    			String test = args[2];
								} catch (Exception e) {
					    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
									return true;
								}
					    		Player p = (Player) sender;
					    		if(battleOfBlocks.Arenaexist(args[2])){
						    		battleOfBlocks.getArena(args[2]).setstartblue(p.getLocation());
					    			sender.sendMessage(PNC + ChatColor.GREEN + "Blue's start point set on your current position !");
					    		} else {
					    			sender.sendMessage(PNC + "This Arena doesn't exist !");
					    		}
								return true;
				    		} else if(args[1].equalsIgnoreCase("red")){
				    			try {
				    			String test = args[2];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		Player p = (Player) sender;
				    		if(battleOfBlocks.Arenaexist(args[2])){
					    		battleOfBlocks.getArena(args[2]).setstartred(p.getLocation());
				    			sender.sendMessage(PNC + ChatColor.GREEN + "Red's start point set on your current position !");
				    		} else {
				    			sender.sendMessage(PNC + "This Arena doesn't exist !");
				    		}
							return true;} else {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
				    		}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("addblock")) {
				    		if(!testPlayer(sender)) return true;
				    		try {
				    			String test = args[1];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		if(args[1].equalsIgnoreCase("blue")){
					    		try {
					    			String test = args[2];
								} catch (Exception e) {
					    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
									return true;
								}
					    		Player p = (Player) sender;
					    		if(battleOfBlocks.Arenaexist(args[2])){
					    			battleOfBlocks.getArena(args[2]).setdetection(1);
					    			battleOfBlocks.getArena(args[2]).settemp((Player) sender);
					    			sender.sendMessage(PNC + ChatColor.GREEN + "Place the bblock !");
					    		} else {
					    			sender.sendMessage(PNC + "This Arena doesn't exist !");
					    		}

								return true;
				    		} else if(args[1].equalsIgnoreCase("red")){
					    		try {
					    			String test = args[2];
								} catch (Exception e) {
					    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
									return true;
								}
					    		Player p = (Player) sender;
					    		if(battleOfBlocks.Arenaexist(args[2])){
					    			battleOfBlocks.getArena(args[2]).setdetection(2);
					    			battleOfBlocks.getArena(args[2]).settemp((Player) sender);
					    			sender.sendMessage(PNC + ChatColor.GREEN + "Place the bblock !");
					    		} else {
					    			sender.sendMessage(PNC + "This Arena doesn't exist !");
					    		}

								return true;
				    		} else {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
				    		}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("deleteblock")) {
				    		if(!testPlayer(sender)) return true;
				    		try {
				    			String test = args[1];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		if(args[1].equalsIgnoreCase("blue")){
					    		try {
					    			String test = args[2];
								} catch (Exception e) {
					    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
									return true;
								}
					    		if(battleOfBlocks.Arenaexist(args[2])){
					    			battleOfBlocks.getArena(args[2]).setdetection(3);
					    			battleOfBlocks.getArena(args[2]).settemp((Player) sender);
					    			sender.sendMessage(PNC + ChatColor.GREEN + "Delete the bblock !");
					    		} else {
					    			sender.sendMessage(PNC + "This Arena doesn't exist !");
					    		}
								return true;
				    		} else if(args[1].equalsIgnoreCase("red")){
					    		try {
					    			String test = args[2];
								} catch (Exception e) {
					    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
									return true;
								}
					    		if(battleOfBlocks.Arenaexist(args[2])){
					    			battleOfBlocks.getArena(args[2]).setdetection(4);
					    			battleOfBlocks.getArena(args[2]).settemp((Player) sender);
					    			sender.sendMessage(PNC + ChatColor.GREEN + "Delete the bblock !");
					    		} else {
					    			sender.sendMessage(PNC + "This Arena doesn't exist !");
					    		}
								return true;
				    		} else {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
				    		}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("setend")) {
				    		if(!testPlayer(sender)) return true;
				    		try {
				    			String test = args[1];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		if(battleOfBlocks.Arenaexist(args[1])){
					    		Player p = (Player) sender;
					    		battleOfBlocks.getArena(args[1]).setend(p.getLocation());
				    			sender.sendMessage(PNC + ChatColor.GREEN + "Ending point set to your current location !");
				    		} else {
				    			sender.sendMessage(PNC + "This Arena doesn't exist !");
				    		}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("setwaitroom")) {
				    		if(!testPlayer(sender)) return true;
				    		try {
				    			String test = args[1];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		if(battleOfBlocks.Arenaexist(args[1])){
				    			Player p = (Player) sender;
				    			battleOfBlocks.getArena(args[1]).setwaitroom(p.getLocation());
				    			sender.sendMessage(PNC + ChatColor.GREEN + "Wait point set to your current location !");
			    			} else {
				    			sender.sendMessage(PNC + "This Arena doesn't exist !");
				    		}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("addarena")){
				    		try {
				    			String test = args[1];
				    			if(test.length() > 15){
					    			sender.sendMessage(PNC + "The length of the arena name musn't be more than 15 caracters !");
									return true;
					    		}
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		if(battleOfBlocks.Arenaexist(args[1])){
				    			sender.sendMessage(PNC + "This arena already exists !");
				    			return true;
				    		} else {
					    		battleOfBlocks.addarena(args[1]);
				    			sender.sendMessage(PNC + ChatColor.GREEN + "Arena added !");
			    			}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("setlives")){
				    		int param;
				    		try {
				    			String test = args[1];
				    			param = new Integer(args[1]);
				    			if(!test_more(param, 1)){
				    				sender.sendMessage(PNC + "You cannot set a number under 1 !");
				    				return true;
				    			}
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		try {
				    			String test = args[2];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		if(battleOfBlocks.Arenaexist(args[2])){
				    			battleOfBlocks.getArena(args[2]).life = param;
				    			battleOfBlocks.getArena(args[2]).bluelife = param;
				    			battleOfBlocks.getArena(args[2]).redlife = param;
				    			sender.sendMessage(PNC + ChatColor.GREEN + param + " lives set to the arena " + args[2] + "!");
				    		} else {
				    			sender.sendMessage(PNC + "This Arena doesn't exist !");
				    		}
							
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("setmaxplayers")){
				    		int param;
				    		try {
				    			String test = args[1];
				    			param = new Integer(args[1]);
				    			if(!test_more(param, 2)){
				    				sender.sendMessage(PNC + "You cannot set a number under 2 !");
				    				return true;
				    			}
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		try {
				    			String test = args[2];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		if(battleOfBlocks.Arenaexist(args[2])){
				    			battleOfBlocks.getArena(args[2]).pmax = param;
				    			sender.sendMessage(PNC + ChatColor.GREEN + "Maximal players number set !");
				    			SignUtility.updatesigns();
				    		} else {
				    			sender.sendMessage(PNC + "This Arena doesn't exist !");
				    		}
							
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("setstartmin")){
				    		int param;
				    		try {
				    			String test = args[1];
				    			param = new Integer(args[1]);
				    			if(!test_more(param, 2)){
				    				sender.sendMessage(PNC + "You cannot set a number under 2 !");
				    				return true;
				    			}
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		try {
				    			String test = args[2];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		if(battleOfBlocks.Arenaexist(args[2])){
				    			if(battleOfBlocks.getArena(args[2]).pmax < param){
				    				sender.sendMessage(PNC + ChatColor.RED + "The minimum players number cannot be bigger than the player max limit !");
				    				return true;
				    			}
				    			battleOfBlocks.getArena(args[2]).startmin = param;
				    			sender.sendMessage(PNC + ChatColor.GREEN + "Minimum players number set !");
				    			SignUtility.updatesigns();
				    		} else {
				    			sender.sendMessage(PNC + "This Arena doesn't exist !");
				    		}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("addreward")){
				    		if(!testPlayer(sender)) return true;
				    		try {
				    			String test = args[1];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		Player p = (Player) sender;
				    		ItemStack is = p.getItemInHand();
				    		
				    		if(battleOfBlocks.Arenaexist(args[1])){
				    			battleOfBlocks.getArena(args[1]).reward.add(is);
				    			sender.sendMessage(PNC + ChatColor.GREEN + "Added a reward of " + is.getAmount() + " " + is.getType() + " !");
				    		} else {
				    			sender.sendMessage(PNC + "This Arena doesn't exist !");
				    		}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("resetrewards")){
				    		int param;
				    		try {
				    			String test = args[1];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		Player p = (Player) sender;
		
				    		if(battleOfBlocks.Arenaexist(args[1])){
				    			battleOfBlocks.getArena(args[1]).reward.clear();
				    			sender.sendMessage(PNC + ChatColor.GREEN + "Reset rewards of this arena !");
				    		} else {
				    			sender.sendMessage(PNC + "This Arena doesn't exist !");
				    		}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("settime")){
				    		int param;
				    		try {
				    			String test = args[1];
				    			param = new Integer(args[1]);
				    			if(!test_more(param, 5)){
				    				sender.sendMessage(PNC + "You cannot set a number under 5 !");
				    				return true;
				    			}
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    			battleOfBlocks.time = param;
				    			sender.sendMessage(PNC + ChatColor.GREEN + "Time before start set !");
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("setcoinskills")){
				    		int param;
				    		try {
				    			String test = args[1];
				    			param = new Integer(args[1]);
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    			battleOfBlocks.facteurkills = param;
				    			sender.sendMessage(PNC + ChatColor.GREEN + "Kills reward set !");
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("setonwin")){
				    		int param;
				    		try {
				    			String test = args[1];
				    			param = new Integer(args[1]);
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    			battleOfBlocks.onwin = param;
				    			sender.sendMessage(PNC + ChatColor.GREEN + "Win rewards set !");
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("tooglebreak")){
				    		try {
				    			String test = args[1];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		if(battleOfBlocks.Arenaexist(args[1])){
				    			Arena ar = battleOfBlocks.getArena(args[1]);
				    			if(ar.canbuild){
					    			sender.sendMessage(PNC + ChatColor.GREEN + "Toogled break off for the arena " + args[1] + " !");
					    			ar.canbuild = false;
				    			} else {
				    				sender.sendMessage(PNC + ChatColor.GREEN + "Toogled break on for the arena " + args[1] + " !");
					    			ar.canbuild = true;
				    			}
				    		} else {
				    			sender.sendMessage(PNC + "This Arena doesn't exist !");
				    		}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("setvip")){
				    		int param;
				    		try {
				    			String test = args[1];
				    			param = new Integer(args[1]);
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		try {
				    			String test = args[2];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		
				    		if(battleOfBlocks.Arenaexist(args[2])){
				    			battleOfBlocks.getArena(args[2]).vip = param;
				    			if(param != 0) sender.sendMessage(PNC + ChatColor.GREEN + "Number when the VIP will aprear on the signs set !");
				    			else sender.sendMessage(PNC + ChatColor.GREEN + "Vip desactivated !");
				    			SignUtility.updatesigns();
				    		} else {
				    			sender.sendMessage(PNC + "This Arena doesn't exist !");
				    		}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("setcontrolname")){
				    		String param;
				    		try {
				    			String test = args[1];
				    			param = args[1];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		if(param.length() > 15){
				    			sender.sendMessage(PNC + "The length of the controlname musn't be more than 15 caracters !");
								return true;
				    		}
			    			battleOfBlocks.controlname = param;
			    			battleOfBlocks.msg.reloadPNC();
			    			battleOfBlocks.msg.load();
			    			sender.sendMessage(PNC + ChatColor.GREEN + "Control name set !");
			    			SignUtility.updatesigns();
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("deletearena")){
				    		try {
				    			String test = args[1];
							} catch (Exception e) {
				    			sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
								return true;
							}
				    		if(battleOfBlocks.Arenaexist(args[1])){
					    		battleOfBlocks.removearena(args[1]);
				    			sender.sendMessage(PNC + ChatColor.GREEN + "Arena deleted !");
			    			} else {
				    			sender.sendMessage(PNC + "This Arena doesn't exist !");
				    		}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("arenalist")){
				    		sender.sendMessage(PNC + ChatColor.BLUE + "Arenas Status:");
				    		for(Arena ar : battleOfBlocks.arenas){
				    			sender.sendMessage(PNC + "  -" + ChatColor.GOLD + ChatColor.UNDERLINE + ar.getName() + ":");
				    			sender.sendMessage(PNC + "     " + ChatColor.DARK_GREEN + ChatColor.BOLD + "Status:" + ChatColor.RESET + ar.getStatus());
				    			sender.sendMessage(PNC + "     " + ChatColor.DARK_GREEN + ChatColor.BOLD + "Game Status:" + ChatColor.RESET + ChatColor.GREEN + "IsWaiting:" + ar.iswaiting + ChatColor.RESET + "/" + ChatColor.GREEN + "IsStarted:" + ar.isstarted);
				    			sender.sendMessage(PNC + "     " + ChatColor.DARK_GREEN + ChatColor.BOLD + "Connected:" + ChatColor.RESET + ChatColor.GREEN + ar.getConnectedPlayers() + "/" + ar.pmax);
				    			sender.sendMessage(PNC + "     " + ChatColor.DARK_GREEN + ChatColor.BOLD + "Lifes:" + ChatColor.RESET + ChatColor.BLUE + ar.bluelife + ChatColor.RESET + "/" + ChatColor.RED + ar.redlife);
				    		}
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("saveall")){
				    		sender.sendMessage(PNC + ChatColor.GREEN + "Saving... !");
				    		try {
								battleOfBlocks.reloadandremake();
							} catch (IOException e) {
								e.printStackTrace();
							}
				    		sender.sendMessage(PNC + ChatColor.GREEN + "Configuration saved !");
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("reload")){
				    		sender.sendMessage(PNC + ChatColor.GREEN + "Reloading... !");
				        	battleOfBlocks.reload(sender);
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("info")){
				    		sender.sendMessage(PNC + ChatColor.YELLOW + "Plugin by gpotter2 ! Version: " + battleOfBlocks.getDescription().getVersion());
				    		return true;
				    	} else if(args[0].equalsIgnoreCase("update")){
				    		battleOfBlocks.getLogger().info("Downloading the new version of BattleOfBlocks !");
				    		Updater up = new Updater(battleOfBlocks, battleOfBlocks.update_id, battleOfBlocks.batofb_file, Updater.UpdateType.DEFAULT, false);
					        if(up.getResult() == UpdateResult.SUCCESS){
					        	sender.sendMessage(PNC + ChatColor.GREEN + "Plugin sucessfuly downloaded ! Reloading the plugin...");
					        	battleOfBlocks.reload(sender);
					        } else if(up.getResult() == UpdateResult.NO_UPDATE){
					        	sender.sendMessage(PNC + ChatColor.GREEN + "The plugin already is up to date !");
					        } else {
					        	sender.sendMessage(PNC + ChatColor.RED + "Plugin couldn't be downloaded !");
					        }
				    		return true;
				    	} else {
				    		sender.sendMessage(PNC + "Syntax error, type " + ChatColor.GREEN + "/battleofblocks" + ChatColor.RED + " for help !");
				    		return true;
				    	}
    } else if(cmd.getName().equalsIgnoreCase("batofb")){
			  if(sender instanceof Player){
				  if(battleOfBlocks.hasPermission(sender, "battleofblocks.play") || battleOfBlocks.playbydefault){
					  	if(args.length == 0){
							sender.sendMessage(PNC + "Wrong usage ! Type /batofb help");
							return true;
					  	}
							if(args[0].equalsIgnoreCase("leave")){
							int deconowright = 0;
							 for(Arena ar : battleOfBlocks.arenas){
								 if(ar.isinGame((Player) sender)){
									 Player p = (Player) sender;
									 ar.disconnect(p);
									 p.teleport(ar.locend);
									 sender.sendMessage(PNC + ChatColor.RED + "You left the game !");
									 deconowright++;
									 break;
								 }
							 }
							 if(deconowright == 0){
								 sender.sendMessage(PNC + ChatColor.RED +  "You are not in any Arena !");
							 }
							} else if(args[0].equalsIgnoreCase("join")){
								if(battleOfBlocks.canjoinwhithcommand){
						    		if(args.length >= 2){
						    			String name = args[1];
						    			if(battleOfBlocks.Arenaexist(name)){
						    				Arena ar = battleOfBlocks.getArena(name);
						    				if(ar.locend == null){
									    		sender.sendMessage(ChatColor.RED + "[BattleOfBlocks] Cannot join the game : ending point not set !");
									    	} else if(ar.locstartred == null){ 
									    		sender.sendMessage(ChatColor.RED + "[BattleOfBlocks] Cannot join the game : red's starting point not set !");
									    	} else if(ar.locstartblue == null){
									    		sender.sendMessage(ChatColor.RED + "[BattleOfBlocks] Cannot join the game : blue's starting point not set !");
									    	} else if(ar.waitroom == null){
									    		sender.sendMessage(ChatColor.RED + "[BattleOfBlocks] Cannot join the game : waiting point not set !");
									    	} else if(ar.vblue.isEmpty() || ar.vred.isEmpty()){
									    		sender.sendMessage(ChatColor.RED + "[BattleOfBlocks] Cannot join the game : must be at the minimum 1 block for each team !");
									    	} else if(ar.pmax < ar.startmin) { 
									    		sender.sendMessage(ChatColor.RED + "[BattleOfBlocks] Cannot join the game : players max number is less than players minimum start !");
									    	} else if(ar.isstarted) { 
									    		sender.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.GAME_ALREADY_STARTED));
									    	} else if(ar.getConnectedPlayers() >= ar.pmax){
									    		sender.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.GAME_FULL));
									    	} else if(((ar.vip + ar.getConnectedPlayers()) >= ar.pmax) && !(battleOfBlocks.hasPermission(sender, "battleofblocks.vip"))){
									    		sender.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.BE_VIP));
									    	} else {
									    		battleOfBlocks.getArena(name).join((Player) sender);
									    	}
						    			} else {
						    				sender.sendMessage(PNC + "The arena '" + name + "' doesn't exist !");
							    			return true;
						    			}
						    		} else {
						    			sender.sendMessage(PNC + "Wrong usage ! Type /batofb help");
						    			return true;
						    		}
								} else {
					    			sender.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.FUNCTION_DISABLED));
					    			return true;
					    		}
							} else if(args[0].equalsIgnoreCase("help")){
								sender.sendMessage(PNC + ChatColor.GREEN + "Usages:");
								sender.sendMessage(ChatColor.GREEN + "/batofb leave ==> (better to use /leave) Leave the arena");
								if(battleOfBlocks.canjoinwhithcommand) sender.sendMessage(ChatColor.GREEN + "/batofb join <arena> ==> Join an arena");
								return true;
							} else if(args[0].equalsIgnoreCase("admin")){
								if(battleOfBlocks.cheatadmin == false){
									sender.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.FUNCTION_DISABLED));
									return true;
								}
								 if(battleOfBlocks.hasPermission(sender, "battleofblocks.admin")){
									 int deconowright = 0;
									 for(Arena ar : battleOfBlocks.arenas){
										 if(ar.isinGame((Player) sender)){
											 Player p = (Player) sender;
											 p.sendMessage("That is cheat, but funny :D");
											 ar.addmoney(p);
											 ar.addmoney(p);
											 ar.addmoney(p);
											 ar.addmoney(p);
											 ar.addmoney(p);
											 deconowright++;
											 break;
										 }
									 }
									 if(deconowright == 0){
										 sender.sendMessage(PNC + ChatColor.RED +  "You are not in any Arena !");
									 }
								 } else {
									 sender.sendMessage(PNC + "Wrong usage ! Type /batofb help");
									 return true;
								 }
							} else {
								sender.sendMessage(PNC + "Wrong usage ! Type /batofb help");
								return true;
							}
					  } else {
						  sender.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.PERMISSION_DENIED));
					  }
			   } else {
				   sender.sendMessage(PNC + "You must be a player !");
			   }
		  return true;
	  }
		    return false;
	  }

	public boolean test_more(int number, int more){
		return (number >= more);
	}
	
	public boolean testPlayer(CommandSender sender){
		if(sender instanceof Player){
			return true;
		} else {
			sender.sendMessage(PNC + "You must be a player !");
			return false;
		}
	}

	public void help(CommandSender sender, int page){
		if(!(battleOfBlocks.hasPermission(sender, "battleofblocks.setup"))){
			return;
		}
		sender.sendMessage(ChatColor.GOLD + "BATTLEOFBLOCKS HELP");
		sender.sendMessage(ChatColor.GREEN +  "" + ChatColor.MAGIC + "batb" + ChatColor.GOLD + "   Plugin by gpotter2 !   " + ChatColor.GREEN +  "" + ChatColor.MAGIC + "batb");
		if(page == 1){
			sender.sendMessage("Page 1");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks arenalist   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " List the arenas");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks addarena <arena>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Create an arena");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks addblock <blue/red> <arena>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Add a block to an arena");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks deleteblock <blue/red> <arena>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Delete a block from an arena");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks setstart <blue/red> <arena>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Set the start of a team in an arena");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks setend <arena>   " + ChatColor.RED + " --> + " + ChatColor.LIGHT_PURPLE + " Set the end of all players from an arena");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks setwaitroom <arena>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Set the waitroom of all players from an arena");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks deletearena <arena>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Delete an arena");
			sender.sendMessage(ChatColor.GOLD + "Next page : /battleofblocks 2");
		} else if(page == 2){
			sender.sendMessage("Page 2");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks setstartmin <number> <arena>  " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Set the minplayers on an arena");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks setmaxplayers <number> <arena>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Set the maxplayers on an arena");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks setlives <number> <arena>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Set the lives on an arena");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks setonwin <number>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Set the server coins reward of any win");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks setcoinskills <number>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Set the server coins reward of any kills");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks addreward <arena>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Add the Stack that you have in hands to the rewards on winning");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks resetrewards <arena>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Reset all the rewards of this arena");
			sender.sendMessage(ChatColor.GOLD + "Next page : /battleofblocks 3");
		} else if(page == 3) {
			sender.sendMessage("Page 3");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks setvip <number> <arena>  " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Set before the start when the VIP will appear on the signs");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks tooglebreak <arena>  " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Enable or disable breaking in an arena (auto reset on finish)");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks setcontrolname <name>  " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Set the name of the instance");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks setcoinskills <number>   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Set the server coins reward of any kills");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks info   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Get the plugin infos !");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks saveall   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Save all the arenas (The arenas are saved automatically during the server's stop)");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks reload   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Reload completly the plugin !");
			sender.sendMessage(ChatColor.BLUE + "[#]" + ChatColor.GREEN + "/battleofblocks update   " + ChatColor.RED + " --> " + ChatColor.LIGHT_PURPLE + " Update the plugin !");
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void clearInventory(Player p){
		p.getInventory().clear();
		p.getInventory().setChestplate(null);
		p.updateInventory();
	}

}