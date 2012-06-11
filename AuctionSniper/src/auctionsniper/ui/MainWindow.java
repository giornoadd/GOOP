package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import auctionsniper.Announcer;
import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	public static final String APPLICATION_TITLE = "Auction Sniper";
	public static final String MAIN_WINDOW_NAME = "Auction Sniper";
	public static final String SNIPERS_TABLE_NAME = "Sniper List";
	public static final String SNIPER_STATUS_NAME = "sniper status";
	public static final String JOIN_BUTTON_NAME = "Join Auction";
	public static final String NEW_ITEM_ID_NAME = "Item id";
	public static final String NEW_ITEM_STOP_PRICE_NAME = "Stop price";
	private static final String NEW_ITEM_LABEL_NAME = "Item:";
	private static final String NEW_ITEM_STOP_PRICE_LABEL_NAME = "Stop price:";
	
	public static final String STATUS_JOINING ="Joining";
	public static final String STATUS_LOST = "Lost";
	public static final String STATUS_LOSING = "Losing";
	public static final String STATUS_BIDDING = "Bidding";
	public static final String STATUS_WINNIG = "Winning";
	public static final String STATUS_WON = "Won";
	public static final String STATUS_FAILED = "Failed";

	private final JLabel sniperStatus = createLabel(STATUS_JOINING);
	private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

		
	public MainWindow(final SniperPortfolio portfolio) {
		super(APPLICATION_TITLE);
		setName(MAIN_WINDOW_NAME);
		add(sniperStatus);
		fillContentPane(makeControls(portfolio));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);	}

	private JPanel makeControls(final SniperPortfolio portfolio) {
		JPanel controls = new JPanel(new FlowLayout());
		final JLabel itemLabel = new JLabel();
		itemLabel.setText(NEW_ITEM_LABEL_NAME);
		controls.add(itemLabel);
		final JTextField itemIdField = new JTextField();
		itemIdField.setColumns(10);
		itemIdField.setName(NEW_ITEM_ID_NAME);
		controls.add(itemIdField);
		
		final JLabel stopPriceLabel = new JLabel();
		stopPriceLabel.setText(NEW_ITEM_STOP_PRICE_LABEL_NAME);
		controls.add(stopPriceLabel);
		final JFormattedTextField stopPriceField = new JFormattedTextField(NumberFormat.getInstance());
		stopPriceField.setColumns(10);
		stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
		controls.add(stopPriceField);
		
		JButton joinAuctionButton = new JButton("Join Auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);
		controls.add(joinAuctionButton);
		joinAuctionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userRequests.announce().joinAuction(new Item(itemId(), stopPrice()));
			}
			private String itemId() {
				return itemIdField.getText();
			}
			private int stopPrice() {
				return ((Number)stopPriceField.getValue()).intValue();
			}

		});
		
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(controls, BorderLayout.NORTH);
		mainPane.add(new JScrollPane(makeSnipersTable(portfolio)), BorderLayout.CENTER);
		return mainPane;
	}

	private static JLabel createLabel(String initialText) {
		JLabel result = new JLabel(initialText);
		result.setName(SNIPER_STATUS_NAME);
		result.setBorder(new LineBorder(Color.BLACK));
		return result;
	}

	private void fillContentPane(JPanel mainPane) {
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(mainPane, BorderLayout.CENTER);
	}
	
	private JTable makeSnipersTable(SniperPortfolio portfolio) {
		SnipersTableModel model = new SnipersTableModel();
		portfolio.addPortfolioListener(model);
		JTable snipersTable = new JTable(model);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}
	
	public void addUserRequestListener(UserRequestListener userRequestListener) {
			userRequests.addListener(userRequestListener);
		
	}
}


