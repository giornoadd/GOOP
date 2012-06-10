package auctionsniper;

public class SniperPortfolio implements SniperCollector  {	

	private final Announcer<PortfolioListener> portfoiloListeners = Announcer.to(PortfolioListener.class);
	
	public void addPortfolioListener(PortfolioListener portfolioListener) {
		portfoiloListeners.addListener(portfolioListener);
	}


	@Override
	public void addSniper(AuctionSniper auctionSniper) {
		portfoiloListeners.announce().sniperAdded(auctionSniper);
	}
}
