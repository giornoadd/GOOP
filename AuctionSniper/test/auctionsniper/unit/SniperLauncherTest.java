package auctionsniper.unit;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperCollector;
import auctionsniper.SniperLauncher;

@RunWith(JMock.class)
public class SniperLauncherTest {
	private final Mockery context = new Mockery();
	private final SniperCollector sniperCollector = context.mock(SniperCollector.class);
	private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
	private final Auction auction = context.mock(Auction.class);
	private final States auctionState = context.states("auction state").startsAs("not joined");
	
	private final SniperLauncher luncher = new SniperLauncher(auctionHouse, sniperCollector);

	@Test
	public void addsNewSniperToCollectionsAndThenJoinsAuction() {
		final String itemId = "item 123";
		context.checking(new Expectations() {{
			allowing(auctionHouse).auctionFor(itemId); will(returnValue(auction));
			allowing(auction).join(); then(auctionState.is("joined"));
			oneOf(auction).addAuctionEventListener(with(sniperForItem(itemId))); when(auctionState.is("not joined"));
			oneOf(sniperCollector).addSniper(with(sniperForItem(itemId))); when(auctionState.is("not joined"));
		}});
		luncher.joinAuction(itemId);
	}

	protected Matcher<AuctionSniper> sniperForItem(String itemId) {
		return new FeatureMatcher<AuctionSniper, String>(equalTo(itemId), 
				"sniper that is ", 
				"was") {
			@Override
			protected String featureValueOf(AuctionSniper actual) {
				return actual.getSnapshot().itemId;
			}
		};
	}
}

