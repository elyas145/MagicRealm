package model.exceptions;

import model.enums.CharacterType;
import model.enums.TileName;

public class IllegalMoveException extends MRException {
	private TileName name;
	private int number;
	private CharacterType ct;
	public IllegalMoveException(TileName n, int number, CharacterType ct){
		super();
		this.number = number;
		name = n;
		this.ct = ct;
	}
	
	public String toString(){
		return "Cannot move " + ct + " to " + name + "clearing: " + number;
	}
}
