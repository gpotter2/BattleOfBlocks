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

package fr.cabricraft.batofb.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.cabricraft.batofb.arenas.Arena;
import fr.cabricraft.batofb.util.ParticleEffect.ParticleProperty;

public class ParticleLauncher {
	
	Arena ar;
	
	public ParticleLauncher(Arena ar){
		this.ar = ar;
	}
	
	public List<LivingEntity> launchFirework(Player p, ParticleEffect pe){
		  Location loc1 = p.getEyeLocation();
		  Location loc2 = p.getTargetBlock((Set<Material>) null, 100).getLocation();
		  List<Location> list = l_straight(loc1, loc2);
		  List<LivingEntity> hitted = new LinkedList<LivingEntity>();
		  for(Location l : list){
			  	try {
			  	  Vector dir = loc2.toVector().subtract(loc1.toVector()).normalize().divide(new Vector(2, 2, 2));
			  	  playParticle(dir, l, pe);
				  List<Entity> le = getNearbyEntities(l, 2);
				  for(Entity e : le){
					  if(e instanceof LivingEntity){
						  if(e instanceof Player){
							  if(e != p) if(!hitted.contains(e)) hitted.add((LivingEntity) e);
						  } else {
							  if(!hitted.contains(e)) hitted.add((LivingEntity) e);
						  }
					  }
				  }
				} catch (Exception e) {
					e.printStackTrace();
				}
		  }
		   p.getWorld().playSound(p.getLocation(), Sound.EXPLODE, 0.1F, 2.0F);
		   p.getWorld().playSound(p.getLocation(), Sound.EXPLODE, 0.1F, 1.5F);
		   p.getWorld().playSound(p.getLocation(), Sound.EXPLODE, 0.1F, 1.4F);
		   p.getWorld().playSound(p.getLocation(), Sound.EXPLODE, 0.1F, 1.3F);
		   p.getWorld().playSound(p.getLocation(), Sound.EXPLODE, 0.1F, 1.2F);
		  for(LivingEntity le : hitted){
			  le.playEffect(EntityEffect.HURT);
		  }
		  return hitted;
	  }
	
	private List<Entity> getNearbyEntities(Location l, int radius){
        int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
        List<Entity> radiusEntities = new LinkedList<Entity>();
            for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
                for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
                    int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
                    for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
                        if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);
                    }
                }
            }
        return radiusEntities;
    }
	
	
	/**
	 * Get all the locations between 2 locations.
	 * 
	 * This is a code that was REALY hard to make, so please if you use it, leave my name :D
	 * 
	 * @param loc1 The first location
	 * @param loc2 The second location
	 * @author gpotter2
	 * @return The list of all the locations between the 2 locations
	 */
	private List<Location> l_straight(Location loc1, Location loc2){
		  World w = loc1.getWorld();
		  List<Location> l = new LinkedList<Location>();
		  
		  if(loc1 == loc2){
			  return null;
		  }
		  
		  double x1 = loc1.getX();
		  double y1 = loc1.getY();
		  double z1 = loc1.getZ();
		  
		  double x2 = loc2.getX();
		  double y2 = loc2.getY();
		  double z2 = loc2.getZ();
		  		  
		  double Cx = Math.abs((x2 - x1));
		  double Cy = Math.abs((y2 - y1));
		  double Cz = Math.abs((z2 - z1));
		  
		  int i;
		    
		  if((Cx >= Cy) && (Cx >= Cz)){
			  for(i = 0; i <= Cx; i++){
				  double x = x1 + (x2-x1)/Cx*i;
				  double y = y1 + (y2-y1)/Cx*i;
				  double z = z1 + (z2-z1)/Cx*i;
				  l.add(new Location(w, x, y, z));
			  }
		  } else if((Cy >= Cx) && (Cy >= Cz)){
			  for(i = 0; i <= Cy; i++){
				  double x = x1 + (x2-x1)/Cy*i;
				  double y = y1 + (y2-y1)/Cy*i;
				  double z = z1 + (z2-z1)/Cy*i;
				  l.add(new Location(w, x, y, z));
			  }
		  } else if((Cz >= Cx) && (Cz >= Cy)){
			  for(i = 0; i <= Cz; i++){
				  double x = x1 + (x2-x1)/Cz*i;
				  double y = y1 + (y2-y1)/Cz*i;
				  double z = z1 + (z2-z1)/Cz*i;
				  l.add(new Location(w, x, y, z));
			  }
		  }
		  return l;
	  }

	private boolean randomBoolean(){
		int al = 1 + (int)(Math.random() * 2);
		if(al > 1.5){
			return true;
		}
		return false;
	}
	
	private Location around(Location loc){
		double nbrX = ((int) (Math.random() * 10)) / 100;
		double nbrY = ((int) (Math.random() * 10)) / 100;
		double nbrZ = ((int) (Math.random() * 10)) / 100;
		return new Location(loc.getWorld(), randomBoolean() ? loc.getX() - nbrX : loc.getX() + nbrX, randomBoolean() ? loc.getY() - nbrY : loc.getY() + nbrY, randomBoolean() ? loc.getZ() - nbrZ : loc.getZ() + nbrZ);
	}
	
	private void playParticle(Vector direction, Location loc, ParticleEffect pe) {
		if(pe.hasProperty(ParticleProperty.DIRECTIONAL)){
			ParticleEffect.fromName(pe.getName()).display(direction, 1F, loc, ar.playersingame);
			ParticleEffect.fromName(pe.getName()).display(direction, 1F, loc, ar.playersingame);
		} else {
			ParticleEffect.fromName(pe.getName()).display(100, 100, 100, 1F, 2, around(loc), ar.playersingame);
			ParticleEffect.fromName(pe.getName()).display(100, 100, 100, 1F, 2, around(loc), ar.playersingame);
		}
    }
}
