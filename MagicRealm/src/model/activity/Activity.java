package model.activity;

import java.io.Serializable;

import server.ServerController;
import model.controller.ModelControlInterface;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.PhaseType;

public abstract class Activity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9114075078403710878L;
	// perform the action on the model controller
	public abstract void perform(ServerController serverController);

	public void setPhaseType(PhaseType type){
		this.phaseType = type;
	}
	public PhaseType getPhaseType(){
		return this.phaseType;
	}
	
	public ActivityType getType() {
		return type;
	}

	public CharacterType getActor() {
		return character;
	}

	public void setType(ActivityType type) {
		this.type = type;
	}

	protected Activity(ActivityType act, CharacterType actor, PhaseType phaseType) {
		this.type = act;
		this.phaseType = phaseType;
		setActor(actor);
	}

	protected void setActor(CharacterType character) {
		this.character = character;
	}

	private ActivityType type;
	private CharacterType character;
	private PhaseType phaseType;

}
