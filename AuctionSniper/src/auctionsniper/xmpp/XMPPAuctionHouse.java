package auctionsniper.xmpp;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
	private static final String LOGGER_NAME = "XMPPFailure";
	private static final String LOG_FILE_NAME = "auction-sniper.log";
	
	private XMPPConnection connection;
	private XMPPFailureReporter failureReporter;

	public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException, XMPPAuctionException {
		return new XMPPAuctionHouse(hostname, username, password);
	}

	public XMPPAuctionHouse(String hostname, String username, String password) throws XMPPException, XMPPAuctionException {
		this.connection = connection(hostname, username, password);
		this.failureReporter = new LoggingXMPPFailureReporter(makeLogger());
	}
	
	private Logger makeLogger() throws XMPPAuctionException {
		Logger logger = Logger.getLogger(LOGGER_NAME);
		logger.setUseParentHandlers(false);
		logger.addHandler(simpleFileHandler());
		return logger;
	}
	
	private FileHandler simpleFileHandler() throws XMPPAuctionException {
		try {
			FileHandler handler = new FileHandler(LOG_FILE_NAME);
			handler.setFormatter(new SimpleFormatter());
			return handler;
		} catch (Exception e) {
			throw new XMPPAuctionException("Could not create logger FileHandler "
					+ LOG_FILE_NAME, e);
		}
	}
	
	private XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		return connection;
	}


	@Override
	public Auction auctionFor(String itemId) {
		return new XMPPAuction(connection, auctionId(itemId, connection), failureReporter);
	}
	

	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}



	@Override
	public void disconnect() {
		connection.disconnect();
	}
}
