package auctionsniper.unit;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.AuctionSniper;
import auctionsniper.Item;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperSnapshot.SniperState;

import static org.hamcrest.Matchers.*;

@RunWith(JMock.class)
public class AuctionSniperTest {
	private final Mockery context = new Mockery();
	private final Auction auction = context.mock(Auction.class);
	private final SniperListener sniperListener = context.mock(SniperListener.class);
	private final int stopBidding = 1000;
	private final AuctionSniper sniper = new AuctionSniper(new Item(ITEM_ID, stopBidding ), auction);
	private final States sniperState = context.states("sniper");
	public static final String ITEM_ID = "item-54321";
	
	@Before
	public void setupListener() {
		sniper.addSniperListener(sniperListener);
	}
	
	@Test 
	public void reportsLostIfAuctionClosesImmediately() {
		context.checking(new Expectations() {{
			one(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));;
		}});
		sniper.auctionClosed();
	}
	
	@Test 
	public void reportsLostIfAuctionClosesWhenBidding() {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(
				aSniperThatIs(SniperState.BIDDING)));
					then(sniperState.is("bidding"));
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST))); when(sniperState.is("bidding"));
		}});
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
	}
	
	private Matcher<SniperSnapshot> aSniperThatIs(SniperState state) {
		return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), 
				"sniper that is ", 
				"was") {
			@Override
			protected SniperState featureValueOf(SniperSnapshot actual) {
				return actual.state;
			}
		};
	}

	@Test 
	public void reportsWonIfAuctionClosesWhenWinning() {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WINNING))); then(sniperState.is("Winning"));
			atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WON))); when(sniperState.is("Winning"));
		}});
		sniper.currentPrice(123, 45,PriceSource.FromSniper);
		sniper.auctionClosed();
	}
	
	@Test
	public void bidHigherAndReportsBiddingWhenNewPriceArrives() {
		final int price = 123;
		final int increment = 25;
		final int bid = price + increment;
		context.checking(new Expectations() {{
			one(auction).bid(price + increment);
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
		}});
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
	}
	
	@Test public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
		context.checking(new Expectations(){{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
				then(sniperState.is("bidding"));
			atLeast(1).of(sniperListener).sniperStateChanged(
					new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
						when(sniperState.is("bidding"));
		}});
		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
		sniper.currentPrice(135, 45, PriceSource.FromSniper);
	}
	
	@Test 
	public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
		allowingSniperBidding();
		context.checking(new Expectations() {{
			int bid = 123 + 45;
			allowing(auction).bid(bid);
			atLeast(1).of(sniperListener).sniperStateChanged(
					new SniperSnapshot(ITEM_ID, 2345, bid, SniperState.LOSING));
			when(sniperState.is("bidding"));
		}});
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
	}
	
	private void allowingSniperBidding() {
		context.checking(new Expectations() {{
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
			then(sniperState.is("bidding"));
		}});
	}

	//TODO add more test
//	doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice()
//	reportsLostIfAuctionClosesWhenLosing()
//	continuesToBeLosingOnceStopPriceHasBeenReached()
//	doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice()
	
	@Test 
	public void reportsFailedIfAuctionFailsWhenBidding() {
		ignoringAuction();
		allowingSniperBidding();
		expectSniperToFailWhenItIs("bidding");
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.auctionFailed();
	}
	
	private void ignoringAuction() {
		context.checking(new Expectations() {{
			ignoring(auction);
		}});
	}

	private void expectSniperToFailWhenItIs(final String state) {
		context.checking(new Expectations() {{
			atLeast(1).of(sniperListener).sniperStateChanged(
					new SniperSnapshot(ITEM_ID, 0, 0, SniperState.FAILED));
			when(sniperState.is(state));
		}});
	}
}
