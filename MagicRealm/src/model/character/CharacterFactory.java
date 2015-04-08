package model.character;

import java.util.ArrayList;

import model.character.belonging.Armor;
import model.character.belonging.Belonging;
import model.character.belonging.Weapon;
import model.enums.ActivityType;
import model.enums.BelongingType;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.PhaseType;

public class CharacterFactory {

	public static ArrayList<Character> getPossibleCharacters() {
		ArrayList<Character> a = new ArrayList<Character>();
		a.add(create(CharacterType.AMAZON, CounterType.VALLEY_INN));
		a.add(create(CharacterType.CAPTAIN, CounterType.VALLEY_INN));
		a.add(create(CharacterType.SWORDSMAN, CounterType.VALLEY_INN));
		return a;
	}

	public static Character create(CharacterType ct, CounterType location) {
		Character c = new Character(ct);
		// init belongings
		// init special phases ONLY based on special character traits. 
		// init belongings.
		ArrayList<Phase> specialPhases = new ArrayList<Phase>();
		ArrayList<ActivityType> possibleActivities = new ArrayList<ActivityType>();
		ArrayList<Belonging> belongings = new ArrayList<Belonging>();
		switch(ct){
		case AMAZON:
			//one extra move phase
			specialPhases.add(new Phase(PhaseType.SPECIAL, CharacterType.AMAZON));
			possibleActivities.add(ActivityType.NONE);
			possibleActivities.add(ActivityType.MOVE);
			specialPhases.get(0).setPossibleActivities(possibleActivities);
			//has to start at the inn
			c.setInitialLocation(CounterType.VALLEY_INN);
			//initial belongings
			belongings.add(new Weapon(BelongingType.SHORT_SWORD, 1, false, 0, 'L'));
			belongings.add(new Armor(BelongingType.HELMET, 'M'));
			belongings.add(new Armor(BelongingType.BREAST_PLATE, 'M'));
			belongings.add(new Armor(BelongingType.SHIELD, 'M'));
			break;
		case CAPTAIN:
			specialPhases.add(new Phase(PhaseType.SPECIAL, CharacterType.CAPTAIN));
			for(ActivityType t : ActivityType.values()){
				possibleActivities.add(t);
			}
			specialPhases.get(0).setPossibleActivities(possibleActivities);
			//has to start at the inn, house, or guard house
			c.setInitialLocation(location);
			//initial belongings
			belongings.add(new Weapon(BelongingType.SHORT_SWORD, 1, false, 0, 'L'));
			belongings.add(new Armor(BelongingType.HELMET, 'M'));
			belongings.add(new Armor(BelongingType.BREAST_PLATE, 'M'));
			belongings.add(new Armor(BelongingType.SHIELD, 'M'));
			break;
		case SWORDSMAN:
			belongings.add(new Weapon(BelongingType.THRUSTING_SWORD, 1, false, 0, 'L'));
			break;
		default:
			break;
		}
		c.setSpecialPhases(specialPhases);
		c.setBelongings(belongings);
		return c;
	}

	public static ArrayList<CounterType> getPossibleStartingLocations(
			CharacterType character) {
		ArrayList<CounterType> locations = new ArrayList<CounterType>();
		switch(character){
		case AMAZON:
			locations.add(CounterType.VALLEY_INN);
			break;
		case CAPTAIN:
			locations.add(CounterType.VALLEY_INN);
			locations.add(CounterType.VALLEY_HOUSE);
			locations.add(CounterType.VALLEY_GUARD_HOUSE);
			break;
		case SWORDSMAN:
			locations.add(CounterType.VALLEY_INN);
			break;
		default:
			break;
		}
		return locations;
	}

}
