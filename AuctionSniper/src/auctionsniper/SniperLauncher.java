package auctionsniper;

public class SniperLauncher implements UserRequestListener {

	private final AuctionHouse auctionHouse;
	private final SniperCollector portfolio;
	
	public SniperLauncher(AuctionHouse auctionHouse, SniperCollector portfolio){
		this.auctionHouse = auctionHouse;
		this.portfolio = portfolio;
	}
	
	@Override
	public void joinAuction(Item item) {
		Auction auction = auctionHouse.auctionFor(item.identifier);
		AuctionSniper sniper = new AuctionSniper(item, auction);
		auction.addAuctionEventListener(sniper);
		portfolio.addSniper(sniper);
		auction.join();
	}
}