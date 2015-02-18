package swingview.controller;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTable;

import model.player.PersonalHistory;

@SuppressWarnings("serial")
public class HistoryView extends JPanel {
	private PersonalHistory history;
	private JTable actions;
	private JTable points;
	
	public HistoryView(PersonalHistory history) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.history = history;
		
		
	}
}
