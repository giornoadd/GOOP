package auctionsniper.integration;

import static auctionsniper.stub.FakeAuctionServer.XMPP_HOSTNAME;

import java.util.concurrent.CountDownLatch;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.stub.ApplicationRunner;
import auctionsniper.stub.FakeAuctionServer;
import auctionsniper.xmpp.XMPPAuction;
import auctionsniper.xmpp.XMPPFailureReporter;

import static auctionsniper.stub.ApplicationRunner.SNIPER_ID;
import static auctionsniper.stub.ApplicationRunner.SNIPER_PASSWORD;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;
import static auctionsniper.xmpp.XMPPAuctionHouse.AUCTION_RESOURCE;
import static auctionsniper.xmpp.XMPPAuctionHouse.AUCTION_ID_FORMAT;

public class XMPPAuctionHouseTest {
	private final FakeAuctionServer server = new FakeAuctionServer("item-65432");
	private final XMPPFailureReporter failureReporter = new XMPPFailureReporter() {
		
		@Override
		public void cannotTranslateMessage(String auctionId, String failedMessage,
				Exception exception) {
			// TODO Auto-generated method stub
			
		}
	};
	private XMPPConnection connection;
	
	
	@Before
	public void init() throws XMPPException {
		connection = connection(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);	
		server.startSellingItem();
	}
	
	@Test 
	public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
		CountDownLatch auctionWasClosed = new CountDownLatch(1);
		Auction auction = new XMPPAuction(connection, auctionId(server.getItemId(), connection), failureReporter);
		auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
		auction.join();
		server.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		server.announceClosed();
		assertTrue("should have been closed", auctionWasClosed.await(2, SECONDS));
	}
	
	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}
	
	private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
		return new AuctionEventListener() {
			public void auctionClosed() { auctionWasClosed.countDown(); }
			public void currentPrice(int price, int increment, PriceSource priceSource) {
				// not implemented
			}
			public void auctionFailed(){
				// not implemented
			}
		};
	}
	
	
	@After 
	public void stopAuction() {
		server.stop();
	}
	
	
	private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		return connection;
	}

}
