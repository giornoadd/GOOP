package auctionsniper.xmpp;

@SuppressWarnings("serial")
public class XMPPAuctionException extends Exception {

	public XMPPAuctionException(String message, Exception internalException) {
		super(message, internalException);
	}

}
