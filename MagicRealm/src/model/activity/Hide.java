package model.activity;

import utils.random.Random;
import controller.Controller;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;

public class Hide extends Activity {

	public Hide(CharacterType actor) {
		super(ActivityType.HIDE, actor);
	}

	@Override
	public void perform(ModelController controller) {
		if (!controller.isCharacterHiding(getActor())) {
			int rand = Random.dieRoll();
			System.out.println("random hide: " + rand);
			if (rand != 6) {
				controller.setCurrentHiding();
			}
		}

	}

}
