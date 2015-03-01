package fr.cabricraft.batofb.util;

public class IronGolemChorno implements Runnable {
	
	IronGolemControl igc;
	
	public IronGolemChorno(IronGolemControl igc) {
		this.igc = igc;
	}
	
	public void run(){
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		igc.removegolem();
	}

}
