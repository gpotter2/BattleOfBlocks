package fr.cabricraft.batofb;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.Vector;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

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
import fr.cabricraft.batofb.util.Messages;

/*
 * This plugin is a free minigame !
 * You may copy the code and create another plugin for yourself only, but you cannot distribute it !
 * 
 * Code writed by gpotter2
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
  boolean update = true;
  public Economy econ = null;
  public Permission permission = null;
  public Messages msg;
  public CustomAdd ca = new CustomAdd();
  public String controlname = "BattleOfBlocks";
  public boolean ecoenabled = true;
  public Kits kits = null;
  public int respawnTime = 0;
  public Shop shop;
  
  public boolean stop = false;
  
  private boolean updatenotice = false;
  
  public void onLoad() {}
  
  public void onDisable(){
	this.stop = true;
    try {
      saveall();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    for(Arena ar : arenas){
    	if(ar.isstarted){
    		ar.finishthegame(0);
    	}
    }
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
  
  public void onEnable()
  {
    try
    {
      this.stop = false;
      this.battleOfBlocks = this;
      
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
      if (this.update) {
	        Updater up = new Updater(this, 76657, getFile(), Updater.UpdateType.DEFAULT, true);
	        if(up.getResult() == UpdateResult.SUCCESS) updatenotice = true;
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
      //LOADING ARENAS
      FileConfiguration config = conffile("saves/save.yml");
      config.set("A", "#####NEVER MODIFY THIS FILE FROM HERE !#####");
      config.save(getDataFolder() + "/saves/save.yml");
      if (config.getConfigurationSection("Arenas") != null)
      {
        Set<String> keys = config.getConfigurationSection("Arenas").getKeys(false);
        for (String key : keys) {
          load(key);
        }
      }
      //
      //IMPLEMENTINGS SIGNS AND SHOP
      this.pm.registerEvents(new SignUtility(battleOfBlocks), battleOfBlocks);
      this.pm.registerEvents(shop, battleOfBlocks);
      //
      //IMPLEMENTINGS COMMANDS
      getCommand("battleofblocks").setExecutor(new Commands(battleOfBlocks));
      getCommand("batofb").setExecutor(new Commands(battleOfBlocks));
      //
      //RELOADING SIGNS
      SignUtility.updatesigns();
      //
      //LOADING DONE !!!
      getLogger().info("BattleOfBlocks's arenas " + Arenalist() + " loaded !");
    }
    catch (IOException e) {
      System.out.println("FATAL ERROR ON ENABLE !");
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
  
  public void reloadandremake()
    throws IOException
  {
    FileConfiguration config = conffile("saves/save.yml");
    if (config == null) {
      getLogger().severe("CONFIGURATION FILE = NULL ! (reloadandmake)");
      return;
    }
    config.set("Arenas", null);
    config.set("Signs", null);
    config.save(getDataFolder() + "/saves/save.yml");
    FileConfiguration config2 = getConfig();
    config2.set("Configuration", null);
    saveConfig();
    saveall();
  }
  
  public void save(Arena ar, String pos)
    throws IOException
  {
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
  
  public void saveall()
    throws IOException
  {
    addInf();
    savesigns();
    for (int i = 0; i < this.arenas.size(); i++) {
      Arena ar = (Arena)this.arenas.elementAt(i);
      String get = ar.getName();
      try
      {
        save(ar, "Arenas." + get);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
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
			  event.getPlayer().sendMessage(ChatColor.GREEN + "[BattleOfBlocks] A new great update of the plugin was downloaded !");
			  event.getPlayer().sendMessage(ChatColor.GREEN + "[BattleOfBlocks] Just reload the Plugin with the command '/reload' !!!");
		  }
	  }
  }
  
  public void load(String namearena)
  {
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
    conf.set("Configuration.Defaultlifes", Integer.valueOf(defaultlives));
    conf.set("Configuration.Defaultstart", Integer.valueOf(defaultstart));
    conf.set("Configuration.Defaultmax", Integer.valueOf(defaultmax));
    conf.set("Configuration.DefaultVip", Integer.valueOf(defaultvip));
    conf.set("Configuration.DefaultFacteurKills", Integer.valueOf(facteurkills));
    conf.set("Configuration.DefaultCoinsOnWin", Integer.valueOf(onwin));
    conf.set("Configuration.Timebeforestart", Integer.valueOf(time));
    conf.set("Configuration.CheatAdmin", Boolean.valueOf(cheatadmin));
    conf.set("Configuration.EconomyEnabled", Boolean.valueOf(ecoenabled));
    conf.set("Configuration.ControlName", controlname);
    conf.set("Configuration.AutoUpdate", Boolean.valueOf(update));
    conf.set("Configuration.RespawnTimeInSecs", Integer.valueOf(respawnTime));
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
      if (cs.isSet("CheatAdmin")) {
        cheatadmin = cs.getBoolean("CheatAdmin");
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
      if (cs.isSet("AutoUpdate")) {
        update = cs.getBoolean("AutoUpdate");
      }
      if (cs.isSet("EconomyEnabled")) {
    	  ecoenabled = cs.getBoolean("EconomyEnabled");
      }
      if (cs.isSet("RespawnTimeInSecs")) {
    	  respawnTime = cs.getInt("RespawnTimeInSecs");
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
  
}