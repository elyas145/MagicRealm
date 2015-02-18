package swingview.controller.birdsong;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.Controller;
import model.activity.Activity;
import model.board.clearing.Clearing;
import model.board.tile.HexTile;
import model.enums.TileName;
import model.interfaces.HexTileInterface;

public class ActivityView extends JFrame{
	private BirdSongView parent;
	private Controller controller;
	private ArrayList<String> actions;
	public ActivityView(Controller controller, ArrayList<String> actions){
		super("Set Activity");		
		this.controller = controller;
		this.actions = actions;
				
		JLabel lbl = new JLabel("Move To: ");
		ArrayList<TileName> possibleTiles = controller.getPossibleTiles();
		
		//move panel
		
		//ArrayList<Clearing> possibleClearings = controller.getPossibleClearings();
		
	}
}
