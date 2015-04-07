package model.character;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.PhaseType;

public class Phase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7192013520095214358L;
	private List<ActivityType> possibleActivities;
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
		setActivities();
	}
	public Phase(PhaseType t) {
		type = t;
		setActivities();
	}

	public List<ActivityType> getPossibleActivities() {
		return possibleActivities;
	}

	public void setPossibleActivities(List<ActivityType> possibleActivities) {
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
	private void setActivities(){
		possibleActivities = new ArrayList<ActivityType>();
		switch(type){
		case DEFAULT:
			possibleActivities.add(ActivityType.NONE);
			possibleActivities.add(ActivityType.HIDE);
			possibleActivities.add(ActivityType.MOVE);
			possibleActivities.add(ActivityType.SEARCH);
			possibleActivities.add(ActivityType.ENCHANT);
			break;
		case SUNLIGHT:
			possibleActivities.add(ActivityType.NONE);
			possibleActivities.add(ActivityType.HIDE);
			possibleActivities.add(ActivityType.MOVE);
			possibleActivities.add(ActivityType.SEARCH);
			possibleActivities.add(ActivityType.ENCHANT);
			break;
		case SPECIAL:
			break;
		}
	}
	public String toString(){
		return "Phase type: " + type;
	}
}
