package model.activity;

import java.io.Serializable;

import server.ServerController;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.PhaseType;

public class Search extends Activity implements Serializable {

	public Search(CharacterType actor, PhaseType type) {
		super(ActivityType.SEARCH, actor, type);
	}

	@Override
	public void perform(ServerController controller) {
		controller.startSearching(getActor());
	}

	private static final long serialVersionUID = -2839219458392754433L;

}
