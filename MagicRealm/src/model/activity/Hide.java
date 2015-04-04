package model.activity;

import java.io.Serializable;

import server.ServerController;
import model.controller.ModelControlInterface;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.PhaseType;

public class Hide extends Activity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8360233239299744462L;

	public Hide(CharacterType actor, PhaseType type) {
		super(ActivityType.HIDE, actor, type);
	}

	@Override
	public void perform(ServerController controller) {
		controller.hideCharacter(getActor());
	}

}
