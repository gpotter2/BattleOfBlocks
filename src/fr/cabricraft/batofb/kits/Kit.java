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

package fr.cabricraft.batofb.kits;

import java.util.List;

import org.bukkit.Material;

import fr.cabricraft.batofb.util.Verified;

public class Kit implements Verified {
	//REQUIRED
	public List<ItemsKit> v;
	public String name;
	public String des;
	public Material m;
	//OPTIONAL
	public String perm;
	public int priceToBuy;
	
	Kit(List<ItemsKit> v, String name, String des, Material m, String perm, int priceToBuy){
		this.v = v;
		this.name = name;
		this.des = des;
		this.m = m;
		this.perm = perm;
		this.priceToBuy = priceToBuy;
	}

	public boolean isCorrect() {
		if(v != null && name != null && des != null && m != null){
			return true;
		}
		return false;
	}
}
