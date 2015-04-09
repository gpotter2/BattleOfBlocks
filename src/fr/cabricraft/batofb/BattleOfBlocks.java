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

package fr.cabricraft.batofb;

import java.awt.Event;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.cabricraft.batofb.Updater.UpdateResult;
import fr.cabricraft.batofb.arenas.Arena;
import fr.cabricraft.batofb.command.Commands;
import fr.cabricraft.batofb.kits.Kits;
import fr.cabricraft.batofb.kits.KitsLoader;
import fr.cabricraft.batofb.powerups.CustomAdd;
import fr.cabricraft.batofb.powerups.LoadCustomAdd;
import fr.cabricraft.batofb.powerups.Powerups;
import fr.cabricraft.batofb.shop.Shop;
import fr.cabricraft.batofb.signs.SignUtility;
import fr.cabricraft.batofb.util.DatabasesHandler;
import fr.cabricraft.batofb.util.DatabasesHandler.Condition;
import fr.cabricraft.batofb.util.DatabasesHandler.DatabaseType;
import fr.cabricraft.batofb.util.DatabasesHandler.ObjectType;
import fr.cabricraft.batofb.util.DatabasesUtil.DataObject;
import fr.cabricraft.batofb.util.Messages;

/**
 * This plugin is a free minigame !
 * 
 * @author gpotter2 
 * 
 */

public class BattleOfBlocks extends JavaPlugin implements Listener {
  public Vector<Arena> arenas = new Vector<Arena>();
  BattleOfBlocks battleOfBlocks;
  PluginManager pm;
  int defaultstart = 3;
  int defaultlives = 10;
  int defaultmax = 20;
  int defaultvip = 1;
  public Vector<Location> s = new Vector<Location>();
  public boolean barapienabled;
  public boolean vaultenabled_permissions = false;
  public boolean vaultenabled_economy = false;
  public static int time = 30;
  public int onwin = 100;
  public int facteurkills = 10;
  public boolean cheatadmin = false;
  public boolean check_update = true;
  public Economy econ = null;
  public Permission permission = null;
  public Messages msg;
  public CustomAdd ca = new CustomAdd();
  public String controlname = "BattleOfBlocks";
  public boolean ecoenabled = true;
  public Kits kits = null;
  public int respawnTime = 0;
  public Shop shop;
  public boolean playbydefault = true;
  
  public boolean stop = false;
  
  private boolean database_errors_notice = false;
  private boolean updatenotice = false;
  private String updatenoticemessage = null;
  public int update_id = 76657;
  public File batofb_file;
  public DatabasesHandler data_handler;
  public DatabaseType savehandler = null;
  
  public String database_name = "Minecraft";
  
  //Mysql
  private String host = "localhost";
  private String user = "username";
  private String pass = "password";
  private String port = "3306";
  //
  
  public void onLoad() {}
  
  public void onDisable(){
	this.stop = true;
    try {
      saveall();
    } catch (IOException e) {
      e.printStackTrace();
    }
    for(Arena ar : arenas){
    	if(ar.isstarted){
    		ar.finishthegame(0);
    	}
    }
  }
  
  public void onEnable() {
    try {
      long start_time = System.currentTimeMillis();
      this.stop = false;
      this.battleOfBlocks = this;
      if(!compilationSuccess()){
    	  getLogger().severe("Disabling the plugin !!!");
    	  new BukkitRunnable() {
    		Plugin plugin = battleOfBlocks;
			@Override
			public void run() {
				getPluginLoader().disablePlugin(plugin);
			}
		}.runTaskLater(this, 10L);
    	  return;
      }
      this.batofb_file = getFile();
      
      //LOADING...
      //LOADING ALL OTHER DATAS
      getShop();
      getKits();
      getInf();
      getMessages();
      addInf();
      loadsigns();
      //
      shop = new Shop(battleOfBlocks);
      if (this.check_update) {
	        Updater up = new Updater(this, this.update_id, this.batofb_file, Updater.UpdateType.NO_DOWNLOAD, false);
	        if(up.getResult() == UpdateResult.UPDATE_AVAILABLE){
	        	updatenotice = true;
	        	updatenoticemessage = up.getLatestName().toLowerCase().replace("battleofblocks", "");
	        	getLogger().info("A new version of the plugin is available: " + updatenoticemessage + " !");
	        }
      }
      
      try {
    	  Metrics metrics = new Metrics(this);
          metrics.start();
      } catch (IOException e) {
          // Failed to submit the stats :-(
      }
      
      pm = getServer().getPluginManager();
      
      this.pm.registerEvents(this, this.battleOfBlocks);
      //DETECTING PLUGINS
      if (pm.getPlugin("BarAPI") != null) {
        barapienabled = true;
        getLogger().info("Using BARApi !");
      } else barapienabled = false;
      
      if(pm.getPlugin("Vault") != null){
	      if (setupEconomy()) {
	    	  vaultenabled_economy = true;
	    	  getLogger().info("Using Vault -Economy!");
	      }
	      if (setupPermissions()) {
	    	  vaultenabled_permissions = true;
	    	  getLogger().info("Using Vault -Permissions!");
	      }
      }
      //
      if(!new File(getDataFolder().getAbsolutePath() + "/saves/").exists()){
		  new File(getDataFolder().getAbsolutePath() + "/saves/").mkdir();
	  }
      //LOADING ARENAS
      if(savehandler == null){
    	  FileConfiguration config = conffile("saves/save.yml");
	      config.set("A", "#####NEVER MODIFY THIS FILE FROM HERE !#####");
	      config.save(getDataFolder() + "/saves/save.yml");
	      if (config.getConfigurationSection("Arenas") != null) {
	        Set<String> keys = config.getConfigurationSection("Arenas").getKeys(false);
	        for (String key : keys) {
	        	load(key);
	        }
	      }
      } else if(savehandler == DatabaseType.MYSQL){
    	  data_handler = new DatabasesHandler(DatabaseType.MYSQL, "battleofblocks");
    	  boolean initied = data_handler.init(host, user, pass, database_name, port, "id");
    	  if(!initied){
    		  database_errors_notice = true;
    	  } else {
    		  data_handler.addColumn("name", ObjectType.VARCHAR);
    		  data_handler.addColumn("value", ObjectType.NONE);
    		  data_handler.addColumn("arena", ObjectType.VARCHAR);
    		  loadDatabase();
    	  }
      } else if(savehandler == DatabaseType.SQLITE){
    	  data_handler = new DatabasesHandler(DatabaseType.SQLITE, "battleofblocks", getDataFolder().getAbsolutePath() + "/saves/save.db");
    	  boolean initied = data_handler.init(null, null, null, database_name, null, "id");
    	  if(!initied){
    		  database_errors_notice = true;
    	  } else {
    		  data_handler.addColumn("name", ObjectType.VARCHAR);
    		  data_handler.addColumn("value", ObjectType.NONE);
    		  data_handler.addColumn("arena", ObjectType.VARCHAR);
    		  loadDatabase();
    	  }
      }
      //
      if(!database_errors_notice){
	      //IMPLEMENTINGS SIGNS AND SHOP
	      this.pm.registerEvents(new SignUtility(battleOfBlocks), battleOfBlocks);
	      this.pm.registerEvents(shop, battleOfBlocks);
	      //
	      //IMPLEMENTINGS COMMANDS
	      getCommand("battleofblocks").setExecutor(new Commands(battleOfBlocks));
	      getCommand("batofb").setExecutor(new Commands(battleOfBlocks));
	      //
      }
      //RELOADING SIGNS
      SignUtility.updatesigns();
      //
      //LOADING DONE !!!
      long final_time = System.currentTimeMillis() - start_time;
      getLogger().info("BattleOfBlocks loaded ! Done in " + final_time + "ms !");
    } catch (IOException e) {
      getLogger().severe("FATAL ERROR ON ENABLING BATTLEOFBLOCKS !");
      e.printStackTrace();
    }
  }
  
