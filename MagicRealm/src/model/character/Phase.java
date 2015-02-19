package model.character;

import java.util.ArrayList;

import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.PhaseType;

public class Phase {
	private ArrayList<ActivityType> possibleActivities;
	private PhaseType type;
	private CharacterType character;

	public CharacterType getCharacter() {
		return character;
	}

	public void setCharacter(CharacterType character) {
		this.character = character;
	}

	public Phase(PhaseType t, CharacterType ct) {
		type = t;
		character = ct;
	}
	public Phase(PhaseType t) {
		type = t;
	}

	public ArrayList<ActivityType> getPossibleActivities() {
		return possibleActivities;
	}

	public void setPossibleActivities(ArrayList<ActivityType> possibleActivities) {
		this.possibleActivities = possibleActivities;
	}

	public PhaseType getType() {
		return type;
	}

	public void setType(PhaseType type) {
		this.type = type;
	}

	public void setPossibleActivities(ActivityType[] values) {
		possibleActivities = new ArrayList<ActivityType>();
		for(ActivityType type : values){
			possibleActivities.add(type);
		}
		
	}
}
