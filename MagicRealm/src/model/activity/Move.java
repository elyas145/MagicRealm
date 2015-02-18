package model.activity;

import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.TileName;
import model.enums.TileType;

public class Move extends Activity{
	private TileName tile;
	private int clearing;
	public Move(ActivityType act, TileName tileName, int c) {
		super(act);
		tile = tileName;
		clearing = c;
	}
	
	@Override
	public void perform(ModelController mc) {
		//mc.moveCharacter(character, tt, clearing);
		
	}

}
