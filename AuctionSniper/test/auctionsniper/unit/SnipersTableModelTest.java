package auctionsniper.unit;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.Item;
import auctionsniper.SniperSnapshot;
import auctionsniper.ui.SnipersTableModel.Column;
import auctionsniper.ui.SnipersTableModel;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

@RunWith(JMock.class)
public class SnipersTableModelTest {
	private final Mockery context = new Mockery();
	private TableModelListener listener = context.mock(TableModelListener.class);
	private Auction auction = context.mock(Auction.class);
	private final SnipersTableModel model = new SnipersTableModel();
	
	@Before
	public void attachModelListener() {
		model.addTableModelListener(listener);
	}
	
	@Test 
	public void hasEnoughColumns() {
		assertThat(model.getColumnCount(), equalTo(Column.values().length));
	}
	
	@Test
	public void setSniperValuesInColumns() {
		SniperSnapshot joining = SniperSnapshot.joining("item id");
		SniperSnapshot bidding = joining.bidding(555, 666);
		context.checking(new Expectations() {{
			allowing(listener).tableChanged(with(anyInsertionEvent()));
			one(listener).tableChanged(with(aChangedInRow(0)));
		}});
		
		model.sniperAdded(new AuctionSniper(new Item(joining.itemId, Integer.MAX_VALUE), auction));
		model.sniperStateChanged(bidding);
		
		assertRowMatchesSnapshot(0, bidding);
	}

	private void assertRowMatchesSnapshot(int rowIndex, SniperSnapshot snapshot) {
		assertColumnEquals(rowIndex, Column.ITEM_IDENTIFIER, snapshot.itemId);
		assertColumnEquals(rowIndex, Column.LAST_PRICE, snapshot.lastPrice);
		assertColumnEquals(rowIndex, Column.LAST_BID, snapshot.lastBid);
		assertColumnEquals(rowIndex, Column.SNIPER_STATE, SnipersTableModel.textFor(snapshot.state));
	}

	private void assertColumnEquals(int rowIndex, Column column, Object expected) {
		final int columnIndex = column.ordinal();
		assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
	}

	protected Matcher<TableModelEvent> anyInsertionEvent() {
		return new FeatureMatcher<TableModelEvent, Integer>(equalTo(TableModelEvent.INSERT), "table event ", "was") {
			@Override protected Integer featureValueOf(TableModelEvent actual){
				return actual.getType();
			}
		};
	}

	private Matcher<TableModelEvent> aChangedInRow(int rowIndex) {
		return samePropertyValuesAs(new TableModelEvent(model, rowIndex));	
	}
	
	@Test 
	public void setsUpColumnHeadings() {
		for(Column column: Column.values()){
			assertEquals(column.name, model.getColumnName(column.ordinal()));
		}
	}
	
	@Test 
	public void notifiesListenersWhenAddingASniper() {
		SniperSnapshot joining = SniperSnapshot.joining("item123");
		context.checking(new Expectations() {{
			one(listener).tableChanged(with(anInsertionAtRow(0)));
		}});
		
		assertEquals(0, model.getRowCount());
		
		model.sniperAdded(new AuctionSniper(new Item(joining.itemId, Integer.MAX_VALUE), auction));
		
		assertEquals(1, model.getRowCount());
		assertRowMatchesSnapshot(0, joining);
	}

	///TODO match rowindx too
	protected Matcher<TableModelEvent> anInsertionAtRow(int rowIndex) {
		return new FeatureMatcher<TableModelEvent, Integer>(equalTo(TableModelEvent.INSERT), "table event ", "was") {
			@Override protected Integer featureValueOf(TableModelEvent actual){
				return actual.getType();
			}
		};	
	}
	
	@Test
	public void holdsSniperInAddintionOrder(){
		context.checking(new Expectations(){{
			ignoring(listener);
		}});
		model.sniperAdded(new AuctionSniper(new Item("item 0", Integer.MAX_VALUE), auction));
		model.sniperAdded(new AuctionSniper(new Item("item 1", Integer.MAX_VALUE), auction));
		
		assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
		assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
	}
	
	private String cellValue(int rowIndex, Column column){
		return (String)model.getValueAt(rowIndex, column.ordinal());
	}

//	updatesCorrectRowForSniper() { [...]
//	throwsDefectIfNoExistingSniperForAnUpdate() { [...]

}