  public FileConfiguration conffile(String file){
	if(!new File(getDataFolder(), "saves/").exists()){
		new File(getDataFolder(), "saves/").mkdir();
	}
    File configFile = new File(getDataFolder(), file);
    if (!getDataFolder().exists()) {
      getDataFolder().mkdir();
    }
    if (!configFile.exists()) {
      try
      {
        configFile.createNewFile();
      }
      catch (IOException e)
      {
        e.printStackTrace();
        return null;
      }
    }
    FileConfiguration config = new YamlConfiguration();
    try
    {
      config.load(configFile);
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return config;
  }
  
  boolean setupEconomy() {
	  if(ecoenabled) {
	      RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	      if (rsp == null) {
	        return false;
	      }
	      this.econ = ((Economy) rsp.getProvider());
      }
      return true;
  }
  
  boolean setupPermissions(){
      RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
      if (permissionProvider != null) {
          permission = permissionProvider.getProvider();
      }
      return (permission != null);
  }
  
  public void reloadandremake() throws IOException {
	  if(savehandler == null){
	    FileConfiguration config = conffile("saves/save.yml");
	    if (config == null) {
	      getLogger().severe("CONFIGURATION FILE = NULL ! (reloadandmake)");
	      return;
	    }
	    config.set("Arenas", null);
	    config.save(getDataFolder() + "/saves/save.yml");
	    FileConfiguration config2 = getConfig();
	    config2.set("Configuration", null);
	    saveConfig();
	  } else {
		 data_handler.clearTable();
	  }
	  saveall();
  }
  
  public void loadDatabase(){
	  	String[] arenas_temp = null;
	  	List<Object> get = data_handler.getValues("value", new Condition("name", "infos"), new Condition("arena", "infos"));
	  	if(get != null){
	  		if(get.size() != 0){
	  			String arena_string_get = (String) get.get(0);
	  			if(arena_string_get != null){
	  				if(!arena_string_get.equals("") && !arena_string_get.equals("null")){
						arenas_temp = getArrayFromString(arena_string_get);
						if(arenas_temp != null){
							  if(arenas_temp.length != 0){
								  for(int i = 0; i < arenas_temp.length; i++){
									  String arena_name = arenas_temp[i];
									  if(arena_name != null){
										  if(!arena_name.equals("") && !arena_name.equals("null")){
											  loadArenaDatabase(arena_name);
										  }
									  }
								  }
							  }
						}
	  				}
	  			}
	  		}
	  	}
  }
  
  public void loadArenaDatabase(String arena_name){
	  	int lives;
	    int startmin;
	    int pmax;
	    int vip;
	    Location lwaitroom = null;
	    Location lstartblue = null;
	    Location lstartred = null;
	    Location lend = null;
	    Vector<Location> vred = new Vector<Location>();
	    Vector<Location> vblue = new Vector<Location>();
	    Vector<ItemStack> rewards = new Vector<ItemStack>();
	    boolean canbuild = false;
	    boolean canbreak = false;
	    lives = Integer.parseInt((String) data_handler.getValues("value", new Condition("name", "lives"), new Condition("arena", "arena_" + arena_name)).get(0));
	    startmin = Integer.parseInt((String) data_handler.getValues("value", new Condition("name", "startmin"), new Condition("arena", "arena_" + arena_name)).get(0));
	    pmax = Integer.parseInt((String) data_handler.getValues("value", new Condition("name", "pmax"), new Condition("arena", "arena_" + arena_name)).get(0));
	    vip = Integer.parseInt((String) data_handler.getValues("value", new Condition("name", "vip"), new Condition("arena", "arena_" + arena_name)).get(0));
	    canbuild = Boolean.parseBoolean((String) data_handler.getValues("value", new Condition("name", "canbuild"), new Condition("arena", "arena_" + arena_name)).get(0));
	    canbreak = Boolean.parseBoolean((String) data_handler.getValues("value", new Condition("name", "canbreak"), new Condition("arena", "arena_" + arena_name)).get(0));
	    lwaitroom = str2loc((String) data_handler.getValues("value", new Condition("name", "lwaitroom"), new Condition("arena", "arena_" + arena_name)).get(0));
	    lstartblue = str2loc((String) data_handler.getValues("value", new Condition("name", "lstartblue"), new Condition("arena", "arena_" + arena_name)).get(0));
	    lstartred = str2loc((String) data_handler.getValues("value", new Condition("name", "lstartred"), new Condition("arena", "arena_" + arena_name)).get(0));
	    lend = str2loc((String) data_handler.getValues("value", new Condition("name", "lend"), new Condition("arena", "arena_" + arena_name)).get(0));
	    
	    List<Object> red_blocks_get = data_handler.getValues("value", new Condition("name", "red_blocks"), new Condition("arena", "arena_" + arena_name));
	    if(red_blocks_get != null){
	    	if(red_blocks_get.size() != 0){
				String[] red_blocks = getArrayFromString((String) red_blocks_get.get(0));
				for (String value : red_blocks) {
			    	Location l = str2loc(value);
			      	vred.addElement(l);
			    }
	    	}
	    }
	    List<Object> blue_blocks_get = data_handler.getValues("value", new Condition("name", "blue_blocks"), new Condition("arena", "arena_" + arena_name));
	    if(blue_blocks_get != null){
	    	if(blue_blocks_get.size() != 0){
				String[] blue_blocks = getArrayFromString((String) blue_blocks_get.get(0));
				for (String value : blue_blocks) {
			    	Location l = str2loc(value);
			      	vblue.addElement(l);
			    }
	    	}
	    }
	    List<Object> reward_items_get = data_handler.getValues("value", new Condition("name", "reward_items"), new Condition("arena", "arena_" + arena_name));
	    if(reward_items_get != null){
	    	if(reward_items_get.size() != 0){
		    	String[] reward_items = getArrayFromString((String) reward_items_get.get(0));
			    for (String value : reward_items) {
			    	ItemStack is = str2item(value);
			      	rewards.addElement(is);
			    }
	    	}
	    }
	    Arena ar = new Arena(arena_name, this.battleOfBlocks, lstartred, lstartblue, lend, lwaitroom, vred, vblue, lives, pmax, startmin, onwin, facteurkills, vip, rewards, canbuild, canbreak);
	    Powerups up = new Powerups(ar);
	    this.arenas.addElement(ar);
	    this.pm.registerEvents(ar, this.battleOfBlocks);
	    this.pm.registerEvents(up, this.battleOfBlocks);
  }
  
  public void saveValueArenaDatabase(String arena_name, String name_value, Object data){
	  	DataObject[] da = new DataObject[3];
	  	da[0] = new DataObject("arena", arena_name);
	  	da[1] = new DataObject("name", name_value);
	  	da[2] = new DataObject("value", data + "");
	  	data_handler.InsertOrUpdateValue(da, new Condition("arena", arena_name), new Condition("name", name_value));
  }
  
  public void saveArenaDatabase(Arena ar) throws IOException {
	  	String arena_name = "arena_" + ar.getName();
	  	saveValueArenaDatabase(arena_name, "lives", ar.life);
	  	saveValueArenaDatabase(arena_name, "startmin", ar.startmin);
	  	saveValueArenaDatabase(arena_name, "pmax", ar.pmax);
	  	saveValueArenaDatabase(arena_name, "vip", ar.vip);
	  	saveValueArenaDatabase(arena_name, "canbuild", ar.canbuild);
	  	saveValueArenaDatabase(arena_name, "canbreak", ar.canbreak);
	  	saveValueArenaDatabase(arena_name, "lwaitroom", loc2str(ar.waitroom));
	  	saveValueArenaDatabase(arena_name, "lstartblue", loc2str(ar.locstartblue));
	  	saveValueArenaDatabase(arena_name, "lstartred", loc2str(ar.locstartblue));
	  	saveValueArenaDatabase(arena_name, "lend", loc2str(ar.locend));
	    List<String> red_blocks = new LinkedList<String>();
	    for (int i = 0; i < ar.vred.size(); i++) {
	    	red_blocks.add(loc2str(ar.vred.elementAt(i)));
	    }
		saveValueArenaDatabase(arena_name, "red_blocks", getStringFromArray(red_blocks.toArray(new String[red_blocks.size()])));
	    List<String> blue_blocks = new LinkedList<String>();
	    for (int i = 0; i < ar.vblue.size(); i++) {
	    	blue_blocks.add(loc2str(ar.vblue.elementAt(i)));
	    }
		saveValueArenaDatabase(arena_name, "blue_blocks", getStringFromArray(blue_blocks.toArray(new String[blue_blocks.size()])));
	    List<String> rewards_items = new LinkedList<String>();
	    for (ItemStack is : ar.reward) {
	    	rewards_items.add(item2str(is));
	    }
		saveValueArenaDatabase(arena_name, "rewards_items", getStringFromArray(rewards_items.toArray(new String[rewards_items.size()])));
	  }
  
  public void save(Arena ar) throws IOException {
	String pos = "Arenas." + ar.getName();
    FileConfiguration config = conffile("saves/save.yml");
    if (config == null) {
      getLogger().severe("CONFIGURATION FILE = NULL ! (save)");
      return;
    }
    String node3 = pos + "." + "GameConfig";
    config.set(node3 + ".lives", Integer.valueOf(ar.life));
    config.set(node3 + ".startmin", Integer.valueOf(ar.startmin));
    config.set(node3 + ".playersmax", Integer.valueOf(ar.pmax));
    config.set(node3 + ".vip", Integer.valueOf(ar.vip));
    config.set(node3 + ".canbuild", ar.canbuild);
    config.set(node3 + ".canbreak", ar.canbreak);
    
    String node2 = pos + "." + "Save";
    if (ar.waitroom != null) {
      String wait = node2 + ".wait";
      Location l1 = ar.waitroom;
      if(l1.getWorld() != null) {
	      config.set(wait + ".location.world", l1.getWorld().getName());
	      config.set(wait + ".location.x", Integer.valueOf(l1.getBlockX()));
	      config.set(wait + ".location.y", Integer.valueOf(l1.getBlockY()));
	      config.set(wait + ".location.z", Integer.valueOf(l1.getBlockZ()));
	      config.set(wait + ".location.pitch", l1.getPitch());
	      config.set(wait + ".location.yaw", l1.getYaw());
      }
    }
    if (ar.locstartblue != null) {
      String start = node2 + ".startblue";
      Location l1 = ar.locstartblue;
	      if(l1.getWorld() != null) {
	      config.set(start + ".location.world", l1.getWorld().getName());
	      config.set(start + ".location.x", Integer.valueOf(l1.getBlockX()));
	      config.set(start + ".location.y", Integer.valueOf(l1.getBlockY()));
	      config.set(start + ".location.z", Integer.valueOf(l1.getBlockZ()));
	      config.set(start + ".location.pitch", l1.getPitch());
	      config.set(start + ".location.yaw", l1.getYaw());
      }
    }
    if (ar.locstartred != null) {
      String start = node2 + ".startred";
      Location l1 = ar.locstartred;
      if(l1.getWorld() != null) {
	      config.set(start + ".location.world", l1.getWorld().getName());
	      config.set(start + ".location.x", Integer.valueOf(l1.getBlockX()));
	      config.set(start + ".location.y", Integer.valueOf(l1.getBlockY()));
	      config.set(start + ".location.z", Integer.valueOf(l1.getBlockZ()));
	      config.set(start + ".location.pitch", l1.getPitch());
	      config.set(start + ".location.yaw", l1.getYaw());
      }
    }
    if (ar.locend != null) {
      String stop = node2 + ".end";
      Location l3 = ar.locend;
      if(l3.getWorld() != null) {
	      config.set(stop + ".location.world", l3.getWorld().getName());
	      config.set(stop + ".location.x", Integer.valueOf(l3.getBlockX()));
	      config.set(stop + ".location.y", Integer.valueOf(l3.getBlockY()));
	      config.set(stop + ".location.z", Integer.valueOf(l3.getBlockZ()));
	      config.set(stop + ".location.pitch", l3.getPitch());
	      config.set(stop + ".location.yaw", l3.getYaw());
      }
    }
    for (int i = 0; i < ar.vblue.size(); i++) {
      Location l = (Location) ar.vblue.elementAt(i);
      if(l.getWorld() != null) {
	      String node = pos + "." + "Save.Blue." + l.getWorld().getName() + "_" + l.getBlockX() + "_" + l.getBlockY() + "_" + l.getBlockZ();
	      config.set(node + ".location.world", l.getWorld().getName());
	      config.set(node + ".location.x", Integer.valueOf(l.getBlockX()));
	      config.set(node + ".location.y", Integer.valueOf(l.getBlockY()));
	      config.set(node + ".location.z", Integer.valueOf(l.getBlockZ()));
	      config.set(node + ".location.pitch", l.getPitch());
	      config.set(node + ".location.yaw", l.getYaw());
      }
    }
    for (int i = 0; i < ar.vred.size(); i++) {
      Location l = (Location)ar.vred.elementAt(i);
      if(l.getWorld() != null) {
	      String node = pos + "." + "Save.Red." + l.getWorld().getName() + "_" + l.getBlockX() + "_" + l.getBlockY() + "_" + l.getBlockZ();
	      config.set(node + ".location.world", l.getWorld().getName());
	      config.set(node + ".location.x", Integer.valueOf(l.getBlockX()));
	      config.set(node + ".location.y", Integer.valueOf(l.getBlockY()));
	      config.set(node + ".location.z", Integer.valueOf(l.getBlockZ()));
	      config.set(node + ".location.pitch", l.getPitch());
	      config.set(node + ".location.yaw", l.getYaw());
      }
    }
    for (int i = 0; i < ar.reward.size(); i++) {
      ItemStack is = (ItemStack) ar.reward.get(i);
      String node = pos + "." + "RewardsItems." + is.getType().toString();
      config.set(node + ".Material", is.getType().toString());
      config.set(node + ".Amount", Integer.valueOf(is.getAmount()));
    }
    config.save(getDataFolder() + "/saves/save.yml");
  }
  
  public String getStringFromArray(String[] array){
	  String retur = null;
	  for(String value : array){
		  if(retur == null) retur = value;
		  else retur = retur + "#@#" + value;
	  }
	  return retur;
  }
  
  public String[] getArrayFromString(String str){
	  return str.split("#@#");
  }
  
  public void saveall()
    throws IOException
  {
    addInf();
    savesigns();
    if(savehandler == null){
	    for (int i = 0; i < this.arenas.size(); i++) {
	      Arena ar = (Arena) this.arenas.elementAt(i);
	      try {
	        save(ar);
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	    }
    } else {
    	List<String> arenas = new LinkedList<String>();
    	for (int i = 0; i < this.arenas.size(); i++) {
	  	      Arena ar = (Arena) this.arenas.elementAt(i);
	  	      try {
	  	        saveArenaDatabase(ar);
	  	      } catch (IOException e) {
	  	        e.printStackTrace();
	  	      }
	  	      arenas.add(ar.getName());
  	    }
    	List<DataObject> da = new LinkedList<DataObject>();
	  	da.add(new DataObject("arena", "infos"));
	  	da.add(new DataObject("name", "infos"));
		da.add(new DataObject("value", getStringFromArray(arenas.toArray(new String[arenas.size()]))));
	  	data_handler.InsertOrUpdateValue(da.toArray(new DataObject[da.size()]), new Condition("arena", "infos"), new Condition("name", "infos"));
    }
    getLogger().info("BattleOfBlocks's configuration writed !");
  }
  
  public void loadsigns()
  {
    FileConfiguration config = conffile("saves/signs.yml");
    if (config.getConfigurationSection("Signs") != null) {
      Set<String> keys = config.getConfigurationSection("Signs").getKeys(false);
      for (String key : keys)
      {
        ConfigurationSection cs = config.getConfigurationSection("Signs." + key);
        World w = getServer().getWorld(cs.getString("location.world"));
        if (w == null) {
          break;
        }
        Double x = Double.valueOf(cs.getDouble("location.x"));
        Double y = Double.valueOf(cs.getDouble("location.y"));
        Double z = Double.valueOf(cs.getDouble("location.z"));
        
        Location l = new Location(w, x.doubleValue(), y.doubleValue(), z.doubleValue());
        s.addElement(l);
      }
    }
  }
  
  @EventHandler
  public void join(PlayerJoinEvent event){
	  if(updatenotice){
		  if(event.getPlayer().hasPermission("battleofblocks.setup")){
			  event.getPlayer().sendMessage(ChatColor.GREEN + "[BattleOfBlocks] A new great update of the plugin is available !");
			  event.getPlayer().sendMessage(ChatColor.GREEN + "[BattleOfBlocks] Your version: " + ChatColor.RED + "v" + getDescription().getVersion() + ChatColor.GREEN + ". New version: " + ChatColor.GOLD + updatenoticemessage + ChatColor.GREEN + " !");
			  event.getPlayer().sendMessage(ChatColor.GREEN + "[BattleOfBlocks] To download it, just type: '/battleofblocks update' !");
		  }
	  }
	  if(database_errors_notice){
		  if(event.getPlayer().hasPermission("battleofblocks.setup")){
			  event.getPlayer().sendMessage(ChatColor.RED + "[BattleOfBlocks] BattleOfBlocks couldn't use the databases ! The plugin is inactive !");
		  }
	  }
  }
  
  public void load(String namearena) {
    String pos = "Arenas." + namearena;
    
    FileConfiguration config = conffile("saves/save.yml");
    int lives;
    int startmin;
    int pmax;
    int vip;
    Location lwaitroom = null;
    Location lstartblue = null;
    Location lstartred = null;
    Location lend = null;
    Vector<Location> vred = new Vector<Location>();
    Vector<Location> vblue = new Vector<Location>();
    Vector<ItemStack> rewards = new Vector<ItemStack>();
    boolean canbuild = false;
    boolean canbreak = false;
    if (config.getConfigurationSection(pos + "." + "GameConfig") != null) {
      ConfigurationSection cs2 = config.getConfigurationSection(pos + "." + "GameConfig");
      lives = cs2.getInt("lives");
      startmin = cs2.getInt("startmin");
      pmax = cs2.getInt("playersmax");
      vip = cs2.getInt("vip");
      canbuild = cs2.getBoolean("canbuild");
      canbreak = cs2.getBoolean("canbreak");
    }
    else
    {
      lives = this.defaultlives;
      startmin = this.defaultstart;
      pmax = this.defaultmax;
      vip = this.defaultvip;
    }
    if (config.getConfigurationSection(pos + "." + "Save.wait") != null) {
      ConfigurationSection cs = config.getConfigurationSection(pos + "." + "Save.wait");
      World w = getServer().getWorld(cs.getString("location.world"));
      Double x = Double.valueOf(cs.getDouble("location.x"));
      Double y = Double.valueOf(cs.getDouble("location.y"));
      Double z = Double.valueOf(cs.getDouble("location.z"));
      Float yaw = Float.valueOf(cs.getInt("location.yaw"));
      Float pitch = Float.valueOf(cs.getInt("location.pitch"));
      
      lwaitroom = new Location(w, x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw, pitch);
    }
    if (config.getConfigurationSection(pos + "." + "Save.startblue") != null) {
      ConfigurationSection cs = config.getConfigurationSection(pos + "." + "Save.startblue");
      World w = getServer().getWorld(cs.getString("location.world"));
      Double x = Double.valueOf(cs.getDouble("location.x"));
      Double y = Double.valueOf(cs.getDouble("location.y"));
      Double z = Double.valueOf(cs.getDouble("location.z"));
      Float yaw = Float.valueOf(cs.getInt("location.yaw"));
      Float pitch = Float.valueOf(cs.getInt("location.pitch"));
      
      lstartblue = new Location(w, x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw, pitch);
    }
    if (config.getConfigurationSection(pos + "." + "Save.startred") != null) {
      ConfigurationSection cs = config.getConfigurationSection(pos + "." + "Save.startred");
      World w = getServer().getWorld(cs.getString("location.world"));
      Double x = Double.valueOf(cs.getDouble("location.x"));
      Double y = Double.valueOf(cs.getDouble("location.y"));
      Double z = Double.valueOf(cs.getDouble("location.z"));
      Float yaw = Float.valueOf(cs.getInt("location.yaw"));
      Float pitch = Float.valueOf(cs.getInt("location.pitch"));
      
      lstartred = new Location(w, x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw, pitch);
    }
    if (config.getConfigurationSection(pos + "." + "Save.end") != null) {
      ConfigurationSection cs = config.getConfigurationSection(pos + "." + "Save.end");
      World w = getServer().getWorld(cs.getString("location.world"));
      Double x = Double.valueOf(cs.getDouble("location.x"));
      Double y = Double.valueOf(cs.getDouble("location.y"));
      Double z = Double.valueOf(cs.getDouble("location.z"));
      Float yaw = Float.valueOf(cs.getInt("location.yaw"));
      Float pitch = Float.valueOf(cs.getInt("location.pitch"));
      
      lend = new Location(w, x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw, pitch);
    }
    if (config.getConfigurationSection(pos + "." + "Save.Red") != null) {
      Set<String> keys = config.getConfigurationSection(pos + "." + "Save.Red").getKeys(false);
      for (String key : keys)
      {
        ConfigurationSection cs = config.getConfigurationSection(pos + "." + "Save.Red." + key);
        World w = getServer().getWorld(cs.getString("location.world"));
        if (w == null) {
          break;
        }
        Double x = Double.valueOf(cs.getDouble("location.x"));
        Double y = Double.valueOf(cs.getDouble("location.y"));
        Double z = Double.valueOf(cs.getDouble("location.z"));
        Float yaw = Float.valueOf(cs.getInt("location.yaw"));
        Float pitch = Float.valueOf(cs.getInt("location.pitch"));
        
        Location l = new Location(w, x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw, pitch);
        vred.addElement(l);
      }
    }
    if (config.getConfigurationSection(pos + "." + "Save.Blue") != null) {
      Set<String> keys2 = config.getConfigurationSection(pos + "." + "Save.Blue").getKeys(false);
      for (String key : keys2)
      {
        ConfigurationSection cs = config.getConfigurationSection(pos + "." + "Save.Blue." + key);
        World w = getServer().getWorld(cs.getString("location.world"));
        if (w == null) {
          break;
        }
        Double x = Double.valueOf(cs.getDouble("location.x"));
        Double y = Double.valueOf(cs.getDouble("location.y"));
        Double z = Double.valueOf(cs.getDouble("location.z"));
        Float yaw = Float.valueOf(cs.getInt("location.yaw"));
        Float pitch = Float.valueOf(cs.getInt("location.pitch"));
        
        Location l = new Location(w, x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw, pitch);
        vblue.addElement(l);
      }
    }
    if (config.getConfigurationSection(pos + "." + "RewardsItems") != null) {
      Set<String> keys3 = config.getConfigurationSection(pos + "." + "RewardsItems").getKeys(false);
      for (String key : keys3)
      {
        ConfigurationSection cs = config.getConfigurationSection(pos + "." + "RewardsItems." + key);
        Material m = Material.getMaterial(cs.getString("Material"));
        int amount = cs.getInt("Amount");
        rewards.addElement(new ItemStack(m, amount));
      }
    }
    Arena ar = new Arena(namearena, this.battleOfBlocks, lstartred, lstartblue, lend, lwaitroom, vred, vblue, lives, pmax, startmin, onwin, facteurkills, vip, rewards, canbuild, canbreak);
    Powerups up = new Powerups(ar);
    this.arenas.addElement(ar);
    this.pm.registerEvents(ar, this.battleOfBlocks);
    this.pm.registerEvents(up, this.battleOfBlocks);
  }
  
  public void addInf()
    throws IOException {
    FileConfiguration conf = getConfig();
    conf.set("Configuration", null);
    saveConfig();
    conf.set("A", "#####READ ME !#####");
    conf.set("B", "#####YOU CANNOT EDIT THIS FILE WHEN THE SERVER IS RUNNING !#####");
    conf.set("C", "#####YOU HAVE TO STOP IT, MODIFY THIS FILE, AND RESTART IT !#####");
    if(savehandler == null){
    	conf.set("Configuration.SaveHandler", "yaml");
    } else {
    	conf.set("Configuration.SaveHandler", savehandler.toString());
    }
    conf.set("Configuration.Defaultlifes", Integer.valueOf(defaultlives));
    conf.set("Configuration.Defaultstart", Integer.valueOf(defaultstart));
    conf.set("Configuration.Defaultmax", Integer.valueOf(defaultmax));
    conf.set("Configuration.DefaultVip", Integer.valueOf(defaultvip));
    conf.set("Configuration.DefaultFacteurKills", Integer.valueOf(facteurkills));
    conf.set("Configuration.DefaultCoinsOnWin", Integer.valueOf(onwin));
    conf.set("Configuration.Timebeforestart", Integer.valueOf(time));
    conf.set("Configuration.PlayByDefault", Boolean.valueOf(playbydefault));
    conf.set("Configuration.CheatAdmin", Boolean.valueOf(cheatadmin));
    conf.set("Configuration.EconomyEnabled", Boolean.valueOf(ecoenabled));
    conf.set("Configuration.ControlName", controlname);
    conf.set("Configuration.CheckUpdate", Boolean.valueOf(check_update));
    conf.set("Configuration.RespawnTimeInSecs", Integer.valueOf(respawnTime));
    conf.set("Configuration.Database.DatabaseName", database_name);
    conf.set("Configuration.Database.MySQL.host", host);
    conf.set("Configuration.Database.MySQL.user", user);
    conf.set("Configuration.Database.MySQL.pass", pass);
    conf.set("Configuration.Database.MySQL.port", port);
    saveConfig();
  }
  
  public void savesigns() {
	FileConfiguration conf = conffile("saves/signs.yml");
    conf.set("Signs", null);
    for (int i = 0; i < s.size(); i++) {
      Location l = s.elementAt(i);
      if(l.getBlock().getType() == Material.WALL_SIGN || l.getBlock().getType() == Material.SIGN || l.getBlock().getType() == Material.SIGN_POST) {
	      String node = "Signs." + l.getWorld().getName() + "_" + l.getBlockX() + "_" + l.getBlockY() + "_" + l.getBlockZ();
	      conf.set(node + ".location.world", l.getWorld().getName());
	      conf.set(node + ".location.x", Integer.valueOf(l.getBlockX()));
	      conf.set(node + ".location.y", Integer.valueOf(l.getBlockY()));
	      conf.set(node + ".location.z", Integer.valueOf(l.getBlockZ()));
      }
    }
    try {
		conf.save(getDataFolder() + "/saves/signs.yml");
	} catch (IOException e) {
		e.printStackTrace();
	}
  }
  
  public void getInf() {
    FileConfiguration conf = getConfig();
    if (conf.getConfigurationSection("Configuration") != null) {
      ConfigurationSection cs = conf.getConfigurationSection("Configuration");
      if (cs.isSet("SaveHandler")) {
    	  String get_save_handler = cs.getString("SaveHandler");
    	  if(get_save_handler.equalsIgnoreCase("sqlite")){
    		  savehandler = DatabasesHandler.DatabaseType.SQLITE;
    	  } else if(get_save_handler.equalsIgnoreCase("mysql")){
    		  savehandler = DatabasesHandler.DatabaseType.MYSQL;
    	  } else if(get_save_handler.equalsIgnoreCase("yaml")){
    		  savehandler = null;
    	  } else {
    		  getLogger().info(get_save_handler + " is not a valide saveHandler type ! (yaml/sqlite/mysql)");
    		  getLogger().info("Using Yaml !");
    		  savehandler = null;
    	  }
      }
      if (cs.isSet("Defaultlifes")) {
        defaultlives = cs.getInt("Defaultlifes");
      }
      if (cs.isSet("Defaultmax")) {
        defaultmax = cs.getInt("Defaultmax");
      }
      if (cs.isSet("Defaultstart")) {
        defaultstart = cs.getInt("Defaultstart");
      }
      if (cs.isSet("DefaultVip")) {
        defaultvip = cs.getInt("DefaultVip");
      }
      if (cs.isSet("DefaultCoinsOnWin")) {
        onwin = cs.getInt("DefaultCoinsOnWin");
      }
      if (cs.isSet("DefaultFacteurKills")) {
        facteurkills = cs.getInt("DefaultFacteurKills");
      }
      if (cs.isSet("Timebeforestart")) {
        time = cs.getInt("Timebeforestart");
      }
      if (cs.isSet("RespawnTimeInSecs")) {
    	  respawnTime = cs.getInt("RespawnTimeInSecs");
      }
      if (cs.isSet("PlayByDefault")) {
        playbydefault = cs.getBoolean("PlayByDefault");
      }
      if (cs.isSet("ControlName")) {
    	  String temp = cs.getString("ControlName");
    	  if(temp.length() > 15){
    		  getLogger().severe("Cannot load 'ControlName' in config.yml: The controlname musn't been bigger than 15 caracters !");
    		  controlname = "BattleOfBlocks";
    	  } else {
    		  controlname = new String(temp);
    	  }
      }
      if (cs.isSet("CheckUpdate")) {
        check_update = cs.getBoolean("CheckUpdate");
      }
      if (cs.isSet("EconomyEnabled")) {
    	  ecoenabled = cs.getBoolean("EconomyEnabled");
      }
      if (cs.isSet("RespawnTimeInSecs")) {
    	  respawnTime = cs.getInt("RespawnTimeInSecs");
      }
      if(cs.isSet("Database.DatabaseName")){
    	  database_name = cs.getString("Database.DatabaseName");
      }
      ConfigurationSection cs2 = cs.getConfigurationSection("Database.MySQL");
      if(cs2 != null){
	      if (cs2.isSet("host")) {
	    	  host = cs2.getString("host");
	      }
	      if (cs2.isSet("user")) {
	    	  user = cs2.getString("user");
	      }
	      if (cs2.isSet("pass")) {
	    	  pass = cs2.getString("pass");
	      }
	      if (cs2.isSet("port")) {
	    	  port = cs2.getString("port");
	      }
      }
      getLogger().info("Plugin configuration loaded !");
    }
  }
  
  public Arena getArena(String nomarene) {
    for (int i = 0; i < this.arenas.size(); i++) {
      Arena ar = (Arena) this.arenas.elementAt(i);
      if (ar.getName().equals(nomarene)) {
        return (Arena) this.arenas.elementAt(i);
      }
    }
    return null;
  }
  
  public void getMessages() {
	    msg = new Messages(battleOfBlocks);
	    msg.load();
  }
  public void getShop() {
	    FileConfiguration fc = conffile("shop.yml");
	    LoadCustomAdd lca = new LoadCustomAdd(this);
	    lca.load(fc);
  }
  
  public void getKits() {
	    FileConfiguration fc = conffile("kits.yml");
	    KitsLoader kl = new KitsLoader(this);
	    kits = kl.load(fc);
}
  
  public boolean Arenaexist(String nomarene)
  {
    for (int i = 0; i < this.arenas.size(); i++) {
      Arena ar = (Arena)this.arenas.elementAt(i);
      if (ar.getName().equals(nomarene)) {
        return true;
      }
    }
    return false;
  }
  
  public void addarena(String arenaname) {
    Arena ar = new Arena(arenaname, this.battleOfBlocks, null, null, null, null, new Vector<Location>(), new Vector<Location>(), this.defaultlives, this.defaultmax, this.defaultstart, this.onwin, this.facteurkills, this.defaultvip, new Vector<ItemStack>(), false, false);
    Powerups up = new Powerups(ar);
    this.arenas.addElement(ar);
    this.pm.registerEvents(ar, this.battleOfBlocks);
    this.pm.registerEvents(up, this.battleOfBlocks);
  }
  
  public void removearena(String arenaname)
  {
    for (int i = 0; i < this.arenas.size(); i++) {
      Arena ar = (Arena)this.arenas.elementAt(i);
      if (ar.getName().equals(arenaname)) {
        this.arenas.removeElementAt(i);
      }
    }
  }
  
  public String Arenalist() {
    String reponse = "";
    for (int i = 0; i < this.arenas.size(); i++) {
      Arena ar = (Arena)this.arenas.elementAt(i);
      if (reponse == "") {
        reponse = reponse + ar.getName();
      } else {
        reponse = reponse + "; " + ar.getName();
      }
    }
    return reponse;
  }

  public boolean hasPermission(Permissible p, String permission){
	  if(p instanceof Player){
		  Player player = (Player) p;
		  if(vaultenabled_permissions){
			  return battleOfBlocks.permission.has(player, permission);
		  } else {
			  return p.hasPermission(permission);
		  }
	  } else {
		  return p.hasPermission(permission);
	  }
  	}
	    @SuppressWarnings("unchecked")
		public void reload(CommandSender s) {
	    	
	    	final Plugin plugin = this;
	    	final CommandSender sender = s;
	    	
	    	try {
		    	new BukkitRunnable() {
					@Override
					public void run() {
						try {
							long start_time = System.currentTimeMillis();
					        PluginManager pluginManager = Bukkit.getPluginManager();
					        SimpleCommandMap commandMap = null;
					        List<Plugin> plugins = null;
					        Map<String, Plugin> names = null;
					        Map<String, Command> commands = null;
					        Map<Event, SortedSet<RegisteredListener>> listeners = null;
					        boolean reloadlisteners = true;
					        plugin.onDisable();
					        if (pluginManager != null) {
					            try {
					                Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
					                pluginsField.setAccessible(true);
					                plugins = (List<Plugin>) pluginsField.get(pluginManager);
					                Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
					                lookupNamesField.setAccessible(true);
					                names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);
					                try {
					                    Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
					                    listenersField.setAccessible(true);
					                    listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
					                } catch (Exception e) {
					                    reloadlisteners = false;
					                }
					                Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
					                commandMapField.setAccessible(true);
					                commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);
					                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
					                knownCommandsField.setAccessible(true);
					                commands = (Map<String, Command>) knownCommandsField.get(commandMap);
					            } catch (NoSuchFieldException e) {
					                e.printStackTrace();
					                return;
					            } catch (IllegalAccessException e) {
					                e.printStackTrace();
					                return;
					            }
					        }
					        pluginManager.disablePlugin(plugin);
					        if (plugins != null && plugins.contains(plugin))
					            plugins.remove(plugin);
					        if (names != null && names.containsKey(plugin))
					            names.remove(plugin);
					        if (listeners != null && reloadlisteners) {
					            for (SortedSet<RegisteredListener> set : listeners.values()) {
					                for (Iterator<RegisteredListener> it = set.iterator(); it.hasNext(); ) {
					                    RegisteredListener value = it.next();
					                    if (value.getPlugin() == plugin) {
					                        it.remove();
					                    }
					                }
					            }
					        }
					        if (commandMap != null) {
					            for (Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext(); ) {
					                Map.Entry<String, Command> entry = it.next();
					                if (entry.getValue() instanceof PluginCommand) {
					                    PluginCommand c = (PluginCommand) entry.getValue();
					                    if (c.getPlugin() == plugin) {
					                        c.unregister(commandMap);
					                        it.remove();
					                    }
					                }
					            }
					        }
					        ClassLoader cl = plugin.getClass().getClassLoader();
					        if (cl instanceof URLClassLoader) {
					            try {
					                ((URLClassLoader) cl).close();
					            } catch (IOException ex) {
					                Logger.getLogger(BattleOfBlocks.class.getName()).log(Level.SEVERE, null, ex);
					            }
					        }
					        System.gc();
					        Plugin target = null;
					        try {
					            target = Bukkit.getPluginManager().loadPlugin(getFile());
					        } catch (InvalidDescriptionException e) {
					            e.printStackTrace();
					            return;
					        } catch (InvalidPluginException e) {
					            e.printStackTrace();
					            return;
					        }
					        target.onLoad();
					        Bukkit.getPluginManager().enablePlugin(target);
					        long final_time = System.currentTimeMillis() - start_time;
					        sender.sendMessage(ChatColor.RED + "[BattleOfBlocks]" + ChatColor.GREEN + " Plugin sucessfuly reloaded ! (Done in " + final_time + "ms)");
						} catch(Exception e){
			        		sender.sendMessage(ChatColor.RED + "[BattleOfBlocks] Couldn't reload the plugin");
			        	}
					}
				}.runTaskLater(this, 10);
		    } catch(Exception e){
		    	sender.sendMessage(ChatColor.RED + "[BattleOfBlocks] Couldn't reload the plugin");
		    }
	    }
	    public Location str2loc(String str){
	    	if(str != null){
		        String str2loc[]=str.split("\\:");
		        Location loc = new Location(getServer().getWorld(str2loc[0]),0,0,0);
		        loc.setX(Double.parseDouble(str2loc[1]));
		        loc.setY(Double.parseDouble(str2loc[2]));
		        loc.setZ(Double.parseDouble(str2loc[3]));
		        loc.setYaw(Float.parseFloat(str2loc[4]));
		        loc.setPitch(Float.parseFloat(str2loc[5]));
		        return loc;
	    	}
	    	return null;
	    }
	    public String loc2str(Location loc){
	    	if(loc != null){
	    		return loc.getWorld().getName()+":"+loc.getBlockX()+":"+loc.getBlockY()+":"+loc.getBlockZ()+":"+loc.getYaw()+":"+loc.getPitch();
	    	}
	    	return null;
	    }
	    @SuppressWarnings("deprecation")
		public ItemStack str2item(String str){
	    	if(str != null){
		    	String str2item[]=str.split("\\:");
		        return new ItemStack(Material.valueOf(str2item[0]), Integer.valueOf(str2item[1]), (short) 0, Byte.parseByte(str2item[2]));
	    	}
	    	return null;
	    }
	    @SuppressWarnings("deprecation")
		public String item2str(ItemStack i){
	    	if(i != null){
	    		return i.getType().toString()+":"+i.getAmount()+":"+i.getData().getData();
	    	}
	    	return null;
	    }
	    
