package fr.cabricraft.batofb.util;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import fr.cabricraft.batofb.BattleOfBlocks;
import me.confuser.barapi.BarAPI;

public class BossBarConnector {
	
	boolean barapienabled = false;
	boolean apiSupported = false;
	
	private HashMap<String, BossBar> boss_bar_list = new HashMap<String, BossBar>();
	
	public BossBarConnector(BattleOfBlocks battleofblocks, PluginManager pm, Logger logger){
		if(BattleOfBlocks.version.isRecent()){
			apiSupported = true;
			logger.info("Hooked API NORMAL !");
		}
		if (pm.getPlugin("BarAPI") != null) {
			if(apiSupported){
				logger.severe("BARApi is deprecated with new versions of minecraft !");
			} else {
				barapienabled = true;
				logger.info("Using BARApi !");
			}
	    }
	}
	
	public void removeBar(List<Player> list_p, String bar_name){
		if(apiSupported){
			BossBar bb = boss_bar_list.get(bar_name);
			bb.removeAll();
			bb.setVisible(false);
			boss_bar_list.remove(bar_name);
		} else if (barapienabled) {
        	try {
        		for(Player p : list_p){
        			BarAPI.removeBar(p);
        		}
        	} catch (Exception e) {
    			Bukkit.getLogger().severe("BattleOfBlocks: Error while using BarAPI ! If you are using spigot 1.8, this may happend. Disabling BarAPI !!!");
    			barapienabled = false;
    		}
        }
	}
	
	private BarColor randomColor(){
		BarColor[] bars = BarColor.values();
		int rnd = new Random().nextInt(bars.length);
	    return bars[rnd];
	}
	
	private void addBar(String bar_name){
		boss_bar_list.put(bar_name, Bukkit.createBossBar("", randomColor(), BarStyle.SOLID));
	}
	
	public void updateBar(List<Player> list_p, String bar_name, String message, float percent){
		if (apiSupported) {
			if(!boss_bar_list.containsKey(bar_name)){
				addBar(bar_name);
			}
			BossBar bb = boss_bar_list.get(bar_name);
			bb.setTitle(message);
			bb.setProgress((percent/100));
		} else if(barapienabled){
			  for(Player p : list_p){
				try {
					BarAPI.setMessage(p, message, percent);
				} catch (Exception e) {
					Bukkit.getLogger().severe("BattleOfBlocks: Error while using BarAPI ! If you are using spigot 1.8, this may happend. Disabling BarAPI !!!");
					barapienabled = false;
					break;
				}
			}
		}
	}
	public void updateBarPlayer(Player p, String bar_name, String message, float percent) {
		if(apiSupported){
			updateBar(null, bar_name, message, percent);
		} else if(barapienabled){
			try {
				BarAPI.setMessage(p, message, percent);
			} catch (Exception e) {
				Bukkit.getLogger().severe("BattleOfBlocks: Error while using BarAPI ! If you are using spigot 1.8, this may happend. Disabling BarAPI !!!");
				barapienabled = false;
			}
		}
	}
	
	public void addPlayers(List<Player> players, String bar_name){
		if(!apiSupported) return;
		if(!boss_bar_list.containsKey(bar_name)){
			addBar(bar_name);
		}
		for(Player p : players){
			addPlayer(p, bar_name);
		}
	}
	
	public void addPlayer(Player p, String bar_name){
		if(!apiSupported) return;
		if(!boss_bar_list.containsKey(bar_name)){
			addBar(bar_name);
		}
		BossBar bb = boss_bar_list.get(bar_name);
		if(!bb.getPlayers().contains(p)){
			bb.addPlayer(p);
		}
	}
	
	public void removeAll(List<Player> players){
		if(apiSupported){
			for(String k : boss_bar_list.keySet()){
				removeBar(null, k);
			}
		} else if (barapienabled) {
			for(Player p : players){
				removeAll(p);
			}
		}
	}
	
	public void removeAll(Player player){
		if(apiSupported){
			for(BossBar bb : boss_bar_list.values()){
				if(bb.getPlayers().contains(player)){
					bb.removePlayer(player);
				}
			}
		} else if (barapienabled) {
	    	try {
	    		BarAPI.removeBar(player);
	    	} catch (Exception e) {
				Bukkit.getLogger().severe("BattleOfBlocks: Error while using BarAPI ! If you are using spigot 1.8, this may happend. Disabling BarAPI !!!");
				barapienabled = false;
			}
	    }
	}
}
