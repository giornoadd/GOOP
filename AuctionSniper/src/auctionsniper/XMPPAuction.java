package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import static java.lang.String.*;
import static auctionsniper.Main.AUCTION_ID_FORMAT;

public class XMPPAuction implements Auction {
	public static final String PEICE_COMMAND_FORMAT = "SOLVersion: 1.1; Event: CLOSE;";
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	
	private final Announcer<AuctionEventListener> auctionEventListensers = Announcer.to(AuctionEventListener.class);
	private final Chat chat;
	
	public XMPPAuction(Chat chat){
		this.chat = chat;
	}
	
	public void bid(int amount) {
		sendMessage(format(BID_COMMAND_FORMAT, amount));
	}
	
	public void join() {
		sendMessage(JOIN_COMMAND_FORMAT);
	}
	
	private void sendMessage(final String message) {
		try{
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
	
	public XMPPAuction(XMPPConnection connection, String itemId) {
		chat = connection.getChatManager().createChat(auctionId(itemId, connection), new AuctionMessageTranslator(connection.getUser(), auctionEventListensers.announce()));
	}
	
	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}

	@Override
	public void addAuctionEventListener(AuctionEventListener auctionEventListener) {
		auctionEventListensers.addListener(auctionEventListener);
	}
}
