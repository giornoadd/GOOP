package auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Announcer;
import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;

import static java.lang.String.*;
import static auctionsniper.xmpp.XMPPAuctionHouse.JOIN_COMMAND_FORMAT;
import static auctionsniper.xmpp.XMPPAuctionHouse.BID_COMMAND_FORMAT;
import static auctionsniper.xmpp.XMPPAuctionHouse.AUCTION_ID_FORMAT;;


public class XMPPAuction implements Auction {
	private final Announcer<AuctionEventListener> auctionEventListensers = Announcer.to(AuctionEventListener.class);
	private final Chat chat;
	
	public XMPPAuction(XMPPConnection connection, String itemId) {
		chat = connection.getChatManager().createChat(auctionId(itemId, connection), new AuctionMessageTranslator(connection.getUser(), auctionEventListensers.announce()));
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
	
	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}

	@Override
	public void addAuctionEventListener(AuctionEventListener auctionEventListener) {
		auctionEventListensers.addListener(auctionEventListener);
	}
}
