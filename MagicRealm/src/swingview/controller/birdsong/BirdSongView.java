package swingview.controller.birdsong;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import swingview.controller.HistoryView;
import model.player.PersonalHistory;
import controller.Controller;

@SuppressWarnings("serial")
public class BirdSongView extends JPanel{
	private Controller parent;
	private PersonalHistory history;
	public BirdSongView(Controller parent) {
		this.parent = parent;
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		history = parent.getPlayerHistory();
		HistoryView historyView = new HistoryView(history);
		historyView.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(historyView);
		
		
	}
	
}
