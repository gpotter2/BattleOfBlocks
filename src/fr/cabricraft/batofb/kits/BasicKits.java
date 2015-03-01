package fr.cabricraft.batofb.kits;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import fr.cabricraft.batofb.BattleOfBlocks;
import fr.cabricraft.batofb.arenas.Arena;

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
		List<String> l = new LinkedList<String>();
		l.add("Mage");
		if(hasBoughtKit(p, "Spy")) l.add("Spy");
		if(hasBoughtKit(p, "Phoenix")) l.add("Phoenix");
		if(hasBoughtKit(p, "Shamen")) l.add("Shamen");
		if(hasBoughtKit(p, "Healer")) l.add("Healer");
		if(hasBoughtKit(p, "Troll")) l.add("Troll");
		if(hasBoughtKit(p, "Fury")) l.add("Fury");
		String kit;
		if(l.size() == 1){
			kit = l.get(0);
		} else {
			int nombreAleatoire = (int)(Math.random() * (l.size()));
			kit = l.get(nombreAleatoire);
		}
		arena.p_kits.put(p.getUniqueId(), kit);
	}
	
	@SuppressWarnings("deprecation")
	public List<ItemStack> getItemsPlayer(Player p){
		if(arena.getKitName(p) == null){
			setRandomClass(p);
		}
		List<ItemStack> list = new LinkedList<ItemStack>();
		String kp = arena.getKitName(p);
		list.add(getIS(Material.IRON_SWORD, "SWORD", "Funny no ?", p));
	    if(kp.equalsIgnoreCase("Mage")){
	    	list.add(getIS(Material.BONE, "Flash !", "Flash players !", p));
	    } else if(kp.equalsIgnoreCase("Spy")){
	    	list.add(getIS(Material.INK_SACK, "Invisibility !", "Pouf !", p));
	    } else if(kp.equalsIgnoreCase("Phoenix")){
	    	list.add(getIS(Material.BLAZE_ROD, "Inferno !", "Burn !!", p));
	    } else if(kp.equalsIgnoreCase("Shamen")){
	    	list.add(getIS(Material.VINE, "Nature protection !", "Create a wall !", p));
	    } else if(kp.equalsIgnoreCase("Healer")){
	    	list.add(getIS(Material.STICK, "Healer !", "Heal people !", p));
	    } else if(kp.equalsIgnoreCase("Troll")){
	    	list.add(getIS(new ItemStack(Material.INK_SACK, 1, (short)0, DyeColor.RED.getData()), "Team color !", "Change your team color !", p));
	    } else if(kp.equalsIgnoreCase("Fury")){
	    	list.add(getIS(Material.TNT, "Fury !", "Punsh players !", p));
	    }
	    if(list.isEmpty()){
	    	return null;
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
