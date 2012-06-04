package auctionsniper.stub;

import auctionsniper.Main;
import auctionsniper.SniperSnapshot.SniperState;
import auctionsniper.stub.FakeAuctionServer;
import auctionsniper.ui.AuctionSniperDriver;
import auctionsniper.ui.MainWindow;

import static auctionsniper.stub.FakeAuctionServer.*;
import static auctionsniper.ui.MainWindow.*;

public class ApplicationRunner {

	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = "sniper@nboahp103014/Auction";
	private AuctionSniperDriver driver;
	
	

	public void startBiddingIn(final FakeAuctionServer... auctions) {
		Thread thread = new Thread("Test Application") {
			@Override public void run() {
				try {
					Main.main(arguments(auctions));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.hasTitle(MainWindow.APPLICATION_TITLE);
		driver.hasColumnTitles();
		for (FakeAuctionServer auction : auctions) {
			driver.showsSniperStatus(auction.getItemId(), 0, 0, auctionsniper.ui.SnipersTableModel.textFor(SniperState.JOINING));
		}
	}
	
	protected static String[] arguments(FakeAuctionServer... auctions) {
		String[] arguments = new String[auctions.length + 3];
		arguments[0] = XMPP_HOSTNAME;
		arguments[1] = SNIPER_ID;
		arguments[2] = SNIPER_PASSWORD;
		for (int i = 0; i < auctions.length; i++) {
			arguments[i + 3] = auctions[i].getItemId();
		}
		return arguments;
	}


	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}

	public void hasShownSniperIsBidding(final FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, STATUS_BIDDING);	
	}

	public void showsSniperHasLostAuction(FakeAuctionServer auction,int lastPrice, int lastBid)
	{
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, STATUS_LOST);
	}

	public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
		driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, STATUS_WINNIG);		
	}

	public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastBid, lastBid, STATUS_WON);
	}

}
