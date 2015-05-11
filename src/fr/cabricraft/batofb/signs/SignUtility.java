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

package fr.cabricraft.batofb.signs;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.cabricraft.batofb.BattleOfBlocks;
import fr.cabricraft.batofb.arenas.Arena;

public class SignUtility implements Listener {
	
	static BattleOfBlocks battleOfBlocks;

	@SuppressWarnings("static-access")
	public SignUtility(BattleOfBlocks battleOfBlocks){
		this.battleOfBlocks = battleOfBlocks;
	}
	  
	public static void updatesigns(){
		  List<Location> s_to_remove = new LinkedList<Location>();
	      for(Location ls : battleOfBlocks.s){
	    	  if(!ls.getBlock().getChunk().isLoaded()){
	    		  ls.getBlock().getChunk().load();
	    	  }
	    	  if(ls.getBlock().getType() == Material.WALL_SIGN || ls.getBlock().getType() == Material.SIGN || ls.getBlock().getType() == Material.SIGN_POST) {
	    		  Sign s = (Sign) ls.getBlock().getState();
	    		  String arena_name = s.getLine(3);
	    		  if(battleOfBlocks.Arenaexist(arena_name)){
					  Arena ar = battleOfBlocks.getArena(s.getLine(3));
					  if(battleOfBlocks.controlname == null) battleOfBlocks.controlname = "BattleOfBlocks";
				      s.setLine(0, ChatColor.BLUE + battleOfBlocks.controlname);
				      s.setLine(2, ar.getConnectedPlayers() + "/" + ar.pmax);
				      if(ar.getConnectedPlayers() >= ar.pmax) {
				    	  s.setLine(1, ChatColor.BLUE + "[Full]");
				      } else if(ar.isstarted == false && (ar.vip + ar.getConnectedPlayers()) >= ar.pmax){
						  s.setLine(1, ChatColor.LIGHT_PURPLE + "[VIP]");
					  } else if(ar.isstarted) {
						  s.setLine(1, ChatColor.RED + "[Busy]");
					  } else {
						  s.setLine(1, ChatColor.GREEN + "[Join]");
					  }
					  s.update();
	    		  }
	    	  } else {
	    		  s_to_remove.add(ls);
	    	  }
	      }
	      for(Location loc : s_to_remove){
	    	  battleOfBlocks.s.remove(loc);
	      }
	}
	
	  @EventHandler
	  public void onSignChange(SignChangeEvent event) {
		    String[] lines = event.getLines();
		    if (lines[0].equalsIgnoreCase("[batofb]")) {
		    	String name = lines[1];
		    	if(battleOfBlocks.Arenaexist(name)){
					event.getPlayer().sendMessage(ChatColor.GOLD + "[BattleOfBlocks] You created a BSign on arena " + name + " !");
					if(battleOfBlocks.controlname == null) battleOfBlocks.controlname = "BattleOfBlocks";
				    event.setLine(0, ChatColor.BLUE + battleOfBlocks.controlname);
				    event.setLine(3, battleOfBlocks.getArena(name).getName());
				    Arena ar = battleOfBlocks.getArena(name);
				    event.setLine(2, battleOfBlocks.getArena(name).getConnectedPlayers() + "/" + battleOfBlocks.getArena(name).pmax);
				    if(ar.isstarted == false && (ar.vip + ar.getConnectedPlayers()) >= ar.pmax){
						  event.setLine(1, ChatColor.LIGHT_PURPLE + "[VIP]");
					  } else if(ar.getConnectedPlayers() >= ar.pmax) {
						  event.setLine(1, ChatColor.BLUE + "[Full]");
					  } else if(ar.isstarted == false){
						  event.setLine(1, ChatColor.GREEN + "[Join]");
					  } else if(ar.isstarted) {
						  event.setLine(1, ChatColor.RED + "[Busy]");
					  }
					battleOfBlocks.s.add(event.getBlock().getLocation());
		    	} else {
					event.getPlayer().sendMessage(ChatColor.RED + "[BattleOfBlocks] This arena doesn't exist !");
					event.setLine(1, ChatColor.RED + "ArenaName");
		    	}
		    } else if (lines[0].equalsIgnoreCase("[batofb-shop]")) {
		    	if(battleOfBlocks.controlname == null) battleOfBlocks.controlname = "BattleOfBlocks";
		    	event.setLine(0, ChatColor.BLUE + battleOfBlocks.controlname);
		    	event.setLine(1, ChatColor.DARK_AQUA + "[Shop]");
		    } else if(lines[0].equalsIgnoreCase("[batofb-dis]")){
		    	org.bukkit.material.Sign s = (org.bukkit.material.Sign) event.getBlock().getState().getData();
		    	Block attachedBlock = event.getBlock().getRelative(s.getAttachedFace());
		    	if(attachedBlock.getType() == Material.DISPENSER || attachedBlock.getType() == Material.DROPPER){
		    		event.setLine(0, ChatColor.BLUE + "[BatOfBDis]");
		    	} else {
		    		event.setLine(0, ChatColor.BLUE + "ERROR");
		    		event.setLine(1, ChatColor.RED + "Must be on");
		    		event.setLine(2, ChatColor.RED + "a dispenser");
		    		event.setLine(3, ChatColor.RED + "or a dropper !");
		    	}
		    }
	  }
	  
