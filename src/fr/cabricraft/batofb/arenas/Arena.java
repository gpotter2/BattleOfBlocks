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

package fr.cabricraft.batofb.arenas;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.SpawnEgg;
import org.bukkit.material.Wool;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import fr.cabricraft.batofb.BattleOfBlocks;
import fr.cabricraft.batofb.BattleOfBlocks.VersionBukkit;
import fr.cabricraft.batofb.economy.EconomyManager;
import fr.cabricraft.batofb.kits.BasicKits;
import fr.cabricraft.batofb.kits.ItemsKit;
import fr.cabricraft.batofb.kits.Kit;
import fr.cabricraft.batofb.signs.SignUtility;
import fr.cabricraft.batofb.util.BlockSave;
import fr.cabricraft.batofb.util.ParticleEffect;
import fr.cabricraft.batofb.util.TitleSender;
import fr.cabricraft.batofb.util.Verified;

public class Arena
  implements Listener, Verified {
  public List<Location> vblue;
  public List<Location> vred;
  public List<Player> playersingame = new LinkedList<Player>();
  private int detection;
  public int redlife;
  public int bluelife;
  public int life;
  public Location locend = null;
  public Location locstartred = null;
  public Location locstartblue = null;
  public Location waitroom = null;
  public int pmax;
  public int vip;
  public int startmin;
  public List<ItemStack> reward;
  private Player temp;
  private String arenaname;
  public boolean isstarted = false;
  public boolean iswaiting = false;
  public boolean canteleport = true;
  private int onwin;
  private int facteurkills;
  public BattleOfBlocks battleOfBlocks;
  public List<BlockSave> savebreak = new LinkedList<BlockSave>();
  public List<Location> saveplace = new LinkedList<Location>();
  public boolean canbuild = false;
  public boolean canbreak = false;
  
  private HashMap<UUID, Integer> player_list_money = new HashMap<UUID, Integer>();
  private HashMap<UUID, Integer> player_list_kills = new HashMap<UUID, Integer>();
  private HashMap<UUID, Integer> player_list_team = new HashMap<UUID, Integer>();
  private HashMap<UUID, String> player_list_counter = new HashMap<UUID, String>();
  private HashMap<UUID, String> player_list_names = new HashMap<UUID, String>();
  private HashMap<UUID, Boolean> player_list_dead = new HashMap<UUID, Boolean>();
  private HashMap<UUID, ItemStack[]> player_list_inv_storage = new HashMap<UUID, ItemStack[]>();
  
  private Scoreboard reset;
  
  public HashMap<UUID, String> p_kits = new HashMap<UUID, String>();
  
  public Arena(String arenaname, BattleOfBlocks plug, Location locstartred, Location locstartblue, Location locend, Location waitroom, List<Location> vred, List<Location> vblue, int life, int pmax, int pmin, int onwin, int facteurkills, int vip, List<ItemStack> reward, boolean canbuild, boolean canbreak) {
    this.arenaname = arenaname;
    this.battleOfBlocks = plug;
    this.vblue = vblue;
    this.vred = vred;
    this.locstartred = locstartred;
    this.locstartblue = locstartblue;
    this.locend = locend;
    this.waitroom = waitroom;
    this.redlife = life;
    this.bluelife = life;
    this.life = life;
    this.pmax = pmax;
    this.startmin = pmin;
    this.facteurkills = facteurkills;
    this.onwin = onwin;
    this.reward = reward;
    this.vip = vip;
    this.canbuild = canbuild;
    this.canbreak = canbreak;
    this.reset = Bukkit.getScoreboardManager().getNewScoreboard();
    checkBlocks();
  }
  
  @SuppressWarnings("deprecation")
private void checkBlocks(){
	  for(Location l : vblue){
		  Block b = l.getBlock();
		  if(b.getType() != Material.WOOL){
			  b.setType(Material.WOOL);
			  b.setData(DyeColor.BLUE.getData());
		  }
	  }
	  for(Location l : vred){
		  Block b = l.getBlock();
		  if(b.getType() != Material.WOOL){
			  b.setType(Material.WOOL);
			  b.setData(DyeColor.RED.getData());
		  }
	  }
  }
  
  public String PNC(){
		return ChatColor.RED + "[" + battleOfBlocks.controlname + "] " + ChatColor.RESET;
  }
  
  public void sendAll(String message)
  {
    for (int i = 0; i < playersingame.size(); i++) {
      Player p = (Player) playersingame.get(i);
      p.sendMessage(message);
    }
  }
  
  public String getName()
  {
    return arenaname;
  }
  
  public String getStatus(){
	  if(getConnectedPlayers() >= pmax) {
    	  return ChatColor.RED + "[Full]";
      } else if(isstarted == false && (vip + getConnectedPlayers()) >= pmax){
    	  return ChatColor.LIGHT_PURPLE + "[VIP]";
	  } else if(isstarted) {
		  return ChatColor.RED + "[Busy]";
	  } else {
		  return ChatColor.GREEN + "[Join]";
	  }
  }
  
  public boolean isinGame(Player player) {
	  return playersingame.contains(player);
  }
  
  public boolean isDead(Player player){
	  if(player_list_dead.containsKey(player.getUniqueId())){
		  return player_list_dead.get(player.getUniqueId());
	  }
	  return false;
  }
  
  public void restoreInventory(Player p){
    	p.getInventory().setContents(player_list_inv_storage.get(p.getUniqueId()));
  }
  
  public void saveInventory(Player p){
	  player_list_inv_storage.put(p.getUniqueId(), p.getInventory().getContents());
  }
  
  public int getteam(LivingEntity p){
	  int team = player_list_team.containsKey(p.getUniqueId()) ? player_list_team.get(p.getUniqueId()) : 0;
	  return team;
  }
  
  private Player hPlayerGame(int team){
	  List<Player> p_list_temp = new LinkedList<Player>();
	  for(Player p_temp : playersingame){
		  if(getteam(p_temp) == team){
			  p_list_temp.add(p_temp);
		  }
	  }
	  if(p_list_temp.size() == 1){
		  return p_list_temp.get(0);
	  } else {
		  int nombreAleatoire = (int)(Math.random() * (p_list_temp.size()));
		  return p_list_temp.get(nombreAleatoire);
	  }
  }
  
  public int getConnectedPlayers(){
	  return playersingame.size();
  }
  
  public int nbrblue(){
	  int nbr = 0;
	  for(Player p : playersingame){
		  if(getteam(p) == 1){
			  nbr = nbr + 1;
		  }
	  }
	  return nbr;
  }
  
  public int nbrred(){
	  int nbr = 0;
	  for(Player p : playersingame){
		  if(getteam(p) == 2){
			  nbr = nbr + 1;
		  }
	  }
	  return nbr;
  }
  
  private void checkteam(){
	  if(nbrblue() < ((int) (getConnectedPlayers()/2))){
		  while(nbrblue() < ((int) (getConnectedPlayers()/2))){
			  Player p = hPlayerGame(2);
			  p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.TEAM_FULL));
			  setTeam(1, p);
		  }
	  } else if(nbrred() < ((int) (getConnectedPlayers()/2))){
		  while(nbrred() < ((int) (getConnectedPlayers()/2))){
			  Player p = hPlayerGame(1);
			  p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.TEAM_FULL));
			  setTeam(2, p);
		  }
	  }
  }
  
  private void teleportStart(){
		for (int i = 0; i < playersingame.size(); i++){
		      Player pp = (Player)playersingame.get(i);
		      if (getteam(pp) == 0) {
		    	  chooseteamh(pp);
		      }
	    }
	    checkteam();
	    settalkbarre();
	    canchangeweather = false;
	    locstartred.getWorld().setPVP(true);
	    locstartred.getWorld().setTime(0L);
	    locstartred.getWorld().setStorm(false);
	    locstartred.getWorld().setThundering(false);
	    canchangeweather = true;
	    canteleport = true;
	    for (int i = 0; i < playersingame.size(); i++){
		      Player pp = (Player)playersingame.get(i);
		      player_list_names.put(pp.getUniqueId(), pp.getPlayerListName());
		      int t = getteam(pp);
		      player_list_kills.put(pp.getUniqueId(), 0);
		      if (t == 1) {
		    	  	pp.teleport(locstartblue);
		        	if(pp.getName().length() <= 14) pp.setPlayerListName(ChatColor.BLUE + pp.getName());
		        	pp.setCustomNameVisible(true);
		      } else if (t == 2) {
		    	  	pp.teleport(locstartred);
		    	  	if(pp.getName().length() <= 14) pp.setPlayerListName(ChatColor.RED + pp.getName());
		    	  	pp.setCustomNameVisible(true);
		      }
		      clearInventory(pp);
		      settunic(pp, false);
		      InstaureCountDown(pp, setStuff(pp));
	    }
	    isstarted = true;
	    iswaiting = false;
	    canteleport = false;
	    SignUtility.updateSigns();
  }
  
  private void teleportEnd(int winner){
      for (int i = 0; i < playersingame.size(); i++) {
        Player pp = (Player) playersingame.get(i);
        pp.teleport(locend);
        removeCountDownAndPotions(pp);
        clearInventory(pp);
        resetScoreboard(pp);
        if (battleOfBlocks.barapienabled) {
        	try {
        		BarAPI.removeBar(pp);
        	} catch (Exception e) {
    			Bukkit.getLogger().severe("BattleOfBlocks: Error while using BarAPI ! If you are using spigot 1.8, this may happend. Disabling BarAPI !!!");
    			battleOfBlocks.barapienabled = false;
    		}
        }
        restoreInventory(pp);
        removeLastEssentialsLocation(pp);
        if(getteam(pp) == winner) {
	        for (int i2 = 0; i2 < reward.size(); i2++) {
	          ItemStack isr = (ItemStack) reward.get(i2);
	          if (isr != null) {
	            pp.getInventory().addItem(isr);
	          }
	        }
        }
      }
  }
  
  public void setdetection(int i) {
    detection = i;
  }
  
  public void settemp(Player p)
  {
    temp = p;
  }
  
  public void setstartblue(Location loc)
  {
    locstartblue = loc;
  }
  
  public void setstartred(Location loc)
  {
    locstartred = loc;
  }
  
  public void setwaitroom(Location loc)
  {
    waitroom = loc;
  }
  
  @SuppressWarnings("deprecation")
