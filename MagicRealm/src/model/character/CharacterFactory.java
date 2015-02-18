package model.character;

import java.util.ArrayList;

import model.enums.CharacterType;
import model.interfaces.CharacterFactoryInterface;

public class CharacterFactory{

	public static ArrayList<Character> getPossibleCharacters() {
		Character c = new Character(CharacterType.AMAZON);
		Character d = new Character(CharacterType.CAPTAIN);
		Character e = new Character(CharacterType.SWORDSMAN);
		ArrayList<Character> a = new ArrayList<Character>();
		a.add(c);
		a.add(d);
		a.add(e);
		return a;
	}

	public static Character create(CharacterType amazon) {
		Character c = new Character(amazon);		
		return c;
	}

}
