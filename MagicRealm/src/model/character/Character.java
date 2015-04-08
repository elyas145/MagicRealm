package model.character;

import java.io.Serializable;
import java.util.ArrayList;

import config.GameConfiguration;
import model.character.belonging.Belonging;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.interfaces.CharacterInterface;

public class Character implements CharacterInterface, Serializable {
	
	private ArrayList<Belonging> belongings;
	private boolean hiding;
	private CounterType initialLocation;
	private ArrayList<Phase> specialPhases;
	private CharacterType type;

	public Character(CharacterType t) {
		type = t;
		initialLocation = GameConfiguration.INITIAL_SITE;
		hiding = true;
	}
	
	public void setInitialLocation(CounterType location){
		initialLocation = location;
	}
	
	public void AddBelonging(Belonging b) {
		belongings.add(b);
	}

	public ArrayList<Belonging> getBelongings() {
		return belongings;
	}

	public CounterType getInitialLocation() {
		return initialLocation;
	}

	public ArrayList<Phase> getSpecialPhases() {

		return specialPhases;
	}

	public CharacterType getType() {
		return type;
	}

	public boolean isHiding() {
		return hiding;
	}

	public void setBelongings(ArrayList<Belonging> belongings) {
		this.belongings = belongings;
	}

	public void setHiding(boolean hiding) {
		this.hiding = hiding;
	}

	public void setSpecialPhases(ArrayList<Phase> specialPhases) {
		this.specialPhases = specialPhases;
	}

	public void setType(CharacterType type) {
		this.type = type;
	}
	
	private static final long serialVersionUID = 3972396707482617354L;

}
