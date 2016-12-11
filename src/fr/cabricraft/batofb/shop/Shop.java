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

package fr.cabricraft.batofb.shop;

import java.util.LinkedList;
import java.util.List;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.SpawnEgg;

import fr.cabricraft.batofb.BattleOfBlocks;
import fr.cabricraft.batofb.kits.Kit;
import fr.cabricraft.batofb.powerups.CustomAdd;
import fr.cabricraft.batofb.powerups.CustomAddPower;

public class Shop implements Listener {
	BattleOfBlocks battleOfBlocks;
	public Shop(BattleOfBlocks battleOfBlocks){
		this.battleOfBlocks = battleOfBlocks;
	}
	
	public boolean canpay(Player p, int price){
		return battleOfBlocks.econ.has(p, price);
	}
	
	public boolean hasBoughtPerk(Player p, String name){
		return battleOfBlocks.hasPermission(p, "battleofblocks.hasbuy.perk." + name);
	}
	
	public boolean hasBoughtKit(Player p, String name){
		return battleOfBlocks.hasPermission(p, "battleofblocks.hasbuy.kit." + name);
	}
	
	public void buy(int pr, Player p){
		EconomyResponse r;
		r = battleOfBlocks.econ.withdrawPlayer(p, pr);
		if(r.transactionSuccess()){
			p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.structurateMoney(battleOfBlocks.msg.MONEY_REMOVE, battleOfBlocks.econ.format(r.amount))));
			p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.structurateMoney(battleOfBlocks.msg.MONEY_NOW, battleOfBlocks.econ.format(r.balance))));
			p.playNote(p.getLocation(),Instrument.PIANO, Note.natural(1, Tone.C));
		}
	}
	
	public void buyPerk(Player p, String name, int price){
		buy(price, p);
		battleOfBlocks.permission.playerAdd(p, "battleofblocks.hasbuy.perk." + name);
		p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.structuratePerks(battleOfBlocks.msg.SUCCEFULLY_BOUGHT, name)));
	}
	
	public void buyKit(Player p, String name, int price){
		buy(price, p);
		battleOfBlocks.permission.playerAdd(p, "battleofblocks.hasbuy.kit." + name);
		p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.structuratePerks(battleOfBlocks.msg.SUCCEFULLY_BOUGHT, name)));
	}
	
	public ItemStack getISPerk(Material m, String name, String sousname, int price, Player p, String perm){
		ItemStack i = new ItemStack(m);
		List<String> l = new LinkedList<String>();
		ItemMeta im = i.getItemMeta();
		if(hasBoughtPerk(p, name)){
			im.setDisplayName(ChatColor.GRAY + name);
			l.add(battleOfBlocks.msg.putColorRemovePNC(battleOfBlocks.msg.ALREADY_BOUGHT));
		} else {
			String permission;
			if(perm == "none" || perm == null){
				permission = null;
			} else {
				permission = perm;
			}
			if(permission == null){
				l.add(ChatColor.GOLD + "Price: " + price);
				if(canpay(p, price)) {
					im.setDisplayName(ChatColor.GREEN + name);
					l.add(ChatColor.GREEN + "Clic here to buy !");
				} else {
					im.setDisplayName(ChatColor.RED + name);
					l.add(battleOfBlocks.msg.putColorRemovePNC(battleOfBlocks.msg.NOT_ENOUGH_MONEY));
				}
			} else {
				if(battleOfBlocks.hasPermission(p, permission)){
					l.add(ChatColor.GOLD + "Price: " + price);
					if(canpay(p, price)) {
						im.setDisplayName(ChatColor.GREEN + name);
						l.add(ChatColor.GREEN + "Clic here to buy !");
					} else {
						im.setDisplayName(ChatColor.RED + name);
						l.add(battleOfBlocks.msg.putColorRemovePNC(battleOfBlocks.msg.NOT_ENOUGH_MONEY));
					}
				} else {
					im.setDisplayName(ChatColor.RED + name);
					l.add(battleOfBlocks.msg.putColorRemovePNC(battleOfBlocks.msg.PERMISSION_DENIED));
				}
			}
		}
		l.add(ChatColor.GOLD + sousname);
		im.setLore(l);
		i.setItemMeta(im);
		return i;
	}
	
	public ItemStack getISKit(Material m, String name, String sousname, int price, Player p, String perm){
		return getISKit(new ItemStack(m, 1), name, sousname, price, p, perm);
	}
	
	public ItemStack getISKit(ItemStack i, String name, String sousname, int price, Player p, String perm){
		List<String> l = new LinkedList<String>();
		ItemMeta im = i.getItemMeta();
		if(hasBoughtKit(p, name)){
			im.setDisplayName(ChatColor.GRAY + name);
			l.add(battleOfBlocks.msg.putColorRemovePNC(battleOfBlocks.msg.ALREADY_BOUGHT));
		} else {
			String permission;
			if(perm == "none"){
				permission = null;
			} else if(perm == null){
				permission = null;
			} else {
				permission = perm;
			}
			if(permission == null){
				l.add(ChatColor.GOLD + "Price: " + price);
				if(canpay(p, price)) {
					im.setDisplayName(ChatColor.GREEN + name);
					l.add(ChatColor.GREEN + "Clic here to buy !");
				} else {
					im.setDisplayName(ChatColor.RED + name);
					l.add(battleOfBlocks.msg.putColorRemovePNC(battleOfBlocks.msg.NOT_ENOUGH_MONEY));
				}
			} else {
				if(battleOfBlocks.hasPermission(p, permission)){
					l.add(ChatColor.GOLD + "Price: " + price);
					if(canpay(p, price)) {
						im.setDisplayName(ChatColor.GREEN + name);
						l.add(ChatColor.GREEN + "Clic here to buy !");
					} else {
						im.setDisplayName(ChatColor.RED + name);
						l.add(battleOfBlocks.msg.putColorRemovePNC(battleOfBlocks.msg.NOT_ENOUGH_MONEY));
					}
				} else {
					im.setDisplayName(ChatColor.RED + name);
					l.add(battleOfBlocks.msg.putColorRemovePNC(battleOfBlocks.msg.PERMISSION_DENIED));
				}
			}
		}
		l.add(ChatColor.GOLD + sousname);
		im.setLore(l);
		i.setItemMeta(im);
		return i;
	}
	
	public Inventory getInvShop(Player p){
		Inventory inv;
		inv = Bukkit.createInventory(p,9,ChatColor.DARK_AQUA + "BattleOfBlocks Shop !");
		inv.setMaxStackSize(999);
		ItemStack isPerks = new ItemStack(Material.EMERALD);
		ItemMeta imPerks = isPerks.getItemMeta();
		imPerks.setDisplayName(ChatColor.YELLOW + "Perks");
		isPerks.setItemMeta(imPerks);
		ItemStack isKits = new ItemStack(Material.IRON_SWORD);
		ItemMeta imKits = isKits.getItemMeta();
		imKits.setDisplayName(ChatColor.DARK_RED + "Kits");
		isKits.setItemMeta(imKits);
		inv.setItem(2, isPerks);
		inv.setItem(6, isKits);
		return inv;
	}
	
	public Inventory getInvShopPerks(Player p){
		Inventory inv;
		ItemStack back_arrow = new ItemStack(Material.ARROW, 1);
		ItemMeta im_back_arrow = back_arrow.getItemMeta();
		im_back_arrow.setDisplayName("Menu !");
		back_arrow.setItemMeta(im_back_arrow);
		if(battleOfBlocks.ca.getList().size() >= 1){
			inv = Bukkit.createInventory(p,27,ChatColor.DARK_AQUA + "BattleOfBlocks Perks Shop !");
			inv.setItem(26, back_arrow);
		} else {
			inv = Bukkit.createInventory(p,36,ChatColor.DARK_AQUA + "BattleOfBlocks Perks Shop !");
			inv.setItem(35, back_arrow);
		}
		inv.setMaxStackSize(999);
		CustomAdd ca = battleOfBlocks.ca;
		inv.addItem(getISPerk(Material.BRICK, "forcefield", "Have a forefield for 7 secs !", 4000, p, null));
		inv.addItem(getISPerk(Material.IRON_BLOCK , "ironman", "Go on an iron golem for 10 sec !", 2000, p, null));
		inv.addItem(getISPerk(Material.STICK, "knockback", "Have a knockback X stick for 15 secs !", 2500, p, null));
		inv.addItem(getISPerk(Material.FIREBALL, "fireball", "Shot fireballs with your stick for 10 secs !", 5000, p, null));
		inv.addItem(getISPerk(Material.BEDROCK, "berserk", "Become a berserk for 10 sec !", 6500, p, null));
		inv.addItem(getISPerk(Material.TNT, "tntboom", "Kill your ennemies !", 3500, p, null));
		List<CustomAddPower> l = ca.getList();
		for(int i = 0; i < l.size(); i++){
			CustomAddPower cap = l.get(i);
			inv.addItem(getISPerk(cap.m, cap.name, cap.description, cap.pricetobuy, p, cap.perm));
		}
		return inv;
	}
	
	public Inventory getInvShopKits(Player p){
		Inventory inv;
		ItemStack back_arrow = new ItemStack(Material.ARROW, 1);
		ItemMeta im_back_arrow = back_arrow.getItemMeta();
		im_back_arrow.setDisplayName("Menu !");
		back_arrow.setItemMeta(im_back_arrow);
		if(battleOfBlocks.ca.getList().size() >= 1){
			inv = Bukkit.createInventory(p,27,ChatColor.DARK_AQUA + "BattleOfBlocks Kits Shop !");
			inv.setItem(26, back_arrow);
		} else {
			inv = Bukkit.createInventory(p,36,ChatColor.DARK_AQUA + "BattleOfBlocks Kits Shop !");
			inv.setItem(35, back_arrow);
		}
		inv.setMaxStackSize(999);
		inv.addItem(getISKit(Material.INK_SACK, "Spy", "Super Secret!", 3000, p, null));
		inv.addItem(getISKit(new SpawnEgg(EntityType.BLAZE).toItemStack(), "Phoenix", "Burn baby burn!", 800, p, null));
		inv.addItem(getISKit(Material.VINE, "Shamen", "Nature at your side", 800, p, null));
		inv.addItem(getISKit(Material.STICK, "Healer", "I love healing people!!", 200, p, null));
		inv.addItem(getISKit(Material.BOOK_AND_QUILL, "Troll", "TROLOLOLOL!", 600, p, null));
		inv.addItem(getISKit(Material.TNT, "Fury", "Im not happy with you!!!", 500, p, null));
		if(battleOfBlocks.kits != null){
			List<Kit> v = battleOfBlocks.kits.v;
			for(int i = 0; i < v.size(); i++){
				Kit k = v.get(i);
				inv.addItem(getISKit(k.m, k.name, k.des, k.priceToBuy, p, k.perm));
			}
		}
		return inv;
	}
	
	@EventHandler
	public void invfunct(InventoryClickEvent event){
		if(event.getInventory().getTitle().equals(ChatColor.DARK_AQUA + "BattleOfBlocks Perks Shop !")) {
			if(event.getWhoClicked() instanceof Player){
				int inv = 0;
				Player p = (Player) event.getWhoClicked();
				if(event.getCurrentItem() != null) {
					if(event.getCurrentItem().getItemMeta() != null){
						if(event.getCurrentItem().getItemMeta().getDisplayName() != null){
							Material m = event.getCurrentItem().getType();
							String name = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
							if(m == Material.ARROW){
								inv = 2;
							} else {
								if(name == null){
									event.setCancelled(true);
									p.closeInventory();
								}
								if(hasBoughtPerk(p, name)){
									p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.ALREADY_BOUGHT));
								} else {
									if(name == "tntboom"){
										if(canpay(p, 3500)){
											buyPerk(p,name, 3500);
											inv = 1;
										}
										else p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.NOT_ENOUGH_MONEY));
									} else if(name == "fireball"){
										if(canpay(p, 5000)){
											buyPerk(p,name, 5000);
											inv = 1;
										}
										else p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.NOT_ENOUGH_MONEY));
									} else if(name == "forcefield"){
										if(canpay(p, 4000)){
											buyPerk(p,name, 4000);
											inv = 1;
										}
										else p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.NOT_ENOUGH_MONEY));
									} else if(name == "ironman"){
										if(canpay(p, 2000)){
											buyPerk(p,name, 2000);
											inv = 1;
										} 
										else p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.NOT_ENOUGH_MONEY));
									} else if(name == "knockback"){
										if(canpay(p, 2500)){
											buyPerk(p,name, 2500);
											inv = 1;
										}
										else p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.NOT_ENOUGH_MONEY));
									} else if(name == "berserk"){
										if(canpay(p, 6500)){
											buyPerk(p,name, 6500);
											inv = 1;
										}
										else p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.NOT_ENOUGH_MONEY));
									} else {
										for(int i = 0; i < battleOfBlocks.ca.getList().size(); i++){
											CustomAddPower cap = battleOfBlocks.ca.getList().get(i);
											if(cap.name == name){
												if(battleOfBlocks.hasPermission(p, cap.perm)){
													if(canpay(p, cap.pricetobuy)){
														buyPerk(p, name, cap.pricetobuy);
														inv = 1;
													} else {
														p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.NOT_ENOUGH_MONEY));
													}
												} else {
													p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.PERMISSION_DENIED));
												}
												break;
											}
										}
									}
								}
							}
						}
					}
				}
				event.setCancelled(true);
				if(inv == 2) p.openInventory(getInvShop(p));
				else p.openInventory(getInvShopPerks(p));
			}
		}
	}
	
	@EventHandler
	public void invfunct2(InventoryClickEvent event){
		if(event.getInventory().getTitle().equals(ChatColor.DARK_AQUA + "BattleOfBlocks Shop !")){
			if(event.getWhoClicked() instanceof Player){
				Player p = (Player) event.getWhoClicked();
				if(event.getCurrentItem() != null)
				if(event.getCurrentItem().getType() != null){
					Material m = event.getCurrentItem().getType();
					if(m == Material.EMERALD){
						event.setCancelled(true);
						p.openInventory(getInvShopPerks(p));
					} else if(m == Material.IRON_SWORD){
						event.setCancelled(true);
						p.openInventory(getInvShopKits(p));
					}
				}
			}
		}
	}
		@EventHandler
		public void invfunct3(InventoryClickEvent event){
		if(event.getInventory().getTitle().equals(ChatColor.DARK_AQUA + "BattleOfBlocks Kits Shop !")) {
			if(event.getWhoClicked() instanceof Player){
				int inv = 0;
				Player p = (Player) event.getWhoClicked();
				if(event.getCurrentItem() != null) {
					if(event.getCurrentItem().getItemMeta() != null){
						if(event.getCurrentItem().getItemMeta().getDisplayName() != null){
							Material m = event.getCurrentItem().getType();
							String name = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
							if(m == Material.ARROW){
								inv = 2;
							} else {
								if(hasBoughtPerk(p, name)){
									p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.ALREADY_BOUGHT));
								} else {
									if(battleOfBlocks.kits != null){
										if(battleOfBlocks.kits.v != null){
											for(int i = 0; i < battleOfBlocks.kits.v.size(); i++){
												Kit k = battleOfBlocks.kits.v.get(i);
												if(k.isCorrect()){
													if(k.name.equalsIgnoreCase(name)){
														if(battleOfBlocks.hasPermission(p, k.perm)){
															if(canpay(p, k.priceToBuy)) {
																buyKit(p, k.name, k.priceToBuy);
																inv = 1;
															} else {
																p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.NOT_ENOUGH_MONEY));
															}
														} else {
															p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.PERMISSION_DENIED));
														}
														break;
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
				event.setCancelled(true);
				if(inv == 2) p.openInventory(getInvShop(p));
				else p.openInventory(getInvShopKits(p));
			}
		}
	}
	
	public void startShop(Player p){
		p.openInventory(getInvShop(p));
	}
}