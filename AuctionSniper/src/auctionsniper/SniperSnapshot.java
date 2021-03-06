package auctionsniper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.objogate.exception.Defect;

public class SniperSnapshot {
	public final String itemId;
	public final int lastPrice;
	public final int lastBid;
	public final SniperState state;
	
	public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState sniperState) {
		this.itemId = itemId;
		this.lastPrice = lastPrice;
		this.lastBid = lastBid;
		this.state = sniperState;
	}

	@Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    public enum SniperState {
    	JOINING {
    		@Override public SniperState whenAuctionClosed() { return LOST; }
    	},
    	BIDDING {
    		@Override public SniperState whenAuctionClosed() { return LOST; }
    	},
    	FAILED {
    		@Override public SniperState whenAuctionClosed() { return FAILED; }
    	},
    	WINNING {
    		@Override public SniperState whenAuctionClosed() { return WON; }
    	},
       	LOSING {
    		@Override public SniperState whenAuctionClosed() { return LOST; }
   		},
    	LOST,
    	WON;
    	public SniperState whenAuctionClosed() {
    		throw new Defect("Auction is already closed");
    	}
    }

	public static SniperSnapshot joining(String itemId) {
		return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
	}

	public SniperSnapshot filed() {
		return new SniperSnapshot(itemId, 0, 0, SniperState.FAILED);
	}

	public SniperSnapshot winning(int price) {
		return new SniperSnapshot(itemId, price, price, SniperState.WINNING);
	}
	
	public SniperSnapshot losing(int newLastPrice) {
		return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.LOSING);
	}

	public SniperSnapshot bidding(int price, int bid) {
		return new SniperSnapshot(itemId, price, bid, SniperState.BIDDING);
	}

	public SniperSnapshot closed() {
		return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
	}

	public boolean isForSameItemsAs(SniperSnapshot sniperSnapshot) {
		return this.itemId == sniperSnapshot.itemId;
	}
}