package auctionsniper;

public class SniperLauncher implements UserRequestListener {

	private final AuctionHouse auctionHouse;
	private final SniperCollector portfolio;
	
	public SniperLauncher(AuctionHouse auctionHouse, SniperCollector portfolio){
		this.auctionHouse = auctionHouse;
		this.portfolio = portfolio;
	}
	
	@Override
	public void joinAuction(String itemId) {
		Auction auction = auctionHouse.auctionFor(itemId);
		AuctionSniper sniper = new AuctionSniper(itemId, auction);
		auction.addAuctionEventListener(sniper);
		portfolio.addSniper(sniper);
		auction.join();
	}
}