private void setScoreBoard(Player p) {
	  Scoreboard board;
	  Objective objective;
	  Objective objective_lifes;
	  ScoreboardManager manager = Bukkit.getScoreboardManager();
	  board = manager.getNewScoreboard();
	  objective = board.registerNewObjective("kills", "dummy");
	  Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Kills:"));
	  score.setScore(0);
	  Score kcoins = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "KillCoins:"));
	  kcoins.setScore(0);
	  if(battleOfBlocks.vaultenabled_economy) {
		  Score money = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Money:"));
		  money.setScore(0);
	  }
	  objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	  objective_lifes = board.registerNewObjective("lifes", "health");
	  objective_lifes.setDisplaySlot(DisplaySlot.BELOW_NAME);
	  objective_lifes.setDisplayName(ChatColor.RED + "\u2764");
	  p.setScoreboard(board);
  }
  
  @SuppressWarnings("deprecation")
public void updateScoreboard(Player p) {
	  Scoreboard scb = p.getScoreboard();
	  Objective obj = scb.getObjective("kills");
	  if(obj == null){
		  obj = scb.registerNewObjective("kills", "dummy");
	  }
	  if(battleOfBlocks.controlname == null) battleOfBlocks.controlname = "BattleOfBlocks";
	  obj.setDisplayName(ChatColor.BLUE + "" + ChatColor.MAGIC + "bob" + ChatColor.RESET + battleOfBlocks.controlname + ChatColor.RED + "" + ChatColor.MAGIC + "bob");
	  Score score = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Kills:"));
	  score.setScore(nbrkills(p));
	  Score kcoins = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "KillCoins:"));
	  int killcoins = getmoney(p);
	  kcoins.setScore(killcoins);
	  if(battleOfBlocks.vaultenabled_economy) {
		  Score money = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Money:"));
		  int mmm = nbrkills(p);
			if(battleOfBlocks.hasPermission(p, "battleofblocks.scoreX5")) {
				mmm = mmm*5;
			} else if(battleOfBlocks.hasPermission(p, "battleofblocks.scoreX4")) {
				mmm = mmm*4;
			} else if(battleOfBlocks.hasPermission(p, "battleofblocks.scoreX3")) {
				mmm = mmm*3;
			} else if(battleOfBlocks.hasPermission(p, "battleofblocks.scoreX2")) {
				mmm = mmm*2;
			}
		  money.setScore(mmm * facteurkills);
	  }
	  p.setScoreboard(scb); 
  }
  
  private void resetScoreboard(Player p) {
	  p.setPlayerListName(player_list_names.get(p.getUniqueId()));
	  p.setScoreboard(reset);
  }
  
  private void resetScoreboard() {
	  for(Player p : playersingame){
		  resetScoreboard(p);
	  }
  }
  
  public void setend(Location loc) {
    locend = loc;
  }
  
  public void setmode(Player p) {
    p.setGameMode(GameMode.SURVIVAL);
  }
  
  private void makeCompletlyInvisible(Player p){
	  for(Player pl : playersingame){
		  pl.hidePlayer(p);
	  }
  }
  private void makeCompletlyVisible(Player p){
	  for(Player pl : playersingame){
		  pl.showPlayer(p);
	  }
  }
  private void makeDeadLooking(Player p, Player killer){
	  if(killer != null){
		  	Vector direction = p.getEyeLocation().toVector().subtract(killer.getEyeLocation().toVector());
		    double x = direction.getX();
		    double y = direction.getY();
		    double z = direction.getZ();
		    Location changed = p.getLocation().clone();
		    changed.setYaw(180 - (float) Math.toDegrees(Math.atan2(x, z)));
		    changed.setPitch(90 - (float) Math.toDegrees(Math.acos(y)));
		    canteleport = true;
		    p.teleport(changed);
		    canteleport = false;
	  }
	  p.playNote(p.getLocation(), Instrument.BASS_GUITAR, Note.natural(1, Tone.E));
	  p.playNote(p.getLocation(), Instrument.SNARE_DRUM, Note.natural(1, Tone.C));
	  p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 100));
	  p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, -100));
	  if(BattleOfBlocks.version == VersionBukkit.V1_8){
		  	try {
				if(killer != null){
					TitleSender.sendTitleAndSubTitle(p, "{text:'" + battleOfBlocks.msg.putColorRemovePNC(battleOfBlocks.msg.YOU_DIE) + "',color:red}", "{text:'Killed by ',color:yellow,extra:[{text:'" + killer.getName() + "',color:red}]}", 10, ((battleOfBlocks.respawnTime*20) - 3), 10);
				} else {
					TitleSender.sendTitleAndSubTitle(p, "{text:'" + battleOfBlocks.msg.putColorRemovePNC(battleOfBlocks.msg.YOU_DIE) + "',color:red}", "{text:'Sucid !',color:yellow}", 10, ((battleOfBlocks.respawnTime*20) - 3), 10);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	  }
  }
  
  public void returntothespawn(final Player p, boolean die, Player killer){
	if(battleOfBlocks.respawnTime == 0){
	    int t = getteam(p);
	    canteleport = true;
	    if (t == 1) {
	    	p.teleport(locstartblue);
	    } else if (t == 2) {
	    	p.teleport(locstartred);
	    }
	    removePotions(p);
	    canteleport = false;
	    clearInventory(p);
	    setStuff(p);
	    settunic(p, false);
	    p.setHealth(p.getMaxHealth());
	    p.setFoodLevel(20);
	    if (die) {
	      if (killer != null) {
	        p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.structurate(battleOfBlocks.msg.KILLED_BY, p, killer)));
	      } else {
	        p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.YOU_DIE));
	      }
	    }
	} else {
		if (die) {
			  removePotions(p);
			  p.setHealth(p.getMaxHealth());
			  p.setFoodLevel(20);
		      if (killer != null) {
			        p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.structurate(battleOfBlocks.msg.KILLED_BY, p, killer)));
			        p.sendMessage(PNC() + ChatColor.RED + "Respawn: " + (battleOfBlocks.respawnTime) + " secs !");
			        makeCompletlyInvisible(p);
			        makeDeadLooking(p, killer);
			        player_list_dead.put(p.getUniqueId(), true);
		      } else {
		    	  	p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.YOU_DIE));
			        p.sendMessage(PNC() + ChatColor.RED + "Respawn: " + (battleOfBlocks.respawnTime) + " secs !");
			        makeCompletlyInvisible(p);
			        makeDeadLooking(p, null);
			        player_list_dead.put(p.getUniqueId(), true);
		      }
		      new BukkitRunnable() {
				@Override
				public void run() {
					if(isstarted && isinGame(p)){
						int t = getteam(p);
						makeCompletlyVisible(p);
						canteleport = true;
						if (t == 1) {
							p.teleport(locstartblue);
						} else if (t == 2) {
							p.teleport(locstartred);
						}
						canteleport = false;
						player_list_dead.put(p.getUniqueId(), false);
						clearInventory(p);
						setStuff(p);
						settunic(p, false);
						removePotions(p);
						p.setHealth(p.getMaxHealth());
					}
				}
			}.runTaskLater(battleOfBlocks, battleOfBlocks.respawnTime * 20);
		} else {
			int t = getteam(p);
			canteleport = true;
		    if (t == 1) {
		      p.teleport(locstartblue);
		    } else if (t == 2) {
		      p.teleport(locstartred);
		    }
		    canteleport = false;
		}
	}
  }
  
  public void updateBarWaitting(int time_restant, int time_finish){
	  if(battleOfBlocks.barapienabled){
		  for(Player p : playersingame){
		  	String message = PNC() + ChatColor.WHITE + ": " + ChatColor.GOLD + time_restant;
			Float percent = 100 - (pourcentage((time_finish - time_restant), time_finish));
			try {
				BarAPI.setMessage(p, message, percent);
			} catch (Exception e) {
				Bukkit.getLogger().severe("BattleOfBlocks: Error while using BarAPI ! If you are using spigot 1.8, this may happend. Disabling BarAPI !!!");
				battleOfBlocks.barapienabled = false;
				break;
			}
		  }
	  }
  }
  
  private void InstaureCountDown(Player p, List<ItemStack> l){
	  long now = System.currentTimeMillis();
	  String to_set = null;
	  for(ItemStack is : l){
		  String itemname = is.getItemMeta().getDisplayName().substring(2);
		  if(to_set == null){
			  to_set = itemname + "#" + now;
		  } else {
			  to_set = to_set + "@" + itemname + "#" + now;
		  }
	  }
	  player_list_counter.put(p.getUniqueId(), to_set);
  }
  
  private void removeCountDownAndPotions(Player p){
	  player_list_counter.remove(p.getUniqueId());
	  removePotions(p);
  }
  
  @EventHandler
  public void infiniteDispensers(BlockDispenseEvent event){
  	if(event.getBlock().getState() instanceof Dispenser){
  		Dispenser d = (Dispenser) event.getBlock().getState();
  		Location l1 = d.getLocation();
  		l1.setX(l1.getX() + 1);
  		Location l2 = d.getLocation();
  		l2.setX(l2.getX() - 1);
  		Location l3 = d.getLocation();
  		l3.setZ(l3.getZ() + 1);
  		Location l4 = d.getLocation();
  		l4.setZ(l4.getZ() - 1);
  		if(l1.getBlock().getType() == Material.SIGN || l1.getBlock().getType() == Material.SIGN_POST || l1.getBlock().getType() == Material.WALL_SIGN){
  			Sign s = (Sign) l1.getBlock().getState();
  			if(s.getLine(0).equals(ChatColor.BLUE + "[BatOfBDis]")){
  				d.getInventory().addItem(event.getItem());
  			}
  		} else if(l2.getBlock().getType() == Material.SIGN || l2.getBlock().getType() == Material.SIGN_POST || l2.getBlock().getType() == Material.WALL_SIGN){
  			Sign s = (Sign) l2.getBlock().getState();
  			if(s.getLine(0).equals(ChatColor.BLUE + "[BatOfBDis]")){
  				d.getInventory().addItem(event.getItem());
  			}
  		} else if(l3.getBlock().getType() == Material.SIGN || l3.getBlock().getType() == Material.SIGN_POST || l3.getBlock().getType() == Material.WALL_SIGN){
  			Sign s = (Sign) l3.getBlock().getState();
  			if(s.getLine(0).equals(ChatColor.BLUE + "[BatOfBDis]")){
  				d.getInventory().addItem(event.getItem());
  			}
  		} else if(l4.getBlock().getType() == Material.SIGN || l4.getBlock().getType() == Material.SIGN_POST || l4.getBlock().getType() == Material.WALL_SIGN){
  			Sign s = (Sign) l4.getBlock().getState();
  			if(s.getLine(0).equals(ChatColor.BLUE + "[BatOfBDis]")){
  				d.getInventory().addItem(event.getItem());
  			}
  		}
  	}
  }
  
  @EventHandler
  public void noOpenDispensers(InventoryOpenEvent event){
  	if(isstarted || iswaiting){
  		if(event.getPlayer() instanceof Player){
	        	Player p = (Player) event.getPlayer();
	        	if(event.getInventory().getType() == InventoryType.DISPENSER){
		        	if(isinGame(p)){
			       		event.setCancelled(true);
			       	}
	        	}
      	}
  	}
  }
  
  private void removePotions(Player p){
	  p.setFireTicks(0);
	  for (PotionEffect effect : p.getActivePotionEffects())
	        p.removePotionEffect(effect.getType());
  }
  
  public void setCountDown(Player p, String name) {
	  long now = System.currentTimeMillis();
	  String get = player_list_counter.get(p.getUniqueId());
	  String[] gets = get.split("@");
	  String to_set = null;
	  for(String g : gets){
		  String[] parts = g.split("#");
		  try {
			  String m_get = parts[0];
			  if(m_get.equalsIgnoreCase(name)){
				  	if(to_set == null){
						to_set = parts[0] + "#" + now;
					} else {
						to_set = to_set + "@" + parts[0] + "#" + now;
					}
			  } else {
				  if(to_set == null){
						to_set = g;  
					} else {
						to_set = to_set + "@" + g;
					}
			  }
		  } catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  player_list_counter.put(p.getUniqueId(), to_set);
  }
  
  public boolean canUsePower(Player p, String name, int seconds){
	  long now = System.currentTimeMillis();
	  String get = player_list_counter.get(p.getUniqueId());
	  if(get == null){
		  return false;
	  }
	  String[] gets = get.split("@");
	  long last = 0;
	  for(String g : gets){
		  String[] parts = g.split("#");
		  try {
			  String m_get = parts[0];
			  if(m_get.equalsIgnoreCase(name)){
				  last = new Long(parts[1]);
			  }
		  } catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  int delay = seconds * 1000;
	  if((now - last) >= delay){
		  return true;
	  } else {
		  return false;
	  }
  }
  
  public int timeBeforeUse(Player p, String name, int seconds){
	  long now = System.currentTimeMillis();
	  String get = player_list_counter.get(p.getUniqueId());
	  String[] gets = get.split("@");
	  long last = 0;
	  for(String g : gets){
		  String[] parts = g.split("#");
		  try {
			  String m_get = parts[0];
			  if(m_get.equalsIgnoreCase(name)){
				  last = new Long(parts[1]);
			  }
		  } catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  int delay = seconds * 1000;
	  long milis = delay - (now - last);
	  p.playSound(p.getLocation(), Sound.ENDERMAN_HIT, 1, 1);
	  return Math.round(milis / 1000);
  }
  
  @SuppressWarnings("deprecation")
public void settunic(Player p, boolean fake) {
    ItemStack it = new ItemStack(Material.LEATHER_CHESTPLATE);
    LeatherArmorMeta lm = (LeatherArmorMeta) it.getItemMeta();
    lm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
    lm.addEnchant(Enchantment.DURABILITY, 5, true);
    int t = getteam(p);
    if (t == 1) {
      if(fake){
    	  lm.setColor(Color.RED);
      }
      else{
    	  lm.setColor(Color.BLUE);
      }
    } else if (t == 2) {
    	if(fake){
    		lm.setColor(Color.BLUE);
    	} else {
        	lm.setColor(Color.RED);
        }
    }
    it.setItemMeta(lm);
    p.getInventory().setChestplate(it);
    p.updateInventory();
  }
  
  public void removeall() {
    playersingame.clear();
    player_list_money = new HashMap<UUID, Integer>();
    player_list_kills = new HashMap<UUID, Integer>();
    player_list_team = new HashMap<UUID, Integer>();
    player_list_counter = new HashMap<UUID, String>();
    player_list_names = new HashMap<UUID, String>();
    player_list_dead = new HashMap<UUID, Boolean>();
  }
  
  public void stopthegame(int Looser){  
	int Winner = 0;
    if (Looser == 2) {
      sendAll(ChatColor.YELLOW + "-------------------------------------------");
      sendAll(battleOfBlocks.msg.putColor(battleOfBlocks.msg.BLUE_WIN));
      launchfireworks(1);
      updatebarwin(1);
      Winner = 1;
    } else if (Looser == 1) {
      sendAll(ChatColor.YELLOW + "-------------------------------------------");
      sendAll(battleOfBlocks.msg.putColor(battleOfBlocks.msg.RED_WIN));
      launchfireworks(2);
      updatebarwin(2);
      Winner = 2;
    }
    if (battleOfBlocks.vaultenabled_economy) {
      for (int i = 0; i < playersingame.size(); i++)
      {
        Player p = (Player)playersingame.get(i);
        int moneyearn = nbrkills(p) * facteurkills;
        if (getteam(p) != Looser)
        {
	    	int money = onwin;
	  		if(battleOfBlocks.hasPermission(p, "battleofblocks.scoreX5")) {
				money = money * 5;
			} else if(battleOfBlocks.hasPermission(p, "battleofblocks.scoreX4")) {
				money = money * 4;
			} else if(battleOfBlocks.hasPermission(p, "battleofblocks.scoreX3")) {
				money = money * 3;
			} else if(battleOfBlocks.hasPermission(p, "battleofblocks.scoreX2")) {
				money = money * 2;
			}
	  		moneyearn += money;
	  		new EconomyManager(p, moneyearn, battleOfBlocks);
        }
        else
        {
          new EconomyManager(p, moneyearn, battleOfBlocks);
        }
        clearInventory(p);
        p.sendMessage(ChatColor.YELLOW + "-------------------------------------------");
      }
    } else {
      sendAll(ChatColor.YELLOW + "-------------------------------------------");
    }
    final Arena arena_temp = this;
    final int option_temp = Winner;
    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(battleOfBlocks, new Runnable() {
    	final Arena arena = arena_temp;
    	final int option = option_temp;
		@Override
		public void run() {
			arena.finishthegame(option);
		}
	}, 200);
    resetScoreboard();
    resetArena();
  }
  
  public BukkitRunnable arenaChrono(final Arena arena, final int option){
	  return new BukkitRunnable() {
		int time = BattleOfBlocks.time;
		@Override
		public void run() {
			  int sec = new Integer(time);
			  for(int i = 0; i < time; i++){
				  if(arena.battleOfBlocks.stop){
					  break;
				  }
				  arena.updateBarWaitting(sec, time);
				  if(sec % 10 == 0 || sec < 11) arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structurateTime(arena.battleOfBlocks.msg.GAME_START_IN_X_SECONDS, sec)));
				  sec--;
				  if(arena.getConnectedPlayers() == 0 || !arena.iswaiting || arena.getConnectedPlayers() < arena.startmin) {
					  arena.updatebarjoin();
					  break;
				  }
				  try {
					Thread.sleep(1000);
				  } catch (InterruptedException e) {
						e.printStackTrace();
				  }
			  }
		}
	};
  }
  
  public void startthegame(){
	  if(!isstarted){
		  teleportStart();
	  }
  }
  
private void removeLastEssentialsLocation(Player p){
	  if(battleOfBlocks.essentials_enabled){
	    	battleOfBlocks.essentials.getUser(p).setLastLocation(p.getLocation());
	  }
  }
  
public void finishthegame(int winner) {
	redlife = life;
    bluelife = life;
    isstarted = false;
    canteleport = true;
    
    teleportEnd(winner);
    if(winner == 0) sendAll(PNC() + ChatColor.RED + "All the players left the game ! The game was stopped !");
    sendAll(PNC() + ChatColor.GREEN + "Thanks for playing ! Plugin by" + ChatColor.BOLD + " gpotter2  " + ChatColor.RESET + ChatColor.GREEN + "!");
    removeall();
    SignUtility.updateSigns();
}
  
@SuppressWarnings("deprecation")
private void resetArena(){
	  if(canbuild){
		  for(int i = 0; i < saveplace.size(); i++) {
			  Location l = saveplace.get(i);
			  Block b = l.getBlock();
			  b.setType(Material.AIR);
		  }
		  for(int i = 0; i < savebreak.size(); i++) {
			  BlockSave sb = savebreak.get(i);
			  Block b = sb.l.getBlock();
			  b.setType(sb.m);
			  b.setData(sb.d);
		  }
		  saveplace.clear();
		  savebreak.clear();
		  List<Entity> le = locstartblue.getWorld().getEntities();
		  for(Entity e : le){
			 if(e instanceof Item){
				 e.remove();
			 }
		  }
	  }
		for(int i = 0; i < vblue.size(); i++) {
			Block b = vblue.get(i).getBlock();
			b.setType(Material.WOOL);
	        b.setData(DyeColor.BLUE.getData());
		}
		for(int i = 0; i < vred.size(); i++) {
			Block b = vred.get(i).getBlock();
			b.setType(Material.WOOL);
	        b.setData(DyeColor.RED.getData());
		}
  }
  
  public void disconnect(Player player) {
    if (battleOfBlocks.barapienabled) {
    	try {
    		BarAPI.removeBar(player);
    	} catch (Exception e) {
			Bukkit.getLogger().severe("BattleOfBlocks: Error while using BarAPI ! If you are using spigot 1.8, this may happend. Disabling BarAPI !!!");
			battleOfBlocks.barapienabled = false;
		}
    }
    removeCountDownAndPotions(player);
    playersingame.remove(player);
    if (getConnectedPlayers() <= 1) {
    	if(isstarted || iswaiting){
    		finishthegame(0);
    	}
    } else {
    	player.sendMessage(PNC() + ChatColor.GREEN + "Thanks for playing ! Plugin by gpotter2 !");
    	SignUtility.updateSigns();
    }
    clearInventory(player);
    restoreInventory(player);
    resetScoreboard(player);
    removeLastEssentialsLocation(player);
    player.teleport(locend);
    sendAll(battleOfBlocks.msg.putColor(battleOfBlocks.msg.structurate(battleOfBlocks.msg.OTHER_LEFT_THE_GAME, player, null)));
  }
  
  private void settalkbarre(){
    if (battleOfBlocks.barapienabled) {
      for (int i = 0; i < playersingame.size(); i++){
        Player p = (Player) playersingame.get(i);
        try {
	        if (getteam(p) == 1) {
	        	BarAPI.setMessage(p, ChatColor.BLUE + "BLUE : " + bluelife + ChatColor.RESET + " , " + ChatColor.RED + "RED : " + redlife, pourcentage(bluelife, life));
	        } else {
	        	BarAPI.setMessage(p, ChatColor.BLUE + "BLUE : " + bluelife + ChatColor.RESET + " , " + ChatColor.RED + "RED : " + redlife, pourcentage(redlife, life));
	        }
        } catch (Exception e) {
			Bukkit.getLogger().severe("BattleOfBlocks: Error while using BarAPI ! If you are using spigot 1.8, this may happend. Disabling BarAPI !!!");
			battleOfBlocks.barapienabled = false;
			break;
		}
      }
    }
  }
  
  public void updatebarjoin()
  {
    if (battleOfBlocks.barapienabled) {
      for (int i = 0; i < playersingame.size(); i++)
      {
        Player p = (Player)playersingame.get(i);
        try {
	        if ((startmin < getConnectedPlayers()) || (startmin == getConnectedPlayers())) {
	          BarAPI.setMessage(p, ChatColor.GREEN + "" + getConnectedPlayers() + "/" + pmax + ChatColor.GOLD + ", " + battleOfBlocks.msg.NEED + ":" + startmin, 100.0F);
	        } else {
	          BarAPI.setMessage(p, ChatColor.GREEN + "" + getConnectedPlayers() + "/" + pmax + ChatColor.GOLD + ", " + battleOfBlocks.msg.NEED + ":" + startmin, pourcentage(getConnectedPlayers(), startmin));
	        }
        } catch (Exception e) {
			Bukkit.getLogger().severe("BattleOfBlocks: Error while using BarAPI ! If you are using spigot 1.8, this may happend. Disabling BarAPI !!!");
			battleOfBlocks.barapienabled = false;
			break;
		}
      }
    }
  }
  
  public void updatebarwin(int winner)
  {
    if (battleOfBlocks.barapienabled) {
      for (int i = 0; i < playersingame.size(); i++)
      {
        Player p = (Player)playersingame.get(i);
        try {
	        if (winner == 1) {
	          BarAPI.setMessage(p, ChatColor.MAGIC + "batb  " + ChatColor.BLUE + battleOfBlocks.msg.putColor(battleOfBlocks.msg.BLUE_WIN).replaceAll(PNC(), "") + ChatColor.MAGIC + "  batb");
	        } else {
	          BarAPI.setMessage(p, ChatColor.MAGIC + "batb  " + ChatColor.RED + battleOfBlocks.msg.putColor(battleOfBlocks.msg.RED_WIN).replaceAll(PNC(), "") + ChatColor.MAGIC + "  batb");
	        }
        } catch (Exception e) {
			Bukkit.getLogger().severe("BattleOfBlocks: Error while using BarAPI ! If you are using spigot 1.8, this may happend. Disabling BarAPI !!!");
			battleOfBlocks.barapienabled = false;
			break;
		}
      }
    }
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
  
  public void join(Player player) {
    if (battleOfBlocks.hasPermission(player, "battleofblocks.play") || battleOfBlocks.playbydefault) {
      if (!isstarted) {
        if (!isinGame(player)) {
          if (getConnectedPlayers() <= pmax) {
	        	player.setMaxHealth(20);
	            player.setHealth(player.getMaxHealth());
	            player.setFoodLevel(20);
	            makeCompletlyVisible(player);
	            setmode(player);
	            saveInventory(player);
	            playersingame.add(player);
	            iswaiting = true;
	            sendAll(battleOfBlocks.msg.structurate(battleOfBlocks.msg.putColor(battleOfBlocks.msg.OTHER_JOIN_THE_GAME), player, null) + ChatColor.GREEN + "(" + getConnectedPlayers() + "/" + pmax + ")");
	            SignUtility.updateSigns();
	            canteleport = true;
	            player.teleport(waitroom);
	            canteleport = false;
	            updatebarjoin();
	            player.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.YOU_JOINED_THE_GAME));
	            setmoney(player, 0);
	            clearInventory(player);
	            setScoreBoard(player);
	            updateScoreboard(player);
	            setInventorySelect(player);
	            setKitChooser(player);
	            if (getConnectedPlayers() == startmin) {
	            	arenaChrono(this, 0).runTaskAsynchronously(battleOfBlocks);
	            	final Arena arena_temp = this;
	            	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(battleOfBlocks, new Runnable() {
	                	final Arena arena = arena_temp;
	            		@Override
	            		public void run() {
	            			if(arena.getConnectedPlayers() == 0 || !arena.iswaiting || arena.getConnectedPlayers() < arena.startmin) {
	            				  return;
	            			  }
	            			  arena.sendAll(arena.PNC() + ChatColor.GREEN + "The game started !");
	            			  arena.startthegame();
	            		}
	            	}, (BattleOfBlocks.time * 20));
	            }
          } else {
        	  player.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.TOO_MANY_PEOPLE));
          }
        } else {
        	player.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.ALREADY_IN_GAME));
        	player.teleport(waitroom);
        }
      } else {
    	if(getConnectedPlayers() == 0) {
    		finishthegame(0);
    	}
        player.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.GAME_ALREADY_STARTED));
      }
    } else {
      player.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.PERMISSION_DENIED));
    }
  }
  
  public void setTeam(int t, Player p) {
    if (t == 1) {
	  player_list_team.put(p.getUniqueId(), t);
      p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.YOU_TEAM_BLUE));
      clearInventory(p);
      setKitChooser(p);
      setInventorySelect(p);
    } else if (t == 2) {
	  player_list_team.put(p.getUniqueId(), t);
      p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.YOU_TEAM_RED));
      clearInventory(p);
      setKitChooser(p);
      setInventorySelect(p);
    }
  }
  
  public String getKitName(Player p){
	  return p_kits.get(p.getUniqueId());
  }
  
  @SuppressWarnings("deprecation")
