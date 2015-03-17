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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import fr.cabricraft.batofb.BattleOfBlocks;
 
public class ToogleInventory {
    @SuppressWarnings("deprecation")
    public static String InventoryToString (Inventory invInventory, Player player)
    {
        invInventory.addItem(player.getInventory().getArmorContents());
        String serialization = invInventory.getSize() + ";";
        for (int i = 0; i < invInventory.getSize(); i++)
        {
            ItemStack is = invInventory.getItem(i);
            if (is != null)
            {
                String serializedItemStack = new String();
             
                String isType = String.valueOf(is.getType().getId());
                serializedItemStack += "t@" + isType;
             
                if (is.getDurability() != 0)
                {
                    String isDurability = String.valueOf(is.getDurability());
                    serializedItemStack += ":d@" + isDurability;
                }

                if (is.getItemMeta() instanceof LeatherArmorMeta)
                {
                	LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
                	int blue = lam.getColor().getBlue();
                	int red = lam.getColor().getRed();
                	int green = lam.getColor().getGreen();
                    serializedItemStack += ":l@" + blue + "%" + green + "%" + red;
                }
                
                if (is.getItemMeta().hasDisplayName())
                {
                	String name = is.getItemMeta().getDisplayName();
                	serializedItemStack += ":n@" + name;
                }
                
                if (is.getAmount() != 1)
                {
                    String isAmount = String.valueOf(is.getAmount());
                    serializedItemStack += ":a@" + isAmount;
                }
             
                Map<Enchantment,Integer> isEnch = is.getEnchantments();
                if (isEnch.size() > 0)
                {
                    for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
                    {
                        serializedItemStack += ":e@" + ench.getKey().getId() + "@" + ench.getValue();
                    }
                }
             
                serialization += i + "#" + serializedItemStack + ";";
            }
        }
        return serialization;
    }
   
    @SuppressWarnings("deprecation")
	public static Inventory StringToInventory(Player p, BattleOfBlocks battleOfBlocks){
        try {
        	String invString = (String) getMetadata(p, "inv", battleOfBlocks);
			String[] serializedBlocks = invString.split(";");
			String invInfo = serializedBlocks[0];
			Inventory deserializedInventory = Bukkit.getServer().createInventory(null, Integer.valueOf(invInfo));
      
			for (int i = 1; i < serializedBlocks.length; i++)
			{
			    String[] serializedBlock = serializedBlocks[i].split("#");
			    int stackPosition = Integer.valueOf(serializedBlock[0]);
			   
			    if (stackPosition >= deserializedInventory.getSize())
			    {
			        continue;
			    }
			   
			    ItemStack is = null;
			    Boolean createdItemStack = false;
			   
			    String[] serializedItemStack = serializedBlock[1].split(":");
			    for (String itemInfo : serializedItemStack)
			    {
			        String[] itemAttribute = itemInfo.split("@");
			        if (itemAttribute[0].equals("t"))
			        {
			            is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
			            createdItemStack = true;
			        }
			        else if (itemAttribute[0].equals("d") && createdItemStack)
			        {
			            is.setDurability(Short.valueOf(itemAttribute[1]));
			        }
			        else if (itemAttribute[0].equals("a") && createdItemStack)
			        {
			            is.setAmount(Integer.valueOf(itemAttribute[1]));
			        }
			        else if (itemAttribute[0].equals("l") && createdItemStack)
			        {
			            String colors[] = itemAttribute[1].split("%");
			            Color c = Color.fromBGR(new Integer(colors[0]), new Integer(colors[1]), new Integer(colors[2]));
			            LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
			            lam.setColor(c);
			            is.setItemMeta(lam);
			        }
			        else if (itemAttribute[0].equals("n") && createdItemStack)
			        {
			        	ItemMeta im = is.getItemMeta();
			        	im.setDisplayName(itemAttribute[1]);
			        	is.setItemMeta(im);
			        }
			        else if (itemAttribute[0].equals("e") && createdItemStack)
			        {
			        	ItemMeta im = is.getItemMeta();
			        	im.addEnchant(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]), true);
			        	is.setItemMeta(im);
			        }
			    }
			    deserializedInventory.setItem(stackPosition, is);
			}
      
			return deserializedInventory;
		} catch (NullPointerException e) {
			Bukkit.getServer().getLogger().info("Error while loading the inventory of " + p.getName());
			return Bukkit.createInventory(p, 9);
		}
    }
    
    public static Object getMetadata(Metadatable object, String key, BattleOfBlocks battleOfBlocks)
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