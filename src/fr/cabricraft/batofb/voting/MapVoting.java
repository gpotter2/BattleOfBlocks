package fr.cabricraft.batofb.voting;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import fr.cabricraft.batofb.BattleOfBlocks;
import fr.cabricraft.batofb.arenas.Arena;
import fr.cabricraft.batofb.signs.SignUtility;
import fr.cabricraft.batofb.util.Verified;

public class MapVoting implements Listener, Verified {
	
	private BattleOfBlocks battleofblocks = null;
	private Location wait = null;
	private Location end = null;
	public int maxplayers = 0;
	public int startmin = 0;
	
	private boolean canteleport = false;
	
	private String name;
	
	private MapVoting thisinstance;
	
	private List<Arena> arena_used = new LinkedList<Arena>();
	private Scoreboard reset;
	
	private List<Player> players = new LinkedList<Player>();
	private HashMap<UUID, ItemStack[]> players_inv = new HashMap<UUID, ItemStack[]>();
	private HashMap<UUID, String> players_votes = new HashMap<UUID, String>();
	private HashMap<UUID, Boolean> players_already_voted = new HashMap<UUID, Boolean>();
	
	public MapVoting(BattleOfBlocks battleofblocks, String name, Location wait, Location end, int maxplayers, int startmin){
		thisinstance = this;
		this.battleofblocks = battleofblocks;
		this.name = name;
		this.end = end;
		this.wait = wait;
		this.maxplayers = maxplayers;
		this.startmin = startmin;
		reset = Bukkit.getScoreboardManager().getNewScoreboard();
	}
	
	public void setWait(Location loc){
		this.wait = loc;
	}
	public void setEnd(Location loc){
		this.end = loc;
	}
	public void setMax(int max){
		this.maxplayers = max;
	}
	public void setStart(int min){
		this.startmin = min;
	}
	public String getName(){
		return name;
	}
	public Location getWait(){
		return wait;
	}
	public Location getEnd(){
		return end;
	}
	
	public void sendAll(String message){
		for(Player p : players){
			p.sendMessage(message);
		}
	}
	
	public int connectedPlayers(){
		return players.size();
	}
	
	public Scoreboard reset(){
		reset.clearSlot(DisplaySlot.BELOW_NAME);
		reset.clearSlot(DisplaySlot.PLAYER_LIST);
		reset.clearSlot(DisplaySlot.SIDEBAR);
		return reset;
	}
	
	public void teleportPlayer(Player p, Location to){
		  canteleport = true;
		  Chunk c = to.getChunk();
		  if(!c.isLoaded()) to.getChunk().load();
		  p.teleport(to);
		  canteleport = false;
	  }
	
	private int getArenaVoteNumber(String arena_name){
		int count = 0;
		for(Iterator<Entry<UUID, String>> it = players_votes.entrySet().iterator(); it.hasNext();){
    		Entry<UUID, String> entry = it.next();
    		UUID uuid = entry.getKey();
    		if(battleofblocks.getServer().getOfflinePlayer(uuid).isOnline()){
				if(entry.getValue().equals(arena_name)){
					count++;
				}
			}
    	}
		return count;
	}
	
	private List<String> getVoteNumberArena(int vote_number){
		HashMap<String, Integer> arenas_votes_list_temp = new HashMap<String, Integer>();
		for(Iterator<Entry<UUID, String>> it = players_votes.entrySet().iterator(); it.hasNext();){
    		Entry<UUID, String> entry = it.next();
    		UUID uuid = entry.getKey();
    		if(battleofblocks.getServer().getOfflinePlayer(uuid).isOnline()){
    			String name = entry.getValue();
    			if(arenas_votes_list_temp.containsKey(name)){
    				arenas_votes_list_temp.put(name, (arenas_votes_list_temp.get(name) + 1));
    			} else {
    				arenas_votes_list_temp.put(name, 1);
    			}
			}
    	}
		List<String> arenas_result = new LinkedList<String>();
		for(Iterator<Entry<String, Integer>> it = arenas_votes_list_temp.entrySet().iterator(); it.hasNext();){
    		Entry<String, Integer> entry = it.next();
    		if(entry.getValue() == vote_number){
    			arenas_result.add(entry.getKey());
    		}
		}
		return arenas_result;
	}
	
