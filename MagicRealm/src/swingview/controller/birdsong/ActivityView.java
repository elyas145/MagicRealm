package swingview.controller.birdsong;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.Controller;
import model.board.clearing.Clearing;
import model.board.tile.HexTile;

public class ActivityView extends JFrame{
	private BirdSongView parent;
	private Controller controller;
	public ActivityView(BirdSongView parent, Controller controller){
		super("Set Activity");
		
		this.parent = parent;
		this.controller = controller;
				
		JLabel lbl = new JLabel("Move To: ");
		ArrayList<HexTile> possibleTiles = controller.getPossibleTiles();
		ArrayList<Clearing> possibleClearings = controller.getPossibleClearings();
		
	}
}
