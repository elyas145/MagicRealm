package model.activity;

import utils.random.Random;
import controller.Controller;
import model.controller.ModelController;
import model.enums.ActivityType;

public class Hide extends Activity {

	public Hide(ActivityType act) {
		super(act);
	}

	@Override
	public void perform(Controller controller) {
		if (!controller.getModel().isCurrentHidden()) {
			int rand = Random.randomInteger(1, 7);
			System.out.println("random hide: " + rand);
			if (rand != 6) {
				// hide
				controller.getModel().setCurrentHiding();
				// flip the counter on the board.
				// TODO
				// controller.getBoardView().flipCharacterCounter(controller.getModel.getCurrentCounter());
			}
		}

	}

}
