package auctionsniper.integration;

import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

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
		final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<String>(equalTo("item-id"), "join request");
		mainWindow.addUserRequestListener(
				new UserRequestListener(){
					@Override
					public void joinAuction(String itemId){
						buttonProbe.setReceivedValue(itemId);
					}
				}
		);
		driver.startBiddingFor("item-id");
		driver.check(buttonProbe);
	}

}