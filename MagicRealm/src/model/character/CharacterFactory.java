package model.character;

import java.util.ArrayList;

import model.character.belonging.Armor;
import model.character.belonging.Belonging;
import model.character.belonging.Weapon;
import model.enums.ActivityType;
import model.enums.ArmorType;
import model.enums.CharacterType;
import model.enums.PhaseType;
import model.enums.ValleyChit;
import model.enums.WeaponType;
import model.interfaces.BelongingType;
import model.interfaces.CharacterFactoryInterface;

public class CharacterFactory {

	public static ArrayList<Character> getPossibleCharacters() {
		ArrayList<Character> a = new ArrayList<Character>();
		a.add(create(CharacterType.AMAZON, ValleyChit.INN));
		a.add(create(CharacterType.CAPTAIN, ValleyChit.INN));
		a.add(create(CharacterType.SWORDSMAN, ValleyChit.INN));
		return a;
	}

	public static Character create(CharacterType ct, ValleyChit location) {
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
			c.setInitialLocation(ValleyChit.INN);
			//initial belongings
			belongings.add(new Weapon(WeaponType.SHORT_SWORD, 1, false, 0, 'L'));
			belongings.add(new Armor(ArmorType.HELMET, 'M'));
			belongings.add(new Armor(ArmorType.BREAST_PLATE, 'M'));
			belongings.add(new Armor(ArmorType.SHIELD, 'M'));
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
			belongings.add(new Weapon(WeaponType.SHORT_SWORD, 1, false, 0, 'L'));
			belongings.add(new Armor(ArmorType.HELMET, 'M'));
			belongings.add(new Armor(ArmorType.BREAST_PLATE, 'M'));
			belongings.add(new Armor(ArmorType.SHIELD, 'M'));
			break;
		case SWORDSMAN:
			belongings.add(new Weapon(WeaponType.THRUSTING_SWORD, 1, false, 0, 'L'));
			break;
		default:
			break;
		}
		c.setSpecialPhases(specialPhases);
		c.setBelongings(belongings);
		return c;
	}

}
