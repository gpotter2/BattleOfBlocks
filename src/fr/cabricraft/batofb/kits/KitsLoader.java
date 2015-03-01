package fr.cabricraft.batofb.kits;

import java.util.Set;
import java.util.Vector;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.cabricraft.batofb.BattleOfBlocks;

public class KitsLoader {
	BattleOfBlocks battleOfBlocks;
	
	public KitsLoader(BattleOfBlocks battleOfBlocks){
		this.battleOfBlocks = battleOfBlocks;
	}
	
	@SuppressWarnings("deprecation")
	public Kits load(FileConfiguration config){
		try {
			if(config.getConfigurationSection("Kits") != null){
		      Set<String> keys = config.getConfigurationSection("Kits").getKeys(false);
		      Vector<Kit> v_kits = new Vector<Kit>();
		      for (String key : keys) {
			        ConfigurationSection cs1 = config.getConfigurationSection("Kits." + key);
			        String kit_name = key;
			        String kit_des = cs1.getString("Description");
			        String kit_perm = "battleofblocks.play";
			        if(cs1.getString("PermissionToBuy") != null) kit_perm = cs1.getString("PermissionToBuy");
			        int priceToBuy = 0;
			        priceToBuy = cs1.getInt("PriceToBuy");
			        Material logo = Material.getMaterial(cs1.getInt("LogoID"));
			        Set<String> keys2 = config.getConfigurationSection("Kits." + key + ".Items").getKeys(false);
			        Vector<ItemsKit> v = new Vector<ItemsKit>();
				    for (String key2 : keys2) {
					        ConfigurationSection cs = config.getConfigurationSection("Kits." + key + ".Items." + key2);
					        String name = key2;
					        Material m = Material.getMaterial(cs.getInt("MaterialID"));
					        int amount = cs.getInt("Amount");
					        if(m == null || amount == 0){
					        	battleOfBlocks.getLogger().severe("Error while loading the item " + name + " in the kit " + kit_name);
					        } else {
						        ItemStack is = new ItemStack(m, amount);
						        ItemMeta im = is.getItemMeta();
						        im.setDisplayName(name);
						        is.setItemMeta(im);
						        ItemsKit ik = new ItemsKit(is);
						        v.addElement(ik);
					        }
				    }
				    if(v.isEmpty() || kit_des == null || logo == null){
				    	battleOfBlocks.getLogger().severe("Error while loading the kit " + kit_name);
				    } else {
					    Kit k = new Kit(v, kit_name,kit_des, logo, kit_perm, priceToBuy);
					    v_kits.addElement(k);
				    }
		      }
		      Kits ks = new Kits(v_kits);
		      battleOfBlocks.getLogger().info("Loaded kits !");
		      return ks;
			}
		} catch(Exception e){
			e.printStackTrace();
			battleOfBlocks.getLogger().severe("Error while loading kits !");
		}
		return null;
	}
}