private void setInventorySelect(Player p)
  {
    Wool wool1 = new Wool(DyeColor.BLUE);
    ItemStack stack1 = wool1.toItemStack(1);
    Wool wool2 = new Wool(DyeColor.RED);
    ItemStack stack2 = wool2.toItemStack(1);
    ItemMeta im1 = stack1.getItemMeta();
    ItemMeta im2 = stack2.getItemMeta();
    im1.setDisplayName("BLUE TEAM !");
    im2.setDisplayName("RED TEAM !");
    stack1.setItemMeta(im1);
    stack2.setItemMeta(im2);
    p.getInventory().addItem(stack1);
    p.getInventory().addItem(stack2);
    
    p.updateInventory();
  }
  
  @SuppressWarnings("deprecation")
private void setKitChooser(Player p){
	  p.getInventory().setItem(8, getIS(Material.NETHER_STAR, "Kit chooser", "Choose your kit", null, p));
	  if(battleOfBlocks.vaultenabled_economy){
		  p.getInventory().setItem(7, getIS(Material.EMERALD, "OutShop", "Shop", null, p));
	  }
	  p.updateInventory();
  }
  
  @SuppressWarnings("deprecation")
private List<ItemStack> setStuff(Player p) {
    ItemStack nuggetg = new ItemStack(Material.EMERALD);
    ItemMeta im = nuggetg.getItemMeta();
    im.setDisplayName("POWERUPS");
    nuggetg.setItemMeta(im);
    p.getInventory().setMaxStackSize(999);
    p.getInventory().setItem(8, nuggetg);
    String kp = getKitName(p);
    List<ItemStack> listi = new BasicKits(this).getItemsPlayer(p);
    if(listi != null){
    	for(ItemStack is : listi){
    		p.getInventory().addItem(is);
    	}
    } else {
    	if(battleOfBlocks.kits != null) {
	    	List<Kit> v = battleOfBlocks.kits.v;
			for(int i = 0; i < v.size(); i++) {
				Kit k = v.get(i);
				if(k.name == kp) {
					List<ItemsKit> v2 = k.v;
					for(int i2 = 0; i2 < v2.size(); i2++) {
						ItemsKit ik = v2.get(i2);
						p.getInventory().addItem(ik.is);
					}
					break;
				}
			}
    	}
	}
    p.updateInventory();
    return listi;
  }
  
  private float pourcentage(float a, float b)
  {
    float percent = a / b * 100.0F;
    return percent;
  }
  
  private void launchfireworks(int winner)
  {
    for (int i = 0; i < playersingame.size(); i++) {
      Player p = (Player)playersingame.get(i);
      if (getteam(p) == winner)
      {
        Firework firework = (Firework)p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(1);
        if (winner == 1) {
          meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.BLUE).withFade(Color.GREEN).withFlicker().withTrail().build());
        } else {
          meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withFade(Color.ORANGE).withFlicker().withTrail().build());
        }
        firework.setFireworkMeta(meta);
      }
    }
  }
  
  @SuppressWarnings("deprecation")
