package model.activity;

import java.io.Serializable;

import server.ServerController;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.PhaseType;

public class Hide extends Activity implements Serializable{
	
	private int rv = 0;
	
	public Hide(CharacterType actor, PhaseType type, int rv) {
		super(ActivityType.HIDE, actor, type);
		this.rv = rv;
	}

	@Override
	public void perform(ServerController controller) {
		controller.hideCharacter(getActor(), rv);
	}
	
	private static final long serialVersionUID = 8360233239299744462L;

}
