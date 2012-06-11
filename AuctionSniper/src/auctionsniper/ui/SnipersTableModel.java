package auctionsniper.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.objogate.exception.Defect;

import auctionsniper.AuctionSniper;
import auctionsniper.PortfolioListener;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperSnapshot.SniperState;

@SuppressWarnings("serial")
public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {
	private List<SniperSnapshot> snapshots = new ArrayList<>();	

	public enum Column {
		ITEM_IDENTIFIER("Item") {
			@Override public Object valueIn(SniperSnapshot snapshot) {
				return snapshot.itemId;
			}
		}, 
		LAST_PRICE("Last Price") {
			@Override public Object valueIn(SniperSnapshot snapshot) {
				return snapshot.lastPrice;
			}
		},
		LAST_BID("Last Bid") {
			@Override public Object valueIn(SniperSnapshot snapshot) {
				return snapshot.lastBid;
			}
		},
		SNIPER_STATE("State") {
			@Override public Object valueIn(SniperSnapshot snapshot) {
				return SnipersTableModel.textFor(snapshot.state);
			}
		};
		public final String name; 
		abstract public Object valueIn(SniperSnapshot snapshot);
		
		public static Column at(int offset) { return values()[offset]; }
		private Column(String name) {
			this.name = name;
		}
	}
	
	private final static String[] STATUS_TEXT = {
		"Joining", "Bidding", "Failed", "Winning", "Losing", "Lost", "Won"
	};
	
	public static String textFor(SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}
	@Override 
	public String getColumnName(int column){
		return Column.at(column).name;
	}
	@Override
	public int getRowCount() {
		return snapshots.size();
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
	}
	
	@Override
	public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
		int row = rowMatching(newSniperSnapshot);
		snapshots.set(row, newSniperSnapshot);
		fireTableRowsUpdated(row, row);
	}
	
	private int rowMatching(SniperSnapshot snapshot) {
		for(int i = 0; i < snapshots.size(); i++) {
			if (snapshot.isForSameItemsAs(snapshots.get(i))){
				return i;
			}
		}
		throw new Defect("Cannot find match for " + snapshot);
	}
	
	private void addSniperSnapshot(SniperSnapshot sniperSnapshot) {
		snapshots.add(sniperSnapshot);
		int row = snapshots.size() - 1;
		fireTableRowsInserted(row, row);
	}

	@Override
	public void sniperAdded(AuctionSniper auctionSniper) {
		addSniperSnapshot(auctionSniper.getSnapshot());
		auctionSniper.addSniperListener(new SwingThreadSniperListener(this));
	}
}