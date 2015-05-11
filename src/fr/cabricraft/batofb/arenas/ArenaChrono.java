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

package fr.cabricraft.batofb.arenas;

import org.bukkit.ChatColor;

import fr.cabricraft.batofb.BattleOfBlocks;

public class ArenaChrono implements Runnable {

	Arena arena;
	int action;
	int ok = 1;
	int time = BattleOfBlocks.time;
	int option;
	
	public ArenaChrono(Arena arena, int action, int option){
		this.arena = arena;
		this.action = action;
		this.option = option;
	}
	
	public void run() {
			if(action == 1) {
			  int sec = new Integer(time);
			  for(int i = 0; i < time; i++){
				  if(arena.battleOfBlocks.stop){
					  ok = 0;
					  break;
				  }
				  arena.updateBarWaitting(sec, time);
				  if(sec % 10 == 0 || sec < 11) arena.sendAll(arena.battleOfBlocks.msg.putColor(arena.battleOfBlocks.msg.stucturateTime(arena.battleOfBlocks.msg.GAME_START_IN_X_SECONDS, sec)));
				  sec--;
				  if(arena.getConnectedPlayers() == 0 || !arena.iswaiting || arena.getConnectedPlayers() < arena.startmin) {
					  ok = 0;
					  arena.updatebarjoin();
					  break;
				  }
				  try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			  }
			  if(ok == 1){
				  arena.sendAll(arena.PNC() + ChatColor.GREEN + "The game started !");
				  arena.startthegame();
			  }
				  
			  ok = 1;
		} else if(action == 2){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			arena.finishthegame(option);
		}
	}

}
