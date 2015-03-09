package model.controller;

import java.util.List;

import model.activity.Activity;
import model.board.Board;
import model.enums.CharacterType;
import model.enums.PeerType;
import model.enums.TableType;
import model.enums.TileName;
import model.exceptions.GameFullException;

/*
 * Meant to be a container for the entire model
 */
public interface ModelControlInterface {

	void moveCharacter(CharacterType characterType, TileName tt,
			int clearing);
	
	void setCharacterHidden(CharacterType character, boolean hid);

	void killCharacter(CharacterType character);

	void setPlayerActivities(List<Activity> activities, CharacterType chr);

	void performSearch(TableType selectedTable, CharacterType chr);
	
	void peerChoice(PeerType choice, CharacterType actor);

	void hideCharacter(CharacterType actor);

	void startSearching(CharacterType actor);

	void hideCharacter(int chance, CharacterType character);

	void setBoard(Board board);

}
