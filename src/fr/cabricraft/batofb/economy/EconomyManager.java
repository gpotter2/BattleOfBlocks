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

package fr.cabricraft.batofb.economy;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.entity.Player;

import fr.cabricraft.batofb.BattleOfBlocks;

public class EconomyManager {
	
	public EconomyManager(Player p, int MoneyToAdd, BattleOfBlocks battleOfBlocks){
		if(battleOfBlocks.ecoenabled && battleOfBlocks.vaultenabled_economy){
			EconomyResponse r;
			r = battleOfBlocks.econ.depositPlayer(p, MoneyToAdd);
			if(r.transactionSuccess()){
				p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.structurateMoney(battleOfBlocks.msg.MONEY_EARN, battleOfBlocks.econ.format(r.amount))));
				p.sendMessage(battleOfBlocks.msg.putColor(battleOfBlocks.msg.structurateMoney(battleOfBlocks.msg.MONEY_NOW, battleOfBlocks.econ.format(r.balance))));
			}
		}
	}
}