private void clearInventory(Player p)
  {
    p.getInventory().clear();
    p.getInventory().setChestplate(null);
    p.getInventory().setBoots(null);
    p.getInventory().setHelmet(null);
    p.getInventory().setLeggings(null);
    p.updateInventory();
  }
  
  private void chooseteamh(CommandSender pl)
  {
    Player p = (Player)pl;
    if (nbrblue() == nbrred()) {
      int h = (int)(Math.random() * 2.0D);
      if (h == 1) {
        setTeam(1, p);
      } else {
        setTeam(2, p);
      }
    }
    else if (nbrblue() < nbrred()) {
      setTeam(1, p);
    }
    else
    {
      setTeam(2, p);
    }
  }
  
  public boolean canpay(Player p, int price)
  {
    int money = getmoney(p);
    if (money > price) {
      return true;
    }
    if (money == price) {
      return true;
    }
    return false;
  }
  
  public int getmoney(Player p){
	  int result = player_list_money.containsKey(p.getUniqueId()) ? player_list_money.get(p.getUniqueId()) : 0;
	  return result;
  }
  
  public void setmoney(Player p, int m){
	  player_list_money.put(p.getUniqueId(), m);
  }
  
  public void pay(Player p, int price)
  {
    setmoney(p, getmoney(p) - price);
    updateScoreboard(p);
  }
  
  public int nbrkills(Player p) {
	  int result = player_list_kills.containsKey(p.getUniqueId()) ? player_list_kills.get(p.getUniqueId()) : 0;
	  return result;
  }
  
  void addnbrkills(Player p) {
	  player_list_kills.put(p.getUniqueId(), Integer.valueOf(nbrkills(p) + 1));
	  updateScoreboard(p);
  }
  
  public void addmoney(int t) {
    for (int i = 0; i < playersingame.size(); i++) {
      Player p = (Player) playersingame.get(i);
      if (getteam(p) == t) {
    	int money = 10;
        int actm = getmoney(p);
        setmoney(p, Integer.valueOf(actm + money));
        p.sendMessage(ChatColor.GOLD + "+" + money + " golds");
        updateScoreboard(p);
      }
    }
  }
  
  public void addmoney(Player k) {
	int money = 10;
    int actm = getmoney(k);
    setmoney(k, Integer.valueOf(actm + money));
    k.sendMessage(ChatColor.GOLD + "+" + money + " golds");
    addnbrkills(k);
  }
  
  @EventHandler
	public void manageInteract(PlayerInteractEvent event){
	  if(iswaiting){
		  if(event.getItem() != null){
			  ItemStack is = event.getItem();
			  if(is.getType() == Material.NETHER_STAR){
				  Player p = event.getPlayer();
				  if(isinGame(p)){
					  p.openInventory(getInvChooseKit(p, arenaname));
				  }
			  } else if(is.getType() == Material.EMERALD){
				  Player p = event.getPlayer();
				  if(isinGame(p)){
					  if(is.getItemMeta().getDisplayName().substring(2).equalsIgnoreCase("OutShop")){
						  battleOfBlocks.shop.startShop(p);
					  }
				  }
			  } else if(is.getType() == Material.WOOL){
					Player p = event.getPlayer();
				    if (isinGame(p)) {
				    	if(battleOfBlocks.hasPermission(p, "battleofblocks.chooseteam")) {
					        @SuppressWarnings("deprecation")
							Byte b1 = Byte.valueOf(DyeColor.BLUE.getData());
					        @SuppressWarnings("deprecation")
							Byte b2 = Byte.valueOf(DyeColor.RED.getData());
					        @SuppressWarnings("deprecation")
							Byte b_get = is.getData().getData();
					        if (b_get.equals(b1)) {
					          setTeam(1, p);
					          event.setCancelled(true);
					        } else if (b_get.equals(b2)) {
					        	setTeam(2, p);
					            event.setCancelled(true);   
					        }
				    	} else {
							p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.PERMISSION_DENIED));
							event.setCancelled(true);
							clearInventory(p);
							setInventorySelect(p);
							setKitChooser(p);
							setInventorySelect(p);
						}
				    }
				}
		  }
	  } else if(isstarted){
		  Player p = event.getPlayer();
		  if(isinGame(p)){
			  if(isDead(p)){
				  event.setCancelled(true);
			  }
		  }
	  }
  }
  
  @SuppressWarnings("deprecation")
