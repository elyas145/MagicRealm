package swingview.controller.birdsong;

import javax.swing.JPanel;

import model.player.PersonalHistory;
import controller.Controller;

@SuppressWarnings("serial")
public class BirdSongView extends JPanel{
	private Controller parent;
	private PersonalHistory history;
	public BirdSongView(Controller parent) {
		this.parent = parent;
		history = parent.getPlayerHistory();
	}
	
}
