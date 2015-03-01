package fr.cabricraft.batofb.powerups;

import java.util.LinkedList;
import java.util.List;

public class CustomAdd {
	List<CustomAddPower> l = new LinkedList<CustomAddPower>();
	
	public List<CustomAddPower> getList(){
		return l;
	}
	public void addCustomPower(CustomAddPower cp){
		l.add(cp);
	}
}
