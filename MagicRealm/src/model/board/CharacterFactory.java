package model.board;

import java.util.ArrayList;
import model.board.Character;
import model.enums.CharacterType;
import model.interfaces.CharacterFactoryInterface;

public class CharacterFactory implements CharacterFactoryInterface{

	@Override
	public ArrayList<Character> getPossibleCharacters() {
		Character c = new Character(CharacterType.AMAZON);
		ArrayList<Character> a = new ArrayList<Character>();
		a.add(c);
		return a;
	}

	@Override
	public Character getChatacter(CharacterType t) {
		// TODO Auto-generated method stub
		return null;
	}

}
