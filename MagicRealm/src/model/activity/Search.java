package model.activity;

import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;

public class Search extends Activity {

	public Search(CharacterType actor) {
		super(ActivityType.SEARCH, actor);
	}

	@Override
	public void perform(ModelController controller) {
		controller.startSearching(getActor());
	}

}
