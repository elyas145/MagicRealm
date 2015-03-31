package model.activity;

import server.ServerController;
import model.controller.ModelControlInterface;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;

public class Hide extends Activity {

	public Hide(CharacterType actor) {
		super(ActivityType.HIDE, actor);
	}

	@Override
	public void perform(ServerController controller) {
		controller.hideCharacter(getActor());
	}

}
