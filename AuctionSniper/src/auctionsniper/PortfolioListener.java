package auctionsniper;

import java.util.EventListener;

public interface PortfolioListener extends EventListener {
	void sniperAdded(AuctionSniper auctionSniper);
}