@EventHandler
  public void detection(BlockPlaceEvent event) {
    if (event.getPlayer().equals(temp)) {
      if (detection != 0) {
        if (detection == 1)
        {
          detection = 0;
          Location l2 = event.getBlockPlaced().getLocation();
          vblue.add(l2);
          event.getBlockPlaced().setType(Material.WOOL);
          event.getBlockPlaced().setData(DyeColor.BLUE.getData());
          event.getPlayer().sendMessage(PNC() + ChatColor.GREEN + "This block is now a bblock !");
        }
        else if (detection == 2)
        {
          detection = 0;
          Location l2 = event.getBlockPlaced().getLocation();
          vred.add(l2);
          event.getBlockPlaced().setType(Material.WOOL);
          event.getBlockPlaced().setData(DyeColor.RED.getData());
          event.getPlayer().sendMessage(PNC() + ChatColor.GREEN + "This block is now a bblock !");
        }
      }
      for (int i = 0; i < vblue.size(); i++) {
        Location loc = (Location)vblue.get(i);
        if (loc.getBlock().getType() == Material.AIR) {
          vblue.remove(i);
        }
      }
      for (int i = 0; i < vred.size(); i++) {
        Location loc = (Location)vred.get(i);
        if (loc.getBlock().getType() == Material.AIR) {
          vred.remove(i);
        }
      }
    }
    if(canbuild){
    	if(isinGame(event.getPlayer())){
	    	Block b = event.getBlock();
	    	saveplace.add(b.getLocation());
    	}
    } else {
	    if (isinGame(event.getPlayer())) {
	    	event.setCancelled(true);
	    	event.getPlayer().updateInventory();
	    }
    }
  }
  
  @EventHandler
  public void onquit(PlayerQuitEvent event) {
	  if(isstarted || iswaiting){
	    if (isinGame(event.getPlayer())) {
	      disconnect(event.getPlayer());
	    }
	  }
  }
  
  @EventHandler
  public void ongamemode(PlayerGameModeChangeEvent event) {
    if ((isstarted || iswaiting)) {
    	if(isinGame(event.getPlayer())){
    		event.setCancelled(true);
    	}
    }
  }
  
  @SuppressWarnings("deprecation")
