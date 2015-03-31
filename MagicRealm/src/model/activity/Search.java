package model.activity;

import server.ServerController;
import model.controller.ModelControlInterface;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;

public class Search extends Activity {

	public Search(CharacterType actor) {
		super(ActivityType.SEARCH, actor);
	}

	@Override
	public void perform(ServerController controller) {
		controller.startSearching(getActor());
	}

}
