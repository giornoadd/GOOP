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


public final class XMPPAuction implements Auction {
	private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
	private final Chat chat;
	
	public XMPPAuction(XMPPConnection connection, String auctionJID, XMPPFailureReporter failureReporter) {
		AuctionMessageTranslator translator = translatorFor(connection, failureReporter);
		this.chat = connection.getChatManager().createChat(auctionJID, translator);
		addAuctionEventListener(chatDisconnectorFor(translator));
	}
	
	private AuctionMessageTranslator translatorFor(XMPPConnection connection, XMPPFailureReporter failureReporter) {
		return new AuctionMessageTranslator(connection.getUser(),auctionEventListeners.announce(), failureReporter);
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
	
	private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) {
		return new AuctionEventListener() {

			@Override
			public void auctionFailed() {
				chat.removeMessageListener(translator);
			}
			@Override
			public void auctionClosed() {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void currentPrice(int price, int increment,
					PriceSource priceSource) {
				// TODO Auto-generated method stub
				
			}
		};
	}


	@Override
	public void addAuctionEventListener(AuctionEventListener auctionEventListener) {
		auctionEventListeners.addListener(auctionEventListener);
	}
}
