package model.activity;

import controller.Controller;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;

public abstract class Activity {
	
	public Activity(ActivityType act) {
		type = act;
	}
	
	// perform the action on the model controller
	public abstract void perform(Controller controller);
	private CharacterType character;
	public CharacterType getCharacter() {
		return character;
	}

	public void setCharacter(CharacterType character) {
		this.character = character;
	}

	public void setType(ActivityType type) {
		this.type = type;
	}
	private ActivityType type;

	public ActivityType getType() {
		return type;
	}
}