	private ItemStack getIS(Material m, String name, Player p, MaterialData data, String... sousname){
		ItemStack i = new ItemStack(m);
		if(data != null) i.setData(data);
		i.setAmount(1);
		ItemMeta im = i.getItemMeta();
		List<String> l = new LinkedList<String>();
		im.setDisplayName(ChatColor.GREEN + name);
		for(String s : sousname){
			l.add(ChatColor.GOLD + s);
		}
		im.setLore(l);
		i.setItemMeta(im);
		return i;
	}
	
	public void clear(){
		players.clear();
		players_inv.clear();
		players_votes.clear();
		players_already_voted.clear();
	}
	
	private void updateBar(){
		float percent;
		if(connectedPlayers() > startmin){
			percent = 100F;
		} else {
			percent = (connectedPlayers()*100)/startmin;
		}
		String message = fr.cabricraft.batofb.util.Messages.PNC() + ChatColor.GREEN + connectedPlayers() + "/" + maxplayers + ", " + battleofblocks.msg.NEED + " " + startmin;
		battleofblocks.bb_connect.updateBar(players, "voting", message, percent);
	}
	
	private void removeBar(Player player){
		battleofblocks.bb_connect.removeBar(players, "voting");
	}
	
	  private void updateScoreboards(){
		  for(Player p : players){
			  updateScoreboard(p);
		  }
	  }
	  private void updateScoreboard(Player p) {
	  	  Scoreboard scb = p.getScoreboard();
	  	  Objective obj;
	  	  if(scb.getObjective("stats") == null){
	  		  obj = scb.registerNewObjective("stats", "dummy");
	  	  } else {
	  		  obj = scb.getObjective("stats");
	  	  }
	  	  obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	  	  obj.setDisplayName(ChatColor.GOLD + "MapVoter");
	  	  for(Arena ar : arena_used){
		  	  @SuppressWarnings("deprecation")
		  	  Score score = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + ar.getName()));
		  	  score.setScore(getArenaVoteNumber(ar.getName()));
	  	  }
	  	  p.setScoreboard(scb);
	  }
	
	private int getInvSize(int size){
		return ((int)(size/9)) + 9;
	}
	@SuppressWarnings("deprecation")
	private Inventory getInv(Player p){
		Inventory inv;
		inv = Bukkit.createInventory(p,getInvSize(battleofblocks.arenas.size()),ChatColor.GREEN + "BattleOfBlocks Map Voting !");
		inv.setMaxStackSize(999);
		for(Arena ar : battleofblocks.arenas){
			if(ar.isCorrect() && !ar.isstarted){
				arena_used.add(ar);
				MaterialData data = new MaterialData(Material.WOOL);
				data.setData(DyeColor.GREEN.getData());
				inv.addItem(getIS(Material.WOOL, ar.getName(), p, data, "Voted " + ChatColor.GREEN + getArenaVoteNumber(name) + ChatColor.GOLD + " times !"));
			}
		}
		return inv;
	}
	
	@SuppressWarnings("deprecation")
	public void addPlayer(Player player){
		player.sendMessage(battleofblocks.msg.putColor(fr.cabricraft.batofb.util.Messages.PNC()) + ChatColor.GREEN + "You joined the vote !");
		teleportPlayer(player, wait);
		player.setGameMode(GameMode.SURVIVAL);
		player.setScoreboard(reset());
		removeBar(player);
		players.add(player);
		players_inv.put(player.getUniqueId(), player.getInventory().getContents());
		players_already_voted.put(player.getUniqueId(), false);
		SignUtility.updateVoteSigns();
		sendAll(battleofblocks.msg.structurate(battleofblocks.msg.putColor(battleofblocks.msg.OTHER_JOIN_THE_GAME), player, null) + ChatColor.GREEN + "" + connectedPlayers() + "/" + maxplayers + " (needed:" + startmin + ")");
		updateScoreboards();
		updateBar();
		player.getInventory().clear();
		player.getInventory().setItem(0, getIS(Material.NETHER_STAR, "Map selector", player, null, "Clic here to select the map"));
		player.updateInventory();
		if(startmin == connectedPlayers()){
			new BukkitRunnable() {
				MapVoting mv = thisinstance;
				@Override
				public void run() {
					for(int i = 0; i < 30; i++){
						int sec = 30 - i;
						if(mv.connectedPlayers() < mv.startmin){
							return;
						}
						if(sec % 10 == 0 || sec < 11) {
							mv.sendAll(battleofblocks.msg.putColor(battleofblocks.msg.structurateTime(battleofblocks.msg.GAME_START_IN_X_SECONDS, sec)));
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(mv.players.size() < mv.startmin){
						return;
					}
					startTheGame();
				}
			}.runTask(battleofblocks);
		}
	}
	
	public void removePlayer(Player player){
		player.sendMessage(battleofblocks.msg.putColor(fr.cabricraft.batofb.util.Messages.PNC()) + ChatColor.RED + "You left the vote !");
		players.remove(player);
		SignUtility.updateVoteSigns();
		sendAll(battleofblocks.msg.structurate(battleofblocks.msg.putColor(battleofblocks.msg.OTHER_LEFT_THE_GAME), player, null) + ChatColor.GREEN + "" + connectedPlayers() + "/" + maxplayers + " (needed:" + startmin + ")");
		teleportPlayer(player, end);
		player.getInventory().setContents(players_inv.get(player.getUniqueId()));
		updateScoreboards();
		updateBar();
		player.setScoreboard(reset());
		removeBar(player);
		if(connectedPlayers() == 0){
			clear();
		}
	}
	
	private void startTheGame(){
		boolean continu = true;
		while(continu){
			Arena best_arena = getBestArena();
			if(best_arena == null){
				continu = false;
				sendAll(battleofblocks.msg.putColor(fr.cabricraft.batofb.util.Messages.PNC()) + ChatColor.RED + "No arenas are free !");
			}
			if(best_arena.isCorrect() && !best_arena.isstarted){
				continu = false;
				for(Player p : players){
					p.setScoreboard(reset());
					removeBar(p);
					p.getInventory().clear();
					p.getInventory().setContents(players_inv.get(p.getUniqueId()));
					best_arena.join(p);
				}
			}
		}
		clear();
		SignUtility.updateVoteSigns();
	}
	private Arena getBestArena(){//TODO improve with a HashMap
    	Integer to_tidy[] = new Integer[arena_used.size()];
    	int to_tidy_size = 0;
    	for(Arena ar : arena_used){
			to_tidy[to_tidy_size] = getArenaVoteNumber(ar.getName());
			to_tidy_size++;
    	}
    	Arrays.sort(to_tidy, new Comparator<Integer>(){ 
	        @Override
	        public int compare(Integer x, Integer y) {
	            return y - x;
	        }
	    });
    	int best = to_tidy[0];
    	List<Arena> best_arenas = new LinkedList<Arena>();
    	for(int number : to_tidy){
    		if(number == best){
    			List<String> get_arenas = getVoteNumberArena(number);
    			for(String s : get_arenas){
    				Arena ar = battleofblocks.getArena(s);
	    			if(!best_arenas.contains(ar)){
	    				best_arenas.add(ar);
	    			}
    			}
    		} else {
    			break;
    		}
    	}
    	if(best_arenas.size() == 1){
    		return best_arenas.get(0);
    	}
    	if(best_arenas.size() == 0){
    		return null;
    	}
    	return best_arenas.get(randomInteger(0, (best_arenas.size() - 1)));
    }
	private int randomInteger(int min, int max){
		return (min + (int)(Math.random() * ((max - min) + 1)));
	}
	
	@EventHandler
	public void commandHandler(PlayerCommandPreprocessEvent event){
		if(players.contains(event.getPlayer())){
			Player p = event.getPlayer();
			if(event.getMessage().equalsIgnoreCase("/leave")){
				  removePlayer(p);
				  event.setCancelled(true);
			} else {
				p.sendMessage(battleofblocks.msg.putColor(battleofblocks.msg.NO_COMMANDS_IN_GAME));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void nodie(EntityDamageEvent event){
	    if (event.getEntity() instanceof Player) {
	    	Player p = (Player) event.getEntity();
	    	if(players.contains(p)){
	    		event.setCancelled(true);
	    	}
	    }
	}
	
	  @EventHandler
	  public void food_high(FoodLevelChangeEvent event){
 		 if(event.getEntity() instanceof Player){
 			if (players.contains((Player) event.getEntity())){
 				 event.setCancelled(true);
 			 }
 		 }
	  }
	  
	    @EventHandler(priority = EventPriority.HIGH)
		public void nomoveitems(InventoryClickEvent event) {
		  if(event.getWhoClicked() instanceof Player){
		      Player p = (Player) event.getWhoClicked();
		      if (players.contains(p)) {
	    		  if(event.getSlotType() == SlotType.ARMOR || event.getSlotType() == SlotType.QUICKBAR){
		    		  event.setResult(Result.DENY);
		    		  event.setCancelled(true);
	    		  }
		      }
		  }
	  }
	    
	    @EventHandler
		public void manageInteract(PlayerInteractEvent event){
			  if(event.getItem() != null){
				  ItemStack is = event.getItem();
				  if(is.getType() == Material.NETHER_STAR){
					  Player p = event.getPlayer();
					  if(players.contains(p)){
						  p.openInventory(getInv(p));
					  }
				  }
			  }
	    }
	  
		@EventHandler
		public void invfunct(InventoryClickEvent event){
			if(event.getInventory().getTitle().equals(ChatColor.GREEN + "BattleOfBlocks Map Voting !")) {
				if(event.getWhoClicked() instanceof Player){
					Player p = (Player) event.getWhoClicked();
					if(players.contains(p)){
						event.setCancelled(true);
						if(!players_already_voted.get(p.getUniqueId())){
							ItemStack is = event.getCurrentItem();
							if(is == null){
								return;
							}
							if(is.getType() != Material.WOOL){
								event.setCancelled(true);
								return;
							}
							String arena_name = is.getItemMeta().getDisplayName().substring(2);
							players_votes.put(p.getUniqueId(), arena_name);
							players_already_voted.put(p.getUniqueId(), true);
							updateScoreboards();
							p.sendMessage(battleofblocks.msg.putColor(fr.cabricraft.batofb.util.Messages.PNC()) + ChatColor.GREEN + "You voted for '" + arena_name + "' !");
							p.closeInventory();
						} else {
							p.sendMessage(battleofblocks.msg.putColor(fr.cabricraft.batofb.util.Messages.PNC()) + ChatColor.RED + "You already voted !");
							p.closeInventory();
						}
					}
				}
			}
		}
	    
	  @EventHandler
	  public void onteleport(PlayerTeleportEvent event){
		  if(canteleport){
			  if (players.contains(event.getPlayer())){
				  event.setCancelled(true);
			  }
		  }
	  }
	  
	  @EventHandler
	  public void onbreak(BlockBreakEvent event){
		  if (players.contains(event.getPlayer())){
			  teleportPlayer(event.getPlayer(),tronc(event.getPlayer().getLocation()));
			  event.setCancelled(true);
		  }
	  }
	  private Location tronc(Location loc){
			Location fin = loc.clone();
			fin.setY(fin.getY() + 0.5);
			return fin;
	  }
	  
	  @EventHandler
	  public void onplace(BlockPlaceEvent event){
		  if (players.contains(event.getPlayer())){
			  event.setCancelled(true);
		  }
	  }
	
	@EventHandler
	public void nodropitems(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if (players.contains(p)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	  public void onquit(PlayerQuitEvent event) {
			Player p = event.getPlayer();
			if (players.contains(p)) {
				removePlayer(p);
			}
	  }
	  
	  @EventHandler
	  public void ongamemode(PlayerGameModeChangeEvent event) {
		  	Player p = event.getPlayer();
			if (players.contains(p)) {
	    		event.setCancelled(true);
	    	}
	  }

	@Override
	public boolean isCorrect() {
		if(end == null || name == null || wait == null){
			return false;
		}
		return true;
	}	
}
