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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.cabricraft.batofb.arenas.Arena;

public class IronGolemControl implements Runnable {
  Player p;
  Arena arena;
  Entity golem;
  
  boolean continueok = true;

  
  public IronGolemControl(Player p, Arena arena, Entity golem) {
    this.p = p;
    this.arena = arena;
    this.golem = golem;
  }
  
public void run() {
	Thread t = new Thread(new IronGolemChorno(this));
	t.start();
    Location locto;
    if (arena.getteam(this.p) == 1) locto = getbh(arena.vred);
    else if (arena.getteam(this.p) == 2) locto = getbh(arena.vblue);
    else return;
    
    golem.setPassenger(this.p);
    
    Block latestlocgolem = golem.getLocation().getBlock().getRelative(BlockFace.DOWN);
    
    while (continueok && arena.isstarted) {
    	Block heregolem = golem.getLocation().getBlock().getRelative(BlockFace.DOWN);
		if(latestlocgolem == heregolem){
			removegolem();
		}
		latestlocgolem = heregolem;
		
		if (getNearbyBlocks(locto, 5).contains(heregolem)) {
			removegolem();
		} else {
			org.bukkit.util.Vector vector = locto.toVector().subtract(heregolem.getLocation().toVector()).normalize().multiply(new org.bukkit.util.Vector(0.2, 0.2, 0.2));
			golem.setVelocity(vector);
		}
    }
    golem.eject();
    golem.remove();
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
  
  public void removegolem(){
	  continueok = false;
	  if(golem.isValid()) p.sendMessage(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.IRON_GOLEM_DIE));
  }
  
  public Location getbh(java.util.Vector<Location> v){
	  int i = (int)(Math.random() * (v.size()));
	  Location loc = v.elementAt(i);
	  return loc;
  }
}