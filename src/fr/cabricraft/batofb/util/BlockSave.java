package fr.cabricraft.batofb.util;

import org.bukkit.Location;
import org.bukkit.Material;

public class BlockSave {
	public Material m;
	public Byte d;
	public Location l;
	public BlockSave(Material m, Location l, Byte d){
		this.m = m;
		this.d = d;
		this.l = l;
	}
}
