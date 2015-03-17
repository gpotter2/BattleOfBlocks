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

package fr.cabricraft.batofb.powerups;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import fr.cabricraft.batofb.BattleOfBlocks;

public class LoadCustomAdd {

	BattleOfBlocks battleOfBlocks;
	
	public LoadCustomAdd(BattleOfBlocks battleOfBlocks){
		this.battleOfBlocks = battleOfBlocks;
	}
	
	@SuppressWarnings("deprecation")
	public void load(FileConfiguration config){
		try {
			List<Material> l = new LinkedList<Material>();
			l.add(Material.REDSTONE);
			l.add(Material.GOLD_NUGGET);
			l.add(Material.LEATHER_BOOTS);
			l.add(Material.BEDROCK);
			l.add(Material.IRON_BLOCK);
			l.add(Material.STICK);
			l.add(Material.BRICK);
			l.add(Material.FIREBALL);
			l.add(Material.SNOW_BALL);
			l.add(Material.TNT);
			if(config.getConfigurationSection("Perks") != null){
			      Set<String> keys = config.getConfigurationSection("Perks").getKeys(false);
			      for (String key : keys)
			      {
			        ConfigurationSection cs = config.getConfigurationSection("Perks." + key);
			        int price = 0;
			        int pricetobuy = 0;
			        String command = null;
			        String perm = null;
			        String description = null;
			        Material m = null;
			        price = cs.getInt("Price");
			        command = cs.getString("Command");
			        description = cs.getString("Description");
			        m = Material.getMaterial(cs.getInt("MaterialID"));
			        perm = cs.getString("PermissionToBuy");
			        pricetobuy = cs.getInt("PriceToBuy");
				    if(price != 0 && command != null && description != null && m != null && perm != null) {
				        if(!(l.contains(m))){
					    	CustomAddPower cap = new CustomAddPower(m,command, price, description, key, perm, pricetobuy);
						    battleOfBlocks.ca.addCustomPower(cap);
				        } else {
				        	battleOfBlocks.getLogger().severe("Materials of Powerups in the shop.yml, mustn't be already used by the inclueds perks !");
				        }
				    } else {
				    	battleOfBlocks.getLogger().severe("Could not load the powerup : " + key);
				    }
			      }
			      battleOfBlocks.getLogger().info("Shop loaded !");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
