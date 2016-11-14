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

package fr.cabricraft.batofb.kits;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import fr.cabricraft.batofb.BattleOfBlocks;
import fr.cabricraft.batofb.arenas.Arena;
import fr.cabricraft.batofb.powerups.Powerups.PowerKit;

public class BasicKits {
	
	Arena arena;
	
	public BasicKits(Arena arena){
		this.arena = arena;
	}
	
	private ItemStack getIS(Material m, String name, String sousname, Player p){
		return getIS(new ItemStack(m, 1), name, sousname, p);
	}
	
	private ItemStack getIS(ItemStack i, String name, String sousname, Player p){
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + name);
		List<String> l = new LinkedList<String>();
		l.add(ChatColor.GOLD + sousname);
		im.setLore(l);
		i.setItemMeta(im);
		return i;
	}
	
	public boolean hasBoughtKit(Player p, String name){
		return p.hasPermission("battleofblocks.hasbuy.kit." + name);
	}
	
	private void setRandomClass(Player p){
		List<PowerKit> l = new LinkedList<PowerKit>();
		l.add(PowerKit.MAGE);
		if(hasBoughtKit(p, "Spy")) l.add(PowerKit.SPY);
		if(hasBoughtKit(p, "Phoenix")) l.add(PowerKit.PHOENIX);
		if(hasBoughtKit(p, "Shamen")) l.add(PowerKit.SHAMEN);
		if(hasBoughtKit(p, "Healer")) l.add(PowerKit.HEALER);
		if(hasBoughtKit(p, "Troll")) l.add(PowerKit.TROLL);
		if(hasBoughtKit(p, "Fury")) l.add(PowerKit.FURY);
		PowerKit kit;
		if(l.size() == 1){
			kit = l.get(0);
		} else {
			int nombreAleatoire = (int)(Math.random() * (l.size()));
			kit = l.get(nombreAleatoire);
		}
		arena.player_list_powerkit.put(p.getUniqueId(), kit);
	}
	
	@SuppressWarnings("deprecation")
	public List<ItemStack> getItemsPlayer(Player p){
		if(arena.getKitName(p) == PowerKit.RANDOM){
			setRandomClass(p);
		}
		List<ItemStack> list = new LinkedList<ItemStack>();
		PowerKit kp = arena.getKitName(p);
		ItemStack d_is = getIS(Material.WOOD_SWORD, "SWORD", "Funny no ?", p);
		d_is.addEnchantment(Enchantment.DURABILITY, 3);
		list.add(d_is);
	    if(kp == PowerKit.MAGE){
	    	list.add(getIS(Material.BONE, "Flash !", "Flash players !", p));
	    } else if(kp == PowerKit.SPY){
	    	list.add(getIS(Material.INK_SACK, "Invisibility !", "Pouf !", p));
	    } else if(kp == PowerKit.PHOENIX){
	    	list.add(getIS(Material.BLAZE_ROD, "Inferno !", "Burn !!", p));
	    } else if(kp == PowerKit.SHAMEN){
	    	list.add(getIS(Material.VINE, "Nature protection !", "Create a wall !", p));
	    } else if(kp == PowerKit.HEALER){
	    	list.add(getIS(Material.STICK, "Healer !", "Heal people !", p));
	    } else if(kp == PowerKit.TROLL){
	    	list.add(getIS(new ItemStack(Material.INK_SACK, 1, (short)0, DyeColor.RED.getData()), "Team color !", "Change your team color !", p));
	    } else if(kp == PowerKit.FURY){
	    	list.add(getIS(Material.TNT, "Fury !", "Punsh players !", p));
	    }
	    return list;
	}
	
	public void setMetadata(Metadatable object, String key, Object value, BattleOfBlocks battleOfBlocks)
	  {
	    object.setMetadata(key, new FixedMetadataValue(battleOfBlocks, value));
	  }
	  
	  public Object getMetadata(Metadatable object, String key, BattleOfBlocks battleOfBlocks)
	  {
	    List<MetadataValue> values = object.getMetadata(key);
	    for (MetadataValue value : values) {
	      if (value.getOwningPlugin() == battleOfBlocks) {
	        return value.value();
	      }
	    }
	    return null;
	  }
}
