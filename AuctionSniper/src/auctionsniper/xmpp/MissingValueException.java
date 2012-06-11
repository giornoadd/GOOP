package auctionsniper.xmpp;

@SuppressWarnings("serial")
public class MissingValueException extends Exception {

	public MissingValueException(String fieldName) {
		super(fieldName);
	}

}
