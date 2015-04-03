package model.activity;

import server.ServerController;
import model.controller.ModelControlInterface;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.PhaseType;

public class Empty extends Activity{

	public Empty(CharacterType actor, PhaseType type) {
		super(ActivityType.NONE, actor, type);
	}

	@Override
	public void perform(ServerController controller) { }

}
