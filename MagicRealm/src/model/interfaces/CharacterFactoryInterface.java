package model.interfaces;

import java.util.ArrayList;

import model.enums.CharacterType;
import model.board.Character;
public interface CharacterFactoryInterface {
	// returns all possible characters in an array list.
	// use this method for the begining of the game, so characters can choose a character
	public ArrayList<Character> getPossibleCharacters();
	
	// returns an instance of the specified character type
	public Character getChatacter(CharacterType t);
	
	
}
