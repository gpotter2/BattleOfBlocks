package fr.cabricraft.batofb.powerups;

import org.bukkit.entity.Player;

import fr.cabricraft.batofb.BattleOfBlocks;

public class PowerupsChrono implements Runnable {

	Powerups power;
	int action;
	int ok = 1;
	int time = BattleOfBlocks.time;
	Player p;
	Object option;
	
	public PowerupsChrono(Powerups power, int action, Player p, Object option){
		this.power = power;
		this.action = action;
		this.p = p;
		this.option = option;
	}
	
	public void run() {
		if(action == 1){
			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(power.arena.isinGame(p)){
				power.removeforcefield(p);
			}
		} else if(action == 2){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(power.arena.isinGame(p)){
				power.removefireball(p);
			}
		} else if(action == 3){
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(power.arena.isinGame(p)){
				power.removekb(p);
			}
		} else if(action == 4){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(power.arena.isinGame(p)){
				power.removeberserk(p);
			}
		} else if(action == 5){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(power.arena.isinGame(p)){
				power.removejump(p);
			}
		} else if(action == 6){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(power.arena.isinGame(p)){
				power.removeinvisible(p);
			}
		} else if(action == 7){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			power.removeWall(option);
		}
	}

}
