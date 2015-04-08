package model.activity;

import java.io.Serializable;

import server.ServerController;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.PhaseType;

public class Enchant extends Activity implements Serializable {
	
	public Enchant(CharacterType actor, PhaseType phaseType) {
		super(ActivityType.ENCHANT, actor, phaseType);
	}

	@Override
	public void perform(ServerController serverController) {
		serverController.requestEnchant(getActor());
	}

	private static final long serialVersionUID = 4037468430901088132L;

}
