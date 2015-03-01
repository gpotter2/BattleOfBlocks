package fr.cabricraft.batofb.kits;

import java.util.Vector;

import org.bukkit.Material;

public class Kit {
	public Vector<ItemsKit> v;
	public String name;
	public String des;
	public Material m;
	public String perm;
	public int priceToBuy;
	
	Kit(Vector<ItemsKit> v, String name, String des, Material m, String perm, int priceToBuy){
		this.v = v;
		this.name = name;
		this.des = des;
		this.m = m;
		this.perm = perm;
		this.priceToBuy = priceToBuy;
	}
}
