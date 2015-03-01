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