	    public boolean compilationSuccess(){
	    	getLogger().info("Checking valuability of the file...");
	    	String base = "fr.cabricraft.batofb.";
			try {
				Class.forName(base + "Metrics");
				Class.forName(base + "Updater");
				Class.forName(base + "arenas.Arena");
				Class.forName(base + "arenas.ArenaChrono");
				Class.forName(base + "command.Commands");
				Class.forName(base + "economy.EconomyManager");
				Class.forName(base + "kits.BasicKits");
				Class.forName(base + "kits.ItemsKit");
				Class.forName(base + "kits.Kit");
				Class.forName(base + "kits.Kits");
				Class.forName(base + "kits.KitsLoader");
				Class.forName(base + "powerups.CustomAdd");
				Class.forName(base + "powerups.CustomAddPower");
				Class.forName(base + "powerups.LoadCustomAdd");
				Class.forName(base + "powerups.Powerups");
				Class.forName(base + "powerups.PowerupsChrono");
				Class.forName(base + "shop.Shop");
				Class.forName(base + "signs.SignUtility");
				Class.forName(base + "util.BlockSave");
				Class.forName(base + "util.ClassTaker");
				Class.forName(base + "util.DatabasesHandler");
				Class.forName(base + "util.DatabasesUtil");
				Class.forName(base + "util.IronGolemChorno");
				Class.forName(base + "util.IronGolemControl");
				Class.forName(base + "util.Messages");
				Class.forName(base + "util.ParticleEffect");
				Class.forName(base + "util.ParticleLauncher");
				Class.forName(base + "util.ReflectionUtils");
				Class.forName(base + "util.ToogleInventory");
				getLogger().info("This file was successfuly compiled !");
				return true;
			} catch(NoClassDefFoundError e){
				getLogger().severe("######################################################");
				getLogger().severe("######################################################");
				getLogger().severe("The class '" + e.getMessage() + "' wasn't compiled !!!");
				getLogger().severe("######################################################");
				getLogger().severe("######################################################");
				return false;
			} catch(ClassNotFoundException e){
				getLogger().severe("######################################################");
				getLogger().severe("######################################################");
				getLogger().severe("The class '" + e.getMessage() + "' wasn't compiled !!!");
				getLogger().severe("######################################################");
				getLogger().severe("######################################################");
				return false;
			}
		}
}