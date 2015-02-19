package model.character;

import java.util.ArrayList;

import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.PhaseType;
import model.interfaces.CharacterFactoryInterface;

public class CharacterFactory {

	public static ArrayList<Character> getPossibleCharacters() {
		ArrayList<Character> a = new ArrayList<Character>();
		a.add(create(CharacterType.AMAZON));
		a.add(create(CharacterType.CAPTAIN));
		a.add(create(CharacterType.SWORDSMAN));
		return a;
	}

	public static Character create(CharacterType ct) {
		Character c = new Character(ct);
		// init belongings
		// init special phases ONLY based on special character traits. no
		// belongings are taken into account
		ArrayList<Phase> specialPhases = new ArrayList<Phase>();
		ArrayList<ActivityType> possibleActivities = new ArrayList<ActivityType>();
		switch(ct){
		case AMAZON:
			//one extra move phase
			specialPhases.add(new Phase(PhaseType.SPECIAL, CharacterType.AMAZON));
			possibleActivities.add(ActivityType.NONE);
			possibleActivities.add(ActivityType.MOVE);
			specialPhases.get(0).setPossibleActivities(possibleActivities);
			break;
		case CAPTAIN:
			specialPhases.add(new Phase(PhaseType.SPECIAL, CharacterType.CAPTAIN));
			for(ActivityType t : ActivityType.values()){
				possibleActivities.add(t);
			}
			specialPhases.get(0).setPossibleActivities(possibleActivities);
			break;
		case SWORDSMAN:
			break;
		default:
			break;
		}
		c.setSpecialPhases(specialPhases);
		return c;
	}

}
