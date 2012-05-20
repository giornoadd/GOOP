package auctionsniper.ui;

import javax.swing.SwingUtilities;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;

public class SniperStateDisplayer implements SniperListener {
	MainWindow ui;
	
	public SniperStateDisplayer(MainWindow ui) {
		this.ui = ui;
	}
	
	@Override
	public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ui.sniperStateChanged(sniperSnapshot);
			}
		});
	}
}