package model.activity;

import server.ServerController;
import model.controller.ModelControlInterface;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.PhaseType;

public class Hide extends Activity {

	public Hide(CharacterType actor, PhaseType type) {
		super(ActivityType.HIDE, actor, type);
	}

	@Override
	public void perform(ServerController controller) {
		controller.hideCharacter(getActor());
	}

}
