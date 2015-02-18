package model.player;

import model.character.Character;

public class Player {
	
	private int number;
	private String name;
	private Character character;
	private PersonalHistory historyPad;
	
	public Player(int num, String nm) {
		number = num;
		name = nm;
		character = null;
		historyPad = new PersonalHistory();
	}
	public void setCharacter(Character c) {
		character = c;		
	}
	public PersonalHistory getPersonalHistory() {
		return historyPad;
	}
	
}
