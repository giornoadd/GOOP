package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;

import auctionsniper.SniperState;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	public static final String APPLICATION_TITLE = "Auction Sniper";
	public static final String MAIN_WINDOW_NAME = "Auction Sniper";
	public static final String SNIPERS_TABLE_NAME = "Sniper List";
	public static final String SNIPER_STATUS_NAME = "sniper status";
	
	public static final String STATUS_JOINING ="Joining";
	public static final String STATUS_LOST = "Lost";
	public static final String STATUS_BIDDING = "Bidding";
	public static final String STATUS_WINNIG = "Winning";
	public static final String STATUS_WON = "Won";

	private final SnipersTableModel snipers = new SnipersTableModel();
	private final JLabel sniperStatus = createLabel(STATUS_JOINING);
	
	public MainWindow() {
		super(APPLICATION_TITLE);
		setName(MAIN_WINDOW_NAME);
		add(sniperStatus);
		fillContentPane(makeSnipersTable());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private static JLabel createLabel(String initialText) {
		JLabel result = new JLabel(initialText);
		result.setName(SNIPER_STATUS_NAME);
		result.setBorder(new LineBorder(Color.BLACK));
		return result;
	}

	private void fillContentPane(JTable snipersTable) {
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);	
	}
	
	private JTable makeSnipersTable() {
		final JTable snipersTable = new JTable(snipers);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}

	public void showStatus(String textStatus) {
		snipers.setStatusText(textStatus);
	}

	public void sniperStatusChanged(SniperState sniperState, String statusText) {
		snipers.sniperStatusChanged(sniperState, statusText);
	}
}