package model.activity;

import model.controller.ModelControlInterface;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;

public class Search extends Activity {

	public Search(CharacterType actor) {
		super(ActivityType.SEARCH, actor);
	}

	@Override
	public void perform(ModelControlInterface controller) {
		controller.startSearching(getActor());
	}

}
