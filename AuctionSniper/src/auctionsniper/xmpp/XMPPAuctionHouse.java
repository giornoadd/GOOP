package auctionsniper.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.xmpp.XMPPAuction;

public class XMPPAuctionHouse implements AuctionHouse {

	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	
	public static final String PEICE_COMMAND_FORMAT = "SOLVersion: 1.1; Event: CLOSE;";
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	
	private XMPPConnection connection;

	public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException {
		return new XMPPAuctionHouse(hostname, username, password);
	}

	public XMPPAuctionHouse(String hostname, String username, String password) throws XMPPException {
		connection = connection(hostname, username, password);
	}
	
	private XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		return connection;
	}


	@Override
	public Auction auctionFor(String itemId) {
		return new XMPPAuction(connection, itemId);
	}


	@Override
	public void disconnect() {
		connection.disconnect();
	}
}
