package auctionsniper.integration;

import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import auctionsniper.stub.AuctionSniperDriver;
import auctionsniper.ui.MainWindow;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {
	private final SniperPortfolio sniperPortfolio = new SniperPortfolio();
	private final MainWindow mainWindow = new MainWindow(sniperPortfolio);
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);
	
	@Test
	public void makesUserRequestWhenJoinButtonClicked() {
		final ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<Item>(equalTo(new Item("item-id", 789)), "item request");
		mainWindow.addUserRequestListener(
				new UserRequestListener(){
					@Override
					public void joinAuction(Item item){
						itemProbe.setReceivedValue(item);
					}
				}
		);
		driver.startBiddingFor("item-id", 789);
		driver.check(itemProbe);
	}

}