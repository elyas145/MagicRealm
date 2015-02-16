package model.controller;

import java.util.Map;

import model.board.Board;
import model.enums.CharacterType;
import model.enums.TileType;
import model.player.Player;

/*
 * Meant to be a container for the entire model
 */
public class ModelController {
	
	public void moveCharacter(Character character, TileType tt, int clearing) {
		board.moveCharacter(character, tt, clearing);
	}
	
	public void killCharacter(Character character) {
		board.removeCharacter(character);
	}
	
	private Board board;
	private Map<CharacterType, Character> characters;

}