@EventHandler
  public void onBreak(BlockBreakEvent event) {
    if (detection == 3) {
      if (event.getPlayer().equals(temp)) {
        detection = 0;
        int ok = 0;
        for (int i = 0; i < vblue.size(); i++) {
          Location l = (Location)vblue.get(i);
          Location l2 = event.getBlock().getLocation();
          if (l.equals(l2)) {
            event.getPlayer().sendMessage(PNC() + ChatColor.GREEN + "Bblock deleted !");
            vred.remove(i);
            ok = 1;
          }
        }
        if (ok == 0) {
          event.getPlayer().sendMessage(PNC() + ChatColor.GREEN + "This block is not a bblock !");
          event.setCancelled(true);
        }
      }
    } else if (detection == 4) {
      if (event.getPlayer().equals(temp)) {
        detection = 0;
        int ok = 0;
        for (int i = 0; i < vred.size(); i++) {
          Location l = (Location)vred.get(i);
          Location l2 = event.getBlock().getLocation();
          if (l.equals(l2)) {
            event.getPlayer().sendMessage(PNC() + ChatColor.GREEN + "Bblock deleted !");
            vred.remove(i);
            ok = 1;
          }
        }
        if (ok == 0) {
          event.getPlayer().sendMessage(PNC() + ChatColor.GREEN + "This block is not a bblock !");
          event.setCancelled(true);
        }
      }
    } else {
    	  if(event.getBlock().getType() == Material.WOOL){
		        for (int i = 0; i < vblue.size(); i++) {
		          Location l = (Location) vblue.get(i);
		          Location l2 = event.getBlock().getLocation();
		          if (l.equals(l2)) {
		        	  if (isinGame(event.getPlayer())) {
		        		  if(iswaiting){
		        			  event.getPlayer().sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.GAME_NOT_STARTED));
			            	  event.setCancelled(true);
			            	  return;
			              } else if ((redlife <= 0) || (bluelife <= 0)) {
			            	  event.getPlayer().sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.GAME_NOT_STARTED));
			            	  event.setCancelled(true);
			            	  return;
			              }
		            	  if (getteam(event.getPlayer()) == 1) {
		                    event.getPlayer().sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.YOUR_TEAM_BLOCK));
		                    event.setCancelled(true);
		                    break;
		                  }
			              bluelife -= 1;
			              if (bluelife == 0) {
			                stopthegame(1);
			                event.setCancelled(true);
			                break;
			              }
			              sendAll(battleOfBlocks.msg.putColor(battleOfBlocks.msg.structurateLives(battleOfBlocks.msg.BLUE_LIVES, bluelife)));
			              canteleport = true;
			              event.getPlayer().teleport(locstartred);
			              canteleport = false;
			              addmoney(2);
			              settalkbarre();
			              ParticleEffect.EXPLOSION_LARGE.display(2, 2, 2, 1, 5, event.getBlock().getLocation(), playersingame);
			              event.setCancelled(true);
		        	  } else {
				     		event.getPlayer().sendMessage(PNC() + ChatColor.GREEN + "You cannot destroy a bblock !");
				     		event.setCancelled(true);
				      }
			    	  break;
		            }
		        }
		    	 for (int i = 0; i < vred.size(); i++) {
				       Location l = (Location)vred.get(i);
				       Location l2 = event.getBlock().getLocation();
				       if (l.equals(l2)) {
				    	   if (isinGame(event.getPlayer())) {
				    		   	  if(iswaiting){
				        			  event.getPlayer().sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.GAME_NOT_STARTED));
					            	  event.setCancelled(true);
					            	  return;
					              } else if ((redlife <= 0) || (bluelife <= 0)) {
					            	  event.getPlayer().sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.GAME_NOT_STARTED));
					            	  event.setCancelled(true);
					            	  return;
					              }
						           if (getteam(event.getPlayer()) == 2) {
						            	event.getPlayer().sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.YOUR_TEAM_BLOCK));
						            	event.setCancelled(true);
						                break;
						           }
					              redlife -= 1;
					              if (redlife == 0) {
					            	  	stopthegame(2);
					                	event.setCancelled(true);
					                	break;
					              }
					              sendAll(battleOfBlocks.msg.putColor(battleOfBlocks.msg.structurateLives(battleOfBlocks.msg.RED_LIVES, redlife)));
					              canteleport = true;
					              event.getPlayer().teleport(locstartblue);
					              canteleport = false;
					              addmoney(1);
					              settalkbarre();
					              ParticleEffect.EXPLOSION_LARGE.display(2, 2, 2, 1, 5, event.getBlock().getLocation(), playersingame);
					              event.setCancelled(true);
				    	   } else {
					     		event.getPlayer().sendMessage(PNC() + ChatColor.GREEN + "You cannot destroy a bblock !");
					     		event.setCancelled(true);
					       }
				    	   break;
				       }
				 }
	      }
    }
    if(canbreak){
    	if(isinGame(event.getPlayer())){
    		if(!iswaiting){
    			if(!(event.getBlock().getType() == Material.GLASS)){
    				Block b = event.getBlock();
    		    	savebreak.add(new BlockSave(b.getType(), b.getLocation(), b.getData()));
    			} else {
    				event.getPlayer().sendMessage(PNC() + ChatColor.RED + "Not glass !");
    			}
    		} else {
    			event.setCancelled(true);
    		}
    	}
    } else {
    	if(isstarted || iswaiting){
		    if(isinGame(event.getPlayer())){
				 event.setCancelled(true);
				 canteleport = true;
			     event.getPlayer().teleport(tronc(event.getPlayer().getLocation()));
			     canteleport = false;
			}
    	}
    }
  }
  private Location tronc(Location loc){
		Location fin = loc.clone();
		fin.setY(fin.getY() + 0.5);
		return fin;
  }
  
  @EventHandler
  public void food_high(FoodLevelChangeEvent event){
 	 if(isstarted || iswaiting){
 		 if(event.getEntity() instanceof Player){
 			 Player p = (Player) event.getEntity();
 			 if(isinGame(p)){
 				 event.setCancelled(true);
 			 }
 		 }
 	 }
  }
  
  @EventHandler
  public void onteleport(PlayerTeleportEvent event)
  {
    if ((isstarted || iswaiting) && (!canteleport) && (isinGame(event.getPlayer()))) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void damageByEntityManager(EntityDamageByEntityEvent event) {
    if (isstarted) {
      if (!(event.getEntity() instanceof Player)) {
        return;
      }
      Player p = (Player) event.getEntity();
      if (isinGame(p)) {
    	  if(isDead(p)){
			  event.setCancelled(true);
			  return;
    	  }
          if ((event.getDamager() instanceof Player)) {
            Player d = (Player) event.getDamager();
            if(isDead(d)){
  		  		event.setCancelled(true);
  		  		return;
      	  	}
            if (getteam(d) == getteam(p)) {
          	  	event.setCancelled(true);
          	  	return;
            }
          }
    	String kit_p = getKitName(p);
    	if(kit_p.equalsIgnoreCase("Troll")) settunic(p, false);
        Damageable damage = p;
        if(event.getDamager() instanceof Player){
        	Player damager = (Player) event.getDamager();
        	if(getKitName(damager).equals(kit_p)){
        		double damage_final = (event.getDamage()*3)/4;
        		event.setDamage(DamageModifier.MAGIC, damage_final);
        	}
        }
        if (damage.getHealth() <= event.getDamage()) {
          if ((event.getDamager() instanceof Player)) {
	            Player killer = (Player) event.getDamager();
	            addmoney(killer);
	            returntothespawn(p, true, killer);
	            event.setCancelled(true);
          } else if((event.getDamager() instanceof Projectile)) {
        	  ProjectileSource ps = ((Projectile) event.getDamager()).getShooter();
        	  if(ps instanceof Player){
        		  Player killer = (Player) ps;
	              addmoney(killer);
	              returntothespawn(p, true, killer);
	              event.setCancelled(true);
        	  } else {
        		  returntothespawn(p, true, null);
            	  event.setCancelled(true);
        	  }
          } else {
        	  returntothespawn(p, true, null);
        	  event.setCancelled(true);
          }
        }
      }
    }
  }

  
  @EventHandler(priority=EventPriority.HIGH)
  public void nodiebeforestart(EntityDamageEvent event){
	if(iswaiting){
	    if ((event.getEntity() instanceof Player)) {
	    	Player p = (Player) event.getEntity();
	      	if (isinGame(p)) {
	      		event.setCancelled(true);
	      	}
	    }
	}
  }
  
  @EventHandler
  public void nodropitems(PlayerDropItemEvent event)
  {
    if (isstarted || iswaiting) {
      Player p = event.getPlayer();
      if (isinGame(p)) {
        event.setCancelled(true);
      }
    }
  }
  @EventHandler(priority = EventPriority.HIGH)
	public void nomoveitems(InventoryClickEvent event) {
	  if (isstarted || iswaiting) {
		  if(event.getWhoClicked() instanceof Player){
		      Player p = (Player) event.getWhoClicked();
		      if (isinGame(p)) {
	    		  if(event.getSlotType() == SlotType.ARMOR || event.getSlotType() == SlotType.QUICKBAR){
		    		  event.setResult(Result.DENY);
		    		  event.setCancelled(true);
	    		  }
		      }
		  }
	  }
  }
  
  public boolean canchangeweather = true;
  @EventHandler
  public void changeweather(WeatherChangeEvent event){
    if (isstarted && event.getWorld() == locstartred.getWorld() && canchangeweather) {
    	canchangeweather = false;
    	locstartred.getWorld().setTime(0L);
    	locstartred.getWorld().setStorm(false);
    	locstartred.getWorld().setThundering(false);
    	canchangeweather = true;
    }
  }
  
  @EventHandler
  public void nodie(EntityDamageEvent event){
    if ((isstarted) && ((event.getEntity() instanceof Player))) {
      Player p = (Player)event.getEntity();
      if (isinGame(p)) {
    	  if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
    	  		event.setCancelled(true);
    	  } else if(event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)){
    		  	event.setCancelled(true);
      	  } else {
      		  if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)){
      		  } else {
      			Damageable damage = p;
      	        if (damage.getHealth() <= event.getDamage()) {
      	        	if(damage.getLastDamageCause() instanceof EntityDamageByEntityEvent){
      	        		if(((EntityDamageByEntityEvent) damage.getLastDamageCause()).getDamager() instanceof Player) returntothespawn(p, true, (Player) ((EntityDamageByEntityEvent) damage.getLastDamageCause()).getDamager());
      	        		else returntothespawn(p, true, null);
      	        	} else {
      	        		returntothespawn(p, true, null);
      	        	}
      	        }
      		  }
      	  }
      }
    }
  }
  
  private boolean cancommand(Player p){
	  if(getMetadata(p, "cancommand", battleOfBlocks) != null){
		  if((boolean) getMetadata(p, "cancommand", battleOfBlocks) == true){
			  return true;
		  }
	  }
	  return false;
  }
  
  @EventHandler
  public void nopickup(PlayerPickupItemEvent event){
	  if(isstarted){
		  if(isinGame(event.getPlayer())){
			  if(!canbreak){
				  event.setCancelled(true);
				  event.getItem().remove();
			  }
		  }
	  }
  }
  
  
  @EventHandler
  public void nocommands(PlayerCommandPreprocessEvent event){
	  if(isstarted || iswaiting) {
		  Player p = event.getPlayer();
		  if(isinGame(p)) {
			  if(event.getMessage().equalsIgnoreCase("/leave")){
				  p.sendMessage(PNC() + ChatColor.RED + "You left the game !");
				  disconnect(p);
				  event.setCancelled(true);
			  } else if(event.getMessage().equalsIgnoreCase("/status")) {
				  event.setCancelled(true);
				  String kit = getKitName(p);
				  if(kit != null){
					  p.sendMessage(PNC() + ChatColor.GREEN + "Your kit:" + getKitName(p));
				  } else {
					  p.sendMessage(PNC() + ChatColor.RED + "You haven't choosen any kit for now !");
				  }
			  } else if(cancommand(p)) {
			  } else if(!(event.getMessage().startsWith("/batofb"))){
				  p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.NO_COMMANDS_IN_GAME));
				  event.setCancelled(true);
			  }
		  }
	  }
  }
  
  @EventHandler
  public void setColorChat(AsyncPlayerChatEvent event){
	  if(isstarted) {
		  Player p = event.getPlayer();
		  if(isinGame(p)) {
			  if(getteam(p) == 1){
				  event.setMessage(ChatColor.BLUE + "[Blue] " + ChatColor.RESET + event.getMessage());
			  } else if(getteam(p) == 2) {
				  event.setMessage(ChatColor.RED + "[Red] " + ChatColor.RESET + event.getMessage());
			  }
		  }
	  }
  }
  
  private ItemStack getIS(Material m, String name, String sousname, String perm, Player p){
	  return getIS(new ItemStack(m, 1), name, sousname, perm, p);
  }
  
	private ItemStack getIS(ItemStack i, String name, String sousname, String perm, Player p){
		i.setAmount(1);
		ItemMeta im = i.getItemMeta();
		List<String> l = new LinkedList<String>();
		if(perm != null){
			if(battleOfBlocks.hasPermission(p, perm)){
				im.setDisplayName(ChatColor.GREEN + name);
				l.add(ChatColor.GOLD + sousname);
			} else {
				im.setDisplayName(ChatColor.RED + name);
				l.add(battleOfBlocks.msg.putColor(battleOfBlocks.msg.PERMISSION_DENIED));
			}
		} else {
			im.setDisplayName(ChatColor.GREEN + name);
			l.add(ChatColor.GOLD + sousname);
		}
		im.setLore(l);
		i.setItemMeta(im);
		return i;
	}
	
	private Inventory getInvChooseKit(Player p, String arenaname){
		Inventory inv;
		inv = Bukkit.createInventory(p,27,ChatColor.GREEN + "BattleOfBlocks KITS ! " + arenaname);
		inv.setMaxStackSize(999);
		inv.addItem(getIS(Material.BONE, "Mage", "Harness the magic !", null, p));
		inv.addItem(getIS(Material.INK_SACK, "Spy", "Super Secret!", "battleofblocks.hasbuy.kit.Spy", p));
		inv.addItem(getIS(new SpawnEgg(EntityType.BLAZE).toItemStack(), "Phoenix", "Burn baby burn!", "battleofblocks.hasbuy.kit.Phoenix", p));
		inv.addItem(getIS(Material.VINE, "Shamen", "Nature at your side", "battleofblocks.hasbuy.kit.Shamen", p));
		inv.addItem(getIS(Material.STICK, "Healer", "I love healing people!!", "battleofblocks.hasbuy.kit.Healer", p));
		inv.addItem(getIS(Material.FEATHER, "Troll", "TROLOLOLOL!", "battleofblocks.hasbuy.kit.Troll", p));
		inv.addItem(getIS(Material.TNT, "Fury", "Im not happy with you!!!", "battleofblocks.hasbuy.kit.Fury", p));
		if(battleOfBlocks.kits != null){
			List<Kit> v = battleOfBlocks.kits.v;
			for(int i = 0; i < v.size(); i++){
				Kit k = v.get(i);
				inv.addItem(getIS(k.m, k.name, k.des, k.perm, p));
			}
		}
		return inv;
	}
	
	@EventHandler
	public void choosekit(InventoryClickEvent event) {
		if(event.getInventory().getTitle().equals(ChatColor.GREEN + "BattleOfBlocks KITS ! " + getName())) {
			if(event.getWhoClicked() instanceof Player){
				Player p = (Player) event.getWhoClicked();
				if(event.getCurrentItem() != null){
					if(event.getCurrentItem().getItemMeta() != null){
						if(event.getCurrentItem().getItemMeta().getDisplayName() != null){
							if(event.getCurrentItem().getItemMeta().getLore() != null){
								if(event.getCurrentItem().getItemMeta().getLore().contains(battleOfBlocks.msg.putColor(battleOfBlocks.msg.PERMISSION_DENIED))){
									p.closeInventory();
									p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.PERMISSION_DENIED));
									event.setCancelled(true);
								} else {
									String kit = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
									p.sendMessage(PNC() + ChatColor.GREEN + "You have choosen the kit: " + kit);
									p_kits.put(p.getUniqueId(), kit);
									p.closeInventory();
									event.setCancelled(true);
								}
							}
						}
					}
				}
			}
		}
	}
  
  @EventHandler
  public void onRespawnOther(PlayerRespawnEvent event){
	  if(isstarted) {
		  Player p = event.getPlayer();
		  if(isinGame(p)) {
			  p.teleport(waitroom);
			  if(getteam(p) == 1){
				  returntothespawn(p, true, null);
			  } else if(getteam(p) == 2) {
				  returntothespawn(p, true, null);
			  }
		  }
	  }
  }

@Override
public boolean isCorrect() {
	if(locend == null){
		return false;
	} else if(locstartred == null){ 
		return false;
	} else if(locstartblue == null){
		return false;
	} else if(waitroom == null){
		return false;
	} else if(vblue.isEmpty() || vred.isEmpty()){
		return false;
	} else if(pmax < startmin) { 
		return false;
	} else {
		return true;
	}
}
}