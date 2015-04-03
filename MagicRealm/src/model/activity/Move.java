package model.activity;

import server.ServerController;
import model.controller.ModelControlInterface;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.PhaseType;
import model.enums.TileName;
import model.exceptions.IllegalMoveException;

public class Move extends Activity {
	private TileName tile;
	private int clearing;

	public Move(CharacterType actor, TileName tileName, int c, PhaseType type) {
		super(ActivityType.MOVE, actor, type);
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
	public void perform(ServerController controller) {
		// set location on the board.
		controller.moveCharacter(getActor(), getTile(), getClearing());
	}

}
