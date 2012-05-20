package auctionsniper;

import auctionsniper.stub.FakeAuctionServer;
import auctionsniper.ui.AuctionSniperDriver;

import static auctionsniper.stub.FakeAuctionServer.*;
import static auctionsniper.ui.MainWindow.*;

public class ApplicationRunner {

	private String itemId;
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = "sniper@nboahp103014/Auction";
	private AuctionSniperDriver driver;
	
	
	public void startBiddingIn(final FakeAuctionServer auction) {
		itemId = auction.getItemId();
		Thread thread = new Thread("Test Application") {
			@Override public void run() {
				try {
					Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.showsSniperStatus(STATUS_JOINING);
	}

	public void showsSniperHasLostAuction() {
		driver.showsSniperStatus(STATUS_LOST);
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}

	public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
		driver.showsSniperStatus(itemId, lastPrice, lastBid, STATUS_BIDDING);
		
	}

	public void showsSniperHasWonAuction(int lastBid) {
		driver.showsSniperStatus(itemId, lastBid, lastBid, STATUS_WON);
	}

	public void showsSniperIsWinningAuction(int winningBid) {
		driver.showsSniperStatus(itemId, winningBid, winningBid, STATUS_WINNIG);		
	}

}
