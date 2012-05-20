package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperSnapshot.SniperState;

@SuppressWarnings("serial")
public class SnipersTableModel extends AbstractTableModel {
	private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
	private SniperSnapshot snapshot = STARTING_UP;
	
	public enum Column {
		ITEM_IDENTIFIER, 
		LAST_PRICE,
		LAST_BID,
		SNIPER_STATE;
		
		public static Column at(int offset) { return values()[offset]; }
	}
	private final static String[] STATUS_TEXT = {
		"Joining", "Bidding", "Winning", "Lost", "Won"
	};
	
	public int getColumnCount() {
		return Column.values().length;
	}
	public int getRowCount() {
		return 1;
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(Column.at(columnIndex)) {
		case ITEM_IDENTIFIER:
			return snapshot.itemId;
		case LAST_PRICE:
			return snapshot.lastPrice;
		case LAST_BID:
			return snapshot.lastBid;
		case SNIPER_STATE:
			return textFor(snapshot.state);
		default:
			throw new IllegalArgumentException("No column at " + columnIndex);	
		}
	}
	
	private String textFor(SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}

	public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
		snapshot = newSniperSnapshot;
		fireTableRowsUpdated(0,0);
	}	
}

