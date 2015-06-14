package fr.cabricraft.batofb.voting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import fr.cabricraft.batofb.util.Verified;

public class MapSign implements Verified {

	private Location loc = null;
	private String map_voting = null;
	
	public MapSign(Location loc, String map_voting){
		this.loc = loc;
		this.map_voting = map_voting;
	}
	
	public Location getLocation(){
		return loc;
	}
	
	public String getMapName(){
		return map_voting;
	}
	
	@Override
	public boolean isCorrect() {
		if(loc == null || map_voting == null || getSign() == null){
			return false;
		}
		return true;
	}
	
	public Sign getSign(){
		if(loc != null){
			Block b = loc.getBlock();
			if(b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST) {
				return ((Sign) b.getState());
			}
		}
		return null;
	}
}
