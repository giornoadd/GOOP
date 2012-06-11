package auctionsniper.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionEventListener.PriceSource;

public class AuctionMessageTranslator implements MessageListener {
	final String sniperId;
	private AuctionEventListener listener;
	private XMPPFailureReporter failureReporter;
		
	public AuctionMessageTranslator(String sniperId, AuctionEventListener listener, XMPPFailureReporter failureReporter) {
		this.listener = listener;
		this.sniperId = sniperId;
		this.failureReporter = failureReporter;
	}

	@Override
	public void processMessage(Chat aChat, Message message) {
		String messageBody = message.getBody();
		try{
			translate(messageBody);
		} catch(Exception exception) {
			failureReporter.cannotTranslateMessage(sniperId, messageBody, exception);
			listener.auctionFailed();
		}
	}

	private void translate(String message) throws MissingValueException {
		AuctionEvent event = AuctionEvent.from(message);
		String eventType = event.type();
		if ("CLOSE".equals(eventType)) {
			listener.auctionClosed();
		} else if ("PRICE".equals(eventType)) {
			listener.currentPrice(event.currentPrice(),event.increment(), event.isFrom(sniperId));
		}
	}
	
	private static class AuctionEvent {
		private final Map<String, String> fields = new HashMap<String, String>();
		public String type() throws MissingValueException
		{ return get("Event");}
		public PriceSource isFrom(String sniperId) throws MissingValueException {
			return sniperId.equals(bidder()) ? PriceSource.FromSniper : PriceSource.FromOtherBidder;
		}
		
		private String bidder() throws MissingValueException  { return get("Bidder"); }
		public int currentPrice() throws MissingValueException  { return getInt("CurrentPrice");}
		public int increment() throws MissingValueException {return getInt("Increment");}
		
		private int getInt(String fieldName) throws MissingValueException {
			return Integer.parseInt(get(fieldName));
		}
		private String get(String fieldName) throws MissingValueException {
			String value = fields.get(fieldName);
			if (null == value) {
				throw new MissingValueException(fieldName);
				}
			return value;
		}
		
		private void addField(String field){
			String[] pair = field.split(":");
			fields.put(pair[0].trim(), pair[1].trim());
		}
		static AuctionEvent from(String messageBody) {
			AuctionEvent event = new AuctionEvent();
			for(String field : fieldsIn(messageBody)) {
				event.addField(field);
			}
			return event;
		}
		static String[] fieldsIn(String messageBody){
			return messageBody.split(";");
		}
		
	}
}
