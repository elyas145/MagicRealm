package model.player;

import java.util.ArrayList;

import model.activity.Activity;
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
	public Character getCharacter() {
		return character;
	}
	public void setActivities(ArrayList<Activity> activities) {
		for(Activity a : activities){
			historyPad.addActivity(a);
		}		
		
	}
	
}
