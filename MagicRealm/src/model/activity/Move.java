package model.activity;

import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.TileType;

public class Move extends Activity{
	private TileType tile;
	private int clearing;
	public Move(ActivityType act, TileType tt, int c) {
		super(act);
		tile = tt;
		clearing = c;
	}
	
	@Override
	public void perform(ModelController mc) {
		//mc.moveCharacter(character, tt, clearing);
		
	}

}
