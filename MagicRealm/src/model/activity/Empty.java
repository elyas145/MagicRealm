package model.activity;

import server.ServerController;
import model.controller.ModelControlInterface;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;

public class Empty extends Activity{

	public Empty(CharacterType actor) {
		super(ActivityType.NONE, actor);
	}

	@Override
	public void perform(ServerController controller) { }

}
