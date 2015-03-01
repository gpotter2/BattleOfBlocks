package fr.cabricraft.batofb.powerups;

import org.bukkit.Material;

public class CustomAddPower {
	
	public Material m;
	public String command;
	public int price;
	public String description;
	public String name;
	public String perm;
	public int pricetobuy;
	
	CustomAddPower(Material m, String command, int price, String description, String name, String perm, int pricetobuy){
		this.m = m;
		this.price = price;
		this.description = description;
		this.command = command;
		this.name = name;
		this.perm = perm;
		this.pricetobuy = pricetobuy;
	}
}
