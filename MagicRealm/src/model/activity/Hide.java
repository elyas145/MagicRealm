package model.activity;

import model.controller.ModelControlInterface;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;

public class Hide extends Activity {

	public Hide(CharacterType actor) {
		super(ActivityType.HIDE, actor);
	}

	@Override
	public void perform(ModelControlInterface controller) {
		controller.hideCharacter(getActor());
	}

}
