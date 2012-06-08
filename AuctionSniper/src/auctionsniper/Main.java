package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SwingThreadSniperListener;

public class Main {
	public static final String MAIN_WINDOW_NAME = "Auction Sniper";
	public static final String SNIPER_STATUS_NAME = "sniper status";
	
	
	private static final int ARG_HOSTNAME = 0;
	private static final int ARG_USERNAME = 1;
	private static final int ARG_PASSWORD = 2;
	
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	
	public static final String PEICE_COMMAND_FORMAT = "SOLVersion: 1.1; Event: CLOSE;";
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	
	private final SnipersTableModel snipers = new SnipersTableModel();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Collection<Auction> notToBeGCd = new ArrayList();
	
	private MainWindow ui;
	
	public Main() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				ui = new MainWindow(snipers);
			}
		});
	}

	public static void main(String... args) throws Exception {
		Main main = new Main();
		XMPPConnection connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
		main.disconnectWhenUICloses(connection);
		main.addUserRequestListenerFor(connection);
	}
	
	private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		return connection;
	}
	
	private void addUserRequestListenerFor(final XMPPConnection connection) {
		ui.addUserRequestListener(new UserRequestListener() {
			@Override
			public void joinAuction(String itemId) {
				snipers.addSniper(SniperSnapshot.joining(itemId));
				Auction auction = new XMPPAuction(connection, itemId);
				notToBeGCd.add(auction);
				auction.addAuctionEventListener(new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers)));
				auction.join();
			}
		});
	}
	
	private void disconnectWhenUICloses(final XMPPConnection connection) {
		ui.addWindowListener(new WindowAdapter() {
			@Override public void windowClosed(WindowEvent e) {
				connection.disconnect();
			}
		});
	}
}