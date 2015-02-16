package model.character;

import java.util.ArrayList;

import model.enums.CharacterType;
import model.interfaces.CharacterFactoryInterface;

public class CharacterFactory implements CharacterFactoryInterface{

	@Override
	public ArrayList<Character> getPossibleCharacters() {
		Character c = new Character(CharacterType.AMAZON);
		Character d = new Character(CharacterType.CAPTAIN);
		Character e = new Character(CharacterType.SWORDSMAN);
		ArrayList<Character> a = new ArrayList<Character>();
		a.add(c);
		a.add(d);
		a.add(e);
		return a;
	}

	@Override
	public Character getChatacter(CharacterType t) {
		// TODO Auto-generated method stub
		return null;
	}

}
