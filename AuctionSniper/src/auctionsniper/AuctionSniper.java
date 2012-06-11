package auctionsniper;

public class AuctionSniper implements AuctionEventListener {

	private final Auction auction;
	private final Item item;
	private SniperSnapshot snapshot;
	private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
	
	public SniperSnapshot getSnapshot() {
		return snapshot;
	}
	
	public AuctionSniper(Item item, Auction auction) {
		this.auction = auction;
		this.item = item;
		this.snapshot = SniperSnapshot.joining(item.identifier);
	}

	@Override
	public void auctionClosed() {
		snapshot = snapshot.closed();
		notifyChange();
	}

	private void notifyChange() {
		listeners.announce().sniperStateChanged(snapshot);
	}

	@Override
	public void currentPrice(int price, int increment, PriceSource priceSource) {
		switch(priceSource) {
		case FromSniper:
			snapshot = snapshot.winning(price);
			break;
		case FromOtherBidder:
			int bid = price + increment;
			if (item.allowsBid(bid))
			{
				auction.bid(bid);
				snapshot = snapshot.bidding(price, bid);
			}else{
				snapshot = snapshot.losing(price);
			}
			break;
		}
		notifyChange();
	}
	
	public void addSniperListener(SniperListener sniperListener) {
		this.listeners.addListener(sniperListener);
	}

	@Override
	public void auctionFailed() {
		snapshot = snapshot.filed();
		notifyChange();
	}
}
