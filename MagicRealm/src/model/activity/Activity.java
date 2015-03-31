package model.activity;

import server.ServerController;
import model.controller.ModelControlInterface;
import model.enums.ActivityType;
import model.enums.CharacterType;

public abstract class Activity {

	// perform the action on the model controller
	public abstract void perform(ServerController serverController);

	public ActivityType getType() {
		return type;
	}

	public CharacterType getActor() {
		return character;
	}

	public void setType(ActivityType type) {
		this.type = type;
	}

	protected Activity(ActivityType act, CharacterType actor) {
		type = act;
		setActor(actor);
	}

	protected void setActor(CharacterType character) {
		this.character = character;
	}

	private ActivityType type;
	private CharacterType character;

}
