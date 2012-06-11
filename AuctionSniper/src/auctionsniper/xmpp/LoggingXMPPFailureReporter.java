package auctionsniper.xmpp;

import java.util.logging.Logger;

import static java.lang.String.format;

public class LoggingXMPPFailureReporter implements XMPPFailureReporter {
	
	private final Logger logger;
	private final static String LOG_FORMAT = "<%s> Could not translate message \"%s\" because \"%s\"";
	
	public LoggingXMPPFailureReporter(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void cannotTranslateMessage(String auctionId, String failedMessage,
			Exception exception) {
		this.logger.severe(format(LOG_FORMAT, auctionId, failedMessage, exception.toString()));

	}

}
