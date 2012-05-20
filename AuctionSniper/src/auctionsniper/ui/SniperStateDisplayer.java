package auctionsniper.ui;

import javax.swing.SwingUtilities;

import auctionsniper.SniperListener;
import auctionsniper.SniperState;

public class SniperStateDisplayer implements SniperListener {
	MainWindow ui;
	
	public SniperStateDisplayer(MainWindow ui) {
		this.ui = ui;
	}
	
	@Override
	public void sniperLost() {
		showStatus(MainWindow.STATUS_LOST);
	}
	@Override
	public void sniperWinning() {
		showStatus(MainWindow.STATUS_WINNIG);			
	}
	
	private void showStatus(final String status) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { ui.showStatus(status); }
		});
	}
	@Override
	public void sniperWon() {
		showStatus(MainWindow.STATUS_WON);
		
	}
	@Override
	public void sniperBidding(final SniperState state) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ui.sniperStatusChanged(state, MainWindow.STATUS_BIDDING);
			}
		});
	}
}