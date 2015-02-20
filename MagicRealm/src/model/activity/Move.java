package model.activity;

import controller.Controller;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.TileName;
import model.enums.TileType;

public class Move extends Activity {
	private TileName tile;
	private int clearing;

	public Move(ActivityType act, TileName tileName, int c) {
		super(act);
		tile = tileName;
		clearing = c;
	}

	public TileName getTile() {
		return tile;
	}

	public void setTile(TileName tile) {
		this.tile = tile;
	}

	public int getClearing() {
		return clearing;
	}

	public void setClearing(int clearing) {
		this.clearing = clearing;
	}

	@Override
	public void perform(Controller controller) {
		boolean isLegal = controller.checkMoveLegality(this);
		if (isLegal) {
			// set location on the board.
			controller
					.getModel()
					.getBoard()
					.setLocationOfCounter(
							controller.getModel().getCurrentCounter(),
							getTile(), getClearing());
			// move counter on the view.
			controller.getBoardView().setCounter(
					controller.getModel().getCurrentCounter(), getTile(),
					getClearing());
		} else {
			controller.getMainView().displayMessage("Illegal move cancelled.");
			return;
		}
	}

}