	  public void checkIfExist(Block b){
		  if(!battleOfBlocks.s.contains(b.getLocation())){
			  battleOfBlocks.s.add(b.getLocation());
		  }
	  }
	  
		  @EventHandler
		  public void onPlayerInteract(PlayerInteractEvent event) {	
			    if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
			    	Block b = event.getClickedBlock();
			    	if(b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN){
			    		Sign s = (Sign) b.getState();
			    		String[] lines = s.getLines();
			    		if(battleOfBlocks.controlname == null) battleOfBlocks.controlname = "BattleOfBlocks";
			    		if(lines[0].equalsIgnoreCase(ChatColor.BLUE + battleOfBlocks.controlname)) {
			    			if(lines[1].equalsIgnoreCase(ChatColor.DARK_AQUA + "[Shop]")){
			    				if(battleOfBlocks.vaultenabled_economy){
			    					battleOfBlocks.shop.startShop(event.getPlayer());
			    					return;
			    				} else {
			    					event.getPlayer().sendMessage(ChatColor.RED + "[BattleOfBlocks] Vault and an economy plugin must be installed !");
			    				}
			    			} else {
			    				checkIfExist(event.getClickedBlock());
				    			if(battleOfBlocks.Arenaexist(lines[3])) {
					    			String name = s.getLine(3);
					    			Arena ar = battleOfBlocks.getArena(name);
							    	if(ar.locend == null){
							    		event.getPlayer().sendMessage(ChatColor.RED + "[BattleOfBlocks] Cannot join the game : ending point not set !");
							    	} else if(ar.locstartred == null){ 
							    		event.getPlayer().sendMessage(ChatColor.RED + "[BattleOfBlocks] Cannot join the game : red's starting point not set !");
							    	} else if(ar.locstartblue == null){
							    		event.getPlayer().sendMessage(ChatColor.RED + "[BattleOfBlocks] Cannot join the game : blue's starting point not set !");
							    	} else if(ar.waitroom == null){
							    		event.getPlayer().sendMessage(ChatColor.RED + "[BattleOfBlocks] Cannot join the game : waiting point not set !");
							    	} else if(ar.vblue.isEmpty() || ar.vred.isEmpty()){
							    		event.getPlayer().sendMessage(ChatColor.RED + "[BattleOfBlocks] Cannot join the game : must be at the minimum 1 block for each team !");
							    	} else if(ar.pmax < ar.startmin) { 
							    		event.getPlayer().sendMessage(ChatColor.RED + "[BattleOfBlocks] Cannot join the game : players max number is less than players minimum start !");
							    	} else if(ar.isstarted) { 
							    		event.getPlayer().sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.GAME_ALREADY_STARTED));
							    	} else if(ar.getConnectedPlayers() >= ar.pmax){
							    		event.getPlayer().sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.GAME_FULL));
							    	} else if(((ar.vip + ar.getConnectedPlayers()) >= ar.pmax) && !(battleOfBlocks.hasPermission(event.getPlayer(), "battleofblocks.vip"))){
							    		event.getPlayer().sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.BE_VIP));
							    	} else {
							    		battleOfBlocks.getArena(name).join(event.getPlayer());
							    	}
				    			} else {
				    				event.getPlayer().sendMessage("This panel doesn't work !");
				    			}
			    			}
			    		}
			    	}
			    }
		  }
}
