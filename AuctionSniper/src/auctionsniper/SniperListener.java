package auctionsniper;

import java.util.EventListener;

import auctionsniper.SniperState;

public interface SniperListener extends EventListener {
	void sniperLost();
	
	void sniperWinning();

	void sniperWon();

	void sniperBidding(SniperState sniperState);
}
