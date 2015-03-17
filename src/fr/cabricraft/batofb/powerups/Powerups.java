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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import fr.cabricraft.batofb.arenas.Arena;
import fr.cabricraft.batofb.util.IronGolemControl;
import fr.cabricraft.batofb.util.ParticleEffect;
import fr.cabricraft.batofb.util.ParticleLauncher;

public class Powerups implements Listener {
	Arena arena;
	List<Block> blockstoremove = new LinkedList<Block>();
	
	public Powerups(Arena arena){
		this.arena = arena;
	}
	
	private ItemStack getIS(Material m, String name, String sousname, int price, Player p, String perm){
		ItemStack i = new ItemStack(m);
		ItemMeta im = i.getItemMeta();
		List<String> l = new LinkedList<String>();
		l.add(ChatColor.GOLD + sousname);
		if(hasBoughtPerk(p, perm)|| perm == "none"){
			if(arena.canpay(p, price)){
				im.setDisplayName(ChatColor.GREEN + name);
				if(m != Material.GOLD_NUGGET) l.add(ChatColor.GOLD + "Price: " + price);
			} else {
				im.setDisplayName(ChatColor.RED + name);
				if(m != Material.GOLD_NUGGET) l.add(ChatColor.GOLD + "Price: " + price);
			}
		} else {
			im.setDisplayName(ChatColor.RED + name);
			if(m != Material.GOLD_NUGGET) l.add(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.DIDNT_BOUGHT_YET));
		}
		im.setLore(l);
		i.setItemMeta(im);
		return i;
	}
	
	
	
	private Inventory getInvPowerups(Player p){
		Inventory inv;
		int inv_size = 27;
		int number_of_basic_powerups = 10;
		if(arena.battleOfBlocks.ca.getList().size() + number_of_basic_powerups > inv_size){
			for(; arena.battleOfBlocks.ca.getList().size() + number_of_basic_powerups > inv_size; inv_size = inv_size + 9){}
			if(inv_size > 45){
				inv_size = 54;
				arena.battleOfBlocks.getLogger().severe("Cannot create a chest bigger than 45 blocks... Too many powerups !");
			}
		}
		inv = Bukkit.createInventory(p,inv_size,ChatColor.GREEN + "POWERUPS !");
		inv.setMaxStackSize(999);
		ItemStack ggc = getIS(Material.GOLD_NUGGET, "Money: " + arena.getmoney(p), "You have : " + arena.getmoney(p) + "golds !", 0, p, "none");
		inv.addItem(getIS(Material.SNOW_BALL, "SLOW !", arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.SLOW), 10, p, "none"));
		inv.addItem(getIS(Material.REDSTONE, "REDSTONE-POWER !", "Instant teleport in 7 blocks where you are looking at !", 30, p, "none"));
		inv.addItem(getIS(Material.ARROW, "ARROWS !", "Launch arrows to your ennemies !", 30, p, "none"));
		inv.addItem(getIS(Material.LEATHER_BOOTS, "JUMP !", arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.JUMP), 30, p, "none"));
		inv.addItem(getIS(Material.IRON_BLOCK , "IRON MAN !", arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.IRON_MAN), 50, p, "ironman"));
		inv.addItem(getIS(Material.BRICK, "FORECFIELD !", arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.FORCEFIELD), 50, p, "forcefield"));
		inv.addItem(getIS(Material.STICK, "KNOCKBACK !", arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.KNOCKBACK), 50, p, "knockback"));
		inv.addItem(getIS(Material.FIREBALL, "FIREBALLS !", arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.FIREBALLS), 70, p, "fireballs"));
		inv.addItem(getIS(Material.BEDROCK, "BERSERK !", arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.BERSERK), 100, p, "berserk"));
		inv.addItem(getIS(Material.TNT, "TNTBOOM !", arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.TNTBOOM), 100, p, "tntboom"));
		CustomAdd ca = arena.battleOfBlocks.ca;
		List<CustomAddPower> l =  ca.getList();
		for(int i = 0; i < l.size(); i++){
			if(inv.getContents().length <= 45){
				CustomAddPower cap = l.get(i);
				inv.addItem(getIS(cap.m, cap.name, cap.description, cap.price, p, cap.name));
			}
		}
		inv.setItem(inv_size - 5, ggc);
		return inv;
	}
	
	public void removeforcefield(Player p){
		p.getInventory().setHelmet(null);
		p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 10, 10);
		p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP_END, "forcefield", null)));
	}
	
	public void removefireball(Player p){
		p.getInventory().remove(Material.BLAZE_POWDER);
		p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 10, 10);
		p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP_END, "fireball", null)));
	}
	
	public void removekb(Player p){
		p.getInventory().remove(Material.STICK);
		p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 10, 10);
		p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP_END, "knockback", null)));
	}
	public void removejump(Player p){
		p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 10, 10);
		p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP_END, "jump", null)));
	}

	public void removeberserk(Player p){
		arena.settunic(p, false);
		p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 10, 10);
		p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP_END, "berserk", null)));
	}
	
	public void removeinvisible(Player p){
		arena.settunic(p, false);
		p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 10, 10);
		p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP_END, "invisibility", null)));
	}
	
	@SuppressWarnings("deprecation")
	public void makeInvisible(Player p){
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1, true));
		p.getInventory().setChestplate(null);
		p.updateInventory();
		Thread t = new Thread(new PowerupsChrono(this,6,p, null));
		t.start();
	}
	
	 public List<Block> getNearbyBlocks(Location location, int Radius) {
		    List<Block> Blocks = new ArrayList<Block>();
		    for (int X = location.getBlockX() - Radius; X <= location.getBlockX() + Radius; X++) {
		      for (int Y = location.getBlockY() - Radius; Y <= location.getBlockY() + Radius; Y++) {
		        for (int Z = location.getBlockZ() - Radius; Z <= location.getBlockZ() + Radius; Z++) {
		          Block b = location.getWorld().getBlockAt(X, Y, Z);
		          Blocks.add(b);
		        }
		      }
		    }
		    return Blocks;
		  }
	
	public List<Block> get9BlockInFrontOfPlayer(Player p){
		BlockFace relative = getDirection(p.getLocation());
		Block b1 = p.getLocation().getBlock().getRelative(relative).getRelative(relative).getRelative(relative);
		Block b2 = b1.getRelative(BlockFace.UP);
		Block b3 = b2.getRelative(BlockFace.UP);
		Block b4 = getBlockOnRight(b1, relative);
		Block b5 = b4.getRelative(BlockFace.UP);
		Block b6 = b5.getRelative(BlockFace.UP);
		Block b7 = getBlockOnLeft(b1, relative);
		Block b8 = b7.getRelative(BlockFace.UP);
		Block b9 = b8.getRelative(BlockFace.UP);
		List<Block> l = new LinkedList<Block>();
		l.add(b1);
		l.add(b2);
		l.add(b3);
		l.add(b4);
		l.add(b5);
		l.add(b6);
		l.add(b7);
		l.add(b8);
		l.add(b9);
		return l;
	}
	
	public void appearWall(Player p){
		List<Block> lb = get9BlockInFrontOfPlayer(p);
		List<Block> blocks_to_remove = new LinkedList<Block>();
		for(Block b : lb){
			if(b.getType() == Material.AIR){
				b.setType(Material.DIRT);
				ParticleEffect.VILLAGER_HAPPY.display(2, 2, 2, 1, 10, b.getLocation(), arena.playersingame);
				blocks_to_remove.add(b);
			}
		}
		Thread t = new Thread(new PowerupsChrono(this,7,p, blocks_to_remove));
		t.start();
	}
	
	public void removeWall(Object list){
		@SuppressWarnings("unchecked")
		List<Block> lb = (List<Block>) list;
		for(Block b : lb){
			blockstoremove.add(b);
		}
	}
	private static BlockFace getDirection(Location loc) {
		float dir = Math.round(loc.getYaw() / 90);
		if (dir == -4 || dir == 0 || dir == 4)
			return BlockFace.SOUTH;
		if (dir == -1 || dir == 3)
			return BlockFace.EAST;
		if (dir == -2 || dir == 2)
			return BlockFace.NORTH;
		if (dir == -3 || dir == 1)
			return BlockFace.WEST;
		return null;
	}
	private static Block getBlockOnRight(Block b, BlockFace bf) {
		if(bf == BlockFace.EAST){
			return b.getRelative(BlockFace.NORTH);
		} else if(bf == BlockFace.SOUTH){
			return b.getRelative(BlockFace.EAST);
		} else if(bf == BlockFace.WEST){
			return b.getRelative(BlockFace.SOUTH);
		} else if(bf == BlockFace.NORTH){
			return b.getRelative(BlockFace.WEST);
		}
		return null;
	}
	private static Block getBlockOnLeft(Block b, BlockFace bf) {
		if(bf == BlockFace.EAST){
			return b.getRelative(BlockFace.SOUTH);
		} else if(bf == BlockFace.SOUTH){
			return b.getRelative(BlockFace.WEST);
		} else if(bf == BlockFace.WEST){
			return b.getRelative(BlockFace.NORTH);
		} else if(bf == BlockFace.NORTH){
			return b.getRelative(BlockFace.EAST);
		}
		return null;
	}
	
	
	@EventHandler
	public void noexplose(ExplosionPrimeEvent event){
		if(arena.isstarted) {
			if(event.getEntityType() == EntityType.FIREBALL) {
				Projectile pro = (Projectile) event.getEntity();
				if(pro.getShooter() instanceof Player){
					if(arena.isinGame((Player) pro.getShooter())){
						event.setRadius(0);
						event.setFire(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void nodamageteam(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			int t = arena.getteam(p);
			if(event.getDamager().getType() == EntityType.FIREBALL){
				Projectile pro = (Projectile) event.getDamager();
				LivingEntity le = (LivingEntity) pro.getShooter();
				if(arena.getteam(le) == t){
					event.setCancelled(true);
				} else {
					arena.returntothespawn(p, true, (Player) pro.getShooter());
					arena.addmoney((Player) pro.getShooter());
				}
			} else if(event.getDamager().getType() == EntityType.SNOWBALL){
				Projectile pro = (Projectile) event.getDamager();
				LivingEntity le = (LivingEntity) pro.getShooter();
				if(arena.getteam(le) == t){
					event.setCancelled(true);
				}
			}
		}
	}
	
	public boolean hasBoughtPerk(Player p, String name){
		return p.hasPermission("battleofblocks.hasbuy.perk." + name);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void invfunct(InventoryClickEvent event){
		if(event.getInventory().getTitle().equals(ChatColor.GREEN + "POWERUPS !")) {
			if(event.getWhoClicked() instanceof Player){
				Player p = (Player) event.getWhoClicked();
				if(arena.isinGame(p)){
					if(arena.isstarted){
						ItemStack is = event.getCurrentItem();
						if(is == null){
							return;
						}
						if(is.getType() == Material.TNT){
							if(hasBoughtPerk(p, "tntboom")) {
								if(arena.canpay(p, 100)){
									p.closeInventory();
									arena.pay(p, 100);
									int t = arena.getteam(p);
									p.sendMessage(ChatColor.GREEN + "BOOM  ...");
									arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP, "TNT BOOM", p)));
									p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
									for(int i = 0; i < arena.playersingame.size(); i++){
										Player p2 = arena.playersingame.get(i);
										int t2 = arena.getteam(p2);
										p2.playSound(p2.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
										if(t != t2){
											arena.returntothespawn(p2, true, null);
										}
									}
								} else {
									p.closeInventory();
									p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.NOT_ENOUGH_MONEY));
								}
							} else {
								p.closeInventory();
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.DIDNT_BOUGHT_YET));
							}
						} else if(is.getType() == Material.SNOW_BALL){
							if(arena.canpay(p, 10)){
								p.closeInventory();
								arena.pay(p, 10);
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.HERE_YOU_ARE));
								ItemStack its = new ItemStack(Material.SNOW_BALL, 5);
								ItemMeta im = its.getItemMeta();
								im.setDisplayName("Slow-balls");
								its.setItemMeta(im);
								p.getInventory().addItem(its);
								arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP, "Slow", p)));
							} else {
								p.closeInventory();
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.NOT_ENOUGH_MONEY));
							}
						} else if(is.getType() == Material.ARROW){
							if(arena.canpay(p, 30)){
								p.closeInventory();
								arena.pay(p, 10);
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.HERE_YOU_ARE));
								ItemStack w_h = new ItemStack(Material.ANVIL);
								ItemMeta i_w_h = w_h.getItemMeta();
								i_w_h.setDisplayName(ChatColor.GREEN + "ARROWS !");
								w_h.setItemMeta(i_w_h);
								p.getInventory().addItem(w_h);
								p.playNote(p.getLocation(),Instrument.PIANO, Note.natural(1, Tone.C));
								arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP, "Arrows", p)));
							} else {
								p.closeInventory();
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.NOT_ENOUGH_MONEY));
							}
						} else if(is.getType() == Material.FIREBALL){
							if(hasBoughtPerk(p, "fireball")) {
								if(arena.canpay(p, 70)){
									arena.pay(p,70);
									p.closeInventory();
									p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.HERE_YOU_ARE));
									ItemStack b_r = new ItemStack(Material.BLAZE_POWDER);
									ItemMeta i_b_r = b_r.getItemMeta();
									i_b_r.setDisplayName(ChatColor.GREEN + "FIREBALLS !");
									b_r.setItemMeta(i_b_r);
									p.getInventory().addItem(b_r);
									p.playNote(p.getLocation(),Instrument.PIANO, Note.natural(1, Tone.C));
									Thread t = new Thread(new PowerupsChrono(this,2,p, null));
									t.start();
									arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP, "Fireball", p)));
								} else {
									p.closeInventory();
									p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.NOT_ENOUGH_MONEY));
								}
							} else {
								p.closeInventory();
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.DIDNT_BOUGHT_YET));
							}
						} else if(is.getType() == Material.BRICK){
							if(hasBoughtPerk(p, "forcefield")) {
								if(arena.canpay(p, 30)){
									arena.pay(p,30);
									p.closeInventory();
									p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.HERE_YOU_ARE));
									ItemStack h = new ItemStack(Material.BRICK);
									p.getInventory().setHelmet(h);
									p.playNote(p.getLocation(),Instrument.PIANO, Note.natural(1, Tone.C));
									Thread t = new Thread(new PowerupsChrono(this,1,p, null));
									t.start();
									arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP, "ForceField", p)));
								} else {
									p.closeInventory();
									p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.NOT_ENOUGH_MONEY));
								}
							} else {
								p.closeInventory();
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.DIDNT_BOUGHT_YET));
							}
						} else if(is.getType() == Material.STICK){
							if(hasBoughtPerk(p, "knockback")) {
								if(arena.canpay(p, 50)){
									arena.pay(p,50);
									p.closeInventory();
									p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.HERE_YOU_ARE));
									ItemStack s = new ItemStack(Material.STICK);
									ItemMeta ims = s.getItemMeta();
									ims.addEnchant(Enchantment.KNOCKBACK, 10, true);
									ims.setDisplayName("KNOCKBACK X !");
									s.setItemMeta(ims);
									p.getInventory().addItem(s);
									p.playNote(p.getLocation(),Instrument.PIANO, Note.natural(1, Tone.C));
									Thread t = new Thread(new PowerupsChrono(this,3,p, null));
									t.start();
									arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP, "KnockBack", p)));
								} else {
									p.closeInventory();
									p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.NOT_ENOUGH_MONEY));
								}
							} else {
								p.closeInventory();
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.DIDNT_BOUGHT_YET));
							}
						} else if(is.getType() == Material.IRON_BLOCK){
							if(hasBoughtPerk(p, "ironman")) {
								if(arena.canpay(p, 50)){
									arena.pay(p, 50);
									p.closeInventory();
									p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.HERE_YOU_ARE));
									Entity golem = p.getWorld().spawnEntity(p.getLocation(), EntityType.IRON_GOLEM);
									Thread t = new Thread(new IronGolemControl(p, arena, golem));
									t.start();
									p.playNote(p.getLocation(),Instrument.PIANO, Note.natural(1, Tone.C));
									arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP, "Iron Man", p)));
								} else {
									p.closeInventory();
									p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.NOT_ENOUGH_MONEY));
								}
							} else {
								p.closeInventory();
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.DIDNT_BOUGHT_YET));
							}
						} else if(is.getType() == Material.BEDROCK){
							if(hasBoughtPerk(p, "berserk")) {
								if(arena.canpay(p, 100)){
									arena.pay(p, 100);
									p.closeInventory();
									p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.HERE_YOU_ARE));
									p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2, true));
									p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1, true));
									p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 2, true));
									p.getInventory().setChestplate(null);
									p.updateInventory();
									Thread t = new Thread(new PowerupsChrono(this,4,p, null));
									t.start();
									p.playNote(p.getLocation(),Instrument.PIANO, Note.natural(1, Tone.C));
									arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP, "Berserk", p)));
								} else {
									p.closeInventory();
									p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.NOT_ENOUGH_MONEY));
								}
							} else {
								p.closeInventory();
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.DIDNT_BOUGHT_YET));
							}
						} else if(is.getType() == Material.LEATHER_BOOTS){
							if(arena.canpay(p, 30)){
								arena.pay(p, 30);
								p.closeInventory();
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.HERE_YOU_ARE));
								p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, 5, true));
								p.playNote(p.getLocation(),Instrument.PIANO, Note.natural(1, Tone.C));
								Thread t = new Thread(new PowerupsChrono(this,5,p, null));
								t.start();
								arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP, "Jump", p)));
							} else {
								p.closeInventory();
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.NOT_ENOUGH_MONEY));
							}
						} else if(is.getType() == Material.REDSTONE){
							if(arena.canpay(p, 30)){
								arena.pay(p, 30);
								p.closeInventory();
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.HERE_YOU_ARE));
								ItemStack w_h = new ItemStack(Material.REDSTONE);
								ItemMeta i_w_h = w_h.getItemMeta();
								i_w_h.setDisplayName(ChatColor.GREEN + "REDSTONE-POWER !");
								w_h.setItemMeta(i_w_h);
								p.getInventory().addItem(w_h);
								p.playNote(p.getLocation(),Instrument.PIANO, Note.natural(1, Tone.C));
								arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP, "Redstone-Power", p)));
							} else {
								p.closeInventory();
								p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.NOT_ENOUGH_MONEY));
							}
						} else if(is.getType() == Material.GOLD_NUGGET){
							p.sendMessage(ChatColor.GOLD + "You have " + arena.getmoney(p) + " coins !");
							p.closeInventory();
						} else {
							for(int i = 0; i < arena.battleOfBlocks.ca.getList().size(); i++){
								CustomAddPower cap = arena.battleOfBlocks.ca.getList().get(i);
								if(is.getType() == cap.m){
									if(p.hasPermission(cap.perm)){
										if(arena.canpay(p, cap.price)){
											arena.pay(p, cap.price);
											p.closeInventory();
											p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.HERE_YOU_ARE));
											String capcommand = cap.command;
											capcommand = capcommand.replaceAll("%player%", p.getName());
											if(capcommand.startsWith("player:")){
												capcommand = capcommand.replaceAll("player:", "");
												arena.setMetadata(p, "cancommand", true, arena.battleOfBlocks);
												if(capcommand.startsWith("/")){
													p.chat(capcommand);
												} else {
													p.chat("/" + capcommand);
												}
											} else {
												if(capcommand.startsWith("/")){
													arena.battleOfBlocks.getServer().dispatchCommand(arena.battleOfBlocks.getServer().getConsoleSender(), capcommand.replaceAll("/", ""));
												} else {
													arena.battleOfBlocks.getServer().dispatchCommand(arena.battleOfBlocks.getServer().getConsoleSender(), capcommand);
												}
											}
											arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.structuratePowerups(arena.battleOfBlocks.msg.POWERUP, cap.name, p)));
										} else {
											p.closeInventory();
											p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.NOT_ENOUGH_MONEY));
										}
									} else {
										p.closeInventory();
										p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.DIDNT_BOUGHT_YET));
									}
									break;
								}
							}
						}
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void functionBar(PlayerInteractEvent event){
		if(arena.isstarted){
			Player p = (Player) event.getPlayer();
			if(arena.isinGame(p)){
				ItemStack is = event.getItem();
				if(is == null || is.getItemMeta() == null){
					return;
				}
				if(is.getItemMeta().getDisplayName() == null){
					return;
				}
				String itemName = is.getItemMeta().getDisplayName().substring(2);
				if(itemName.equals("Flash !")){
					if(arena.canUsePower(p, itemName, 10)){
						arena.setCountDown(p, itemName);
						List<LivingEntity> l = new ParticleLauncher(arena).launchFirework(p, ParticleEffect.SMOKE_LARGE);
						for(LivingEntity le : l){
							if(le instanceof Player){
								Player damaged = (Player) le;
								if(arena.isinGame(damaged)){
									if(arena.getteam(damaged) != arena.getteam(p)){
										le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1, true));
										le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2, true));
										le.setLastDamageCause(new EntityDamageByEntityEvent(p, le, DamageCause.CUSTOM, 10));
									}
								}
							} else {
								le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1, true));
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2, true));
								le.setLastDamageCause(new EntityDamageByEntityEvent(p, le, DamageCause.CUSTOM, 10));
							}
						}
						p.sendMessage(arena.PNC() + ChatColor.GREEN + "Pow !");
					} else {
						p.sendMessage(arena.PNC() + ChatColor.RED + "Please wait " + arena.timeBeforeUse(p, itemName, 10) + " seconds before using this item again !");
					}
					event.setCancelled(true);
				} else if(itemName.equals("Invisibility !")){
					if(arena.canUsePower(p, itemName, 35)){
						arena.setCountDown(p, itemName);
						makeInvisible(p);
						p.sendMessage(arena.PNC() + ChatColor.GREEN + "You are now invisible for 10 sec !");
					} else {
						p.sendMessage(arena.PNC() + ChatColor.RED + "Please wait " + arena.timeBeforeUse(p, itemName, 35) + " seconds before using this item again !");
					}
					event.setCancelled(true);
			    } else if(itemName.equals("Inferno !")){
			    	if(arena.canUsePower(p, itemName, 10)){
						arena.setCountDown(p, itemName);
						List<LivingEntity> l = new ParticleLauncher(arena).launchFirework(p, ParticleEffect.ENCHANTMENT_TABLE);
						for(LivingEntity le : l){
							if(le instanceof Player){
								Player damaged = (Player) le;
								if(arena.isinGame(damaged)){
									if(arena.getteam(damaged) != arena.getteam(p)){
										le.setFireTicks(300);
										le.setLastDamageCause(new EntityDamageByEntityEvent(p, le, DamageCause.CUSTOM, 10));
									}
								}
							} else {
								le.setFireTicks(100);
								le.setLastDamageCause(new EntityDamageByEntityEvent(p, le, DamageCause.CUSTOM, 10));
							}
						}
						p.sendMessage(arena.PNC() + ChatColor.GREEN + "BURN !");
					} else {
						p.sendMessage(arena.PNC() + ChatColor.RED + "Please wait " + arena.timeBeforeUse(p, itemName, 10) + " seconds before using this item again !");
					}
			    	event.setCancelled(true);
			    } else if(itemName.equals("Nature protection !")){
			    	if(arena.canUsePower(p, itemName, 15)){
						arena.setCountDown(p, itemName);
						appearWall(p);
						p.sendMessage(arena.PNC() + ChatColor.GREEN + "Wow !");
					} else {
						p.sendMessage(arena.PNC() + ChatColor.RED + "Please wait " + arena.timeBeforeUse(p, itemName, 15) + " seconds before using this item again !");
					}
			    	event.setCancelled(true);
			    } else if(itemName.equals("Healer !")){
			    	if(arena.canUsePower(p, itemName, 3)){
						arena.setCountDown(p, itemName);
						boolean healed = true;
						List<LivingEntity> l = new ParticleLauncher(arena).launchFirework(p, ParticleEffect.FLAME);
						for(LivingEntity le : l){
							if(le instanceof Player){
								Player damaged = (Player) le;
								if(arena.isinGame(damaged)){
									if(arena.getteam(damaged) == arena.getteam(p)){
										if(le.getHealth() + 4 > le.getMaxHealth()){
											le.setHealth(le.getMaxHealth());
										} else {
											le.setHealth(le.getHealth() + 4);
										}
									} else {
										healed = false;
										le.damage(4, p);
										le.setLastDamageCause(new EntityDamageByEntityEvent(p, le, DamageCause.CUSTOM, 4));
										if(p.getHealth() + 4 > p.getMaxHealth()){
											p.setHealth(p.getMaxHealth());
										} else {
											p.setHealth(p.getHealth() + 4);
										}
									}
								}
							}
						}
						if(healed) p.sendMessage(arena.PNC() + ChatColor.RED + "\u2764\u2764\u2764");
						else p.sendMessage(arena.PNC() + ChatColor.BLACK + "\u2764\u2764\u2764");
					} else {
						p.sendMessage(arena.PNC() + ChatColor.RED + "Please wait " + arena.timeBeforeUse(p, itemName, 3) + " seconds before using this item again !");
					}
			    	event.setCancelled(true);
			    } else if(itemName.equals("Team color !")){
			    	if(arena.canUsePower(p, itemName, 30)){
						arena.setCountDown(p, itemName);
						arena.settunic(p, true);
						p.sendMessage(arena.PNC() + ChatColor.GREEN + "You are now disguised ! Any powerup or damage will remove it !");
					} else {
						p.sendMessage(arena.PNC() + ChatColor.RED + "Please wait " + arena.timeBeforeUse(p, itemName, 30) + " seconds before using this item again !");
					}
			    	event.setCancelled(true);
			    } else if(itemName.equals("Fury !")){
			    	if(arena.canUsePower(p, itemName, 10)){
						arena.setCountDown(p, itemName);
						List<LivingEntity> l = new ParticleLauncher(arena).launchFirework(p, ParticleEffect.VILLAGER_HAPPY);
						for(LivingEntity le : l){
							if(le instanceof Player){
								Player damaged = (Player) le;
								if(arena.isinGame(damaged)){
									if(arena.getteam(damaged) != arena.getteam(p)){
										org.bukkit.util.Vector vector = p.getTargetBlock((Set<Material>) null, 100).getLocation().toVector().subtract(p.getLocation().toVector()).normalize();
										punsh(le, vector);
										le.damage(10, p);
										le.setLastDamageCause(new EntityDamageByEntityEvent(p, le, DamageCause.CUSTOM, 10));
									}
								} else {
									org.bukkit.util.Vector vector = p.getTargetBlock((Set<Material>) null, 100).getLocation().toVector().subtract(p.getLocation().toVector()).normalize();
									punsh(le, vector);
									le.damage(10, p);
									le.setLastDamageCause(new EntityDamageByEntityEvent(p, le, DamageCause.CUSTOM, 10));
								}
							}
						}
						p.sendMessage(arena.PNC() + ChatColor.GREEN + "Lol !");
					} else {
						p.sendMessage(arena.PNC() + ChatColor.RED + "Please wait " + arena.timeBeforeUse(p, itemName, 10) + " seconds before using this item again !");
					}
			    	event.setCancelled(true);
			    }
			}
		}
	}
	
	public void punsh(Entity e, org.bukkit.util.Vector original){
		  org.bukkit.util.Vector vector = original.add(new org.bukkit.util.Vector(0,0.5,0)).multiply(new org.bukkit.util.Vector(2,2,2));
		  e.setVelocity(e.getVelocity().add(vector));
	  }
	
	@EventHandler
	public void clicHandler(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(arena.isstarted){
			if(arena.isinGame(p)){
				if(event.getItem() != null){
					if(event.getItem().getType() == Material.EMERALD){
						p.openInventory(getInvPowerups(p));
						return;
					}
					if(event.getItem().getItemMeta() != null){
						if(event.getItem().getItemMeta().getDisplayName() != null){
							String itemname = event.getItem().getItemMeta().getDisplayName().substring(2);
							 if(itemname.equalsIgnoreCase("FIREBALLS !")){
								 p.launchProjectile(Fireball.class);
							 } else if(itemname.equalsIgnoreCase("REDSTONE-POWER !")){
								 Location l = p.getTargetBlock((Set<Material>) null, 7).getLocation();
								 l.setPitch(p.getLocation().getPitch());
								 l.setYaw(p.getLocation().getYaw());
								 arena.canteleport = true;
								 p.teleport(l);
								 arena.canteleport = false;
								 p.getInventory().remove(event.getItem());
								 p.sendMessage(arena.PNC() + ChatColor.GREEN + "Wosh !");
							 } else if(itemname.equalsIgnoreCase("ARROWS !")){
								 List<Entity> l_e = p.getNearbyEntities(15, 15, 15);
								 for(Entity e : l_e){
									 if(e instanceof LivingEntity){
										 if(e instanceof Player){
											 if(arena.getteam(p) != arena.getteam((LivingEntity) e)){
												 Vector vec = ((LivingEntity) e).getEyeLocation().toVector().subtract(p.getEyeLocation().toVector()).normalize().multiply(new Vector(2, 2, 2));
												 p.launchProjectile(Arrow.class, vec);
												 p.launchProjectile(Arrow.class, vec);
											 }
										 } else {
											 Vector vec = ((LivingEntity) e).getEyeLocation().toVector().subtract(p.getEyeLocation().toVector()).normalize().multiply(new Vector(2, 2, 2));
											 p.launchProjectile(Arrow.class, vec);
											 p.launchProjectile(Arrow.class, vec);
										 }
									 }
								 }
								 p.getInventory().remove(event.getItem());
								 p.sendMessage(arena.PNC() + ChatColor.GREEN + "Launched !");
							 }
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void effects(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			if(arena.isinGame(p)){
				 if(p.getInventory().getHelmet() != null) {
					 if(p.getInventory().getHelmet().getType() == Material.BRICK){
							event.setCancelled(true);
					 }
				 }
				 if(event.getDamager().getType() == EntityType.SNOWBALL){
					 event.setCancelled(true);
					 p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200 ,2));
				 }
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event){
		if(arena.isstarted){
			 if(blockstoremove.size() != 0){
				 for(Block b : blockstoremove){
					 b.setType(Material.AIR);
				 }
				 blockstoremove.clear();
			 }
		}
	}
}
