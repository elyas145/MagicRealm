package model.activity;

import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.TileName;
import model.exceptions.IllegalMoveException;

public class Move extends Activity {
	private TileName tile;
	private int clearing;

	public Move(CharacterType actor, TileName tileName, int c) {
		super(ActivityType.MOVE, actor);
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
	public void perform(ModelController controller) {
		// set location on the board.
		try {
			controller.moveCharacter(getActor(), getTile(), getClearing());
		} catch (IllegalMoveException e) {
			controller.raiseMessage(getActor(), "Illegal move cancelled");
		}
	}

}
