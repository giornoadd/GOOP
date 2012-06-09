package auctionsniper;

import auctionsniper.Auction;

public interface AuctionHouse {
	Auction auctionFor(String itemId);
	void disconnect();
}
