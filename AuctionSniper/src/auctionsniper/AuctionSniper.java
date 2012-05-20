package auctionsniper;

public class AuctionSniper implements AuctionEventListener {

	private boolean isWinning = false;
	private final SniperListener sniperListener;
	private final Auction auction;
	private final String itemId = "item-54321";
	public static final String ITEM_ID = "item-54321";
	
	public AuctionSniper(Auction auction, SniperListener sniperListener) {
		this.sniperListener = sniperListener;
		this.auction = auction;
	}

	@Override
	public void auctionClosed() {
		if (isWinning){
			sniperListener.sniperWon();
		} else {
			sniperListener.sniperLost();
		}
	}

	@Override
	public void currentPrice(int price, int increment, PriceSource priceSource) {
		isWinning = (priceSource == PriceSource.FromSniper);
		if (isWinning){
			sniperListener.sniperWinning();
		} else {
			int bid = price + increment;
			auction.bid(price + increment);
			sniperListener.sniperBidding(new SniperState(itemId, price, bid));
		}
	}
}
