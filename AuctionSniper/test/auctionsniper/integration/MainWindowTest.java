package auctionsniper.integration;

import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import auctionsniper.UserRequestListener;
import auctionsniper.ui.AuctionSniperDriver;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {
	private final SnipersTableModel tableModel = new SnipersTableModel();
	private final MainWindow mainWindow = new MainWindow(tableModel);
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
		//TODO why need to aan even bn is success
		driver.startBiddingFor("item-id");
		driver.check(buttonProbe);
	}

}