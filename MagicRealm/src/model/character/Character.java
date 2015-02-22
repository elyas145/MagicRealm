package model.character;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import config.GameConfiguration;
import model.EnchantedHolder;
import model.counter.chit.Chit;
import model.enums.CharacterType;
import model.enums.PathType;
import model.enums.ValleyChit;
import model.interfaces.CharacterInterface;
import model.interfaces.ClearingInterface;

public class Character implements CharacterInterface {
	private CharacterType type;
	private ValleyChit initialLocation;
	private boolean hiding;
	private ArrayList<Chit> belongings;
	private ArrayList<Phase> specialPhases;

	public Character(CharacterType t) {
		System.out
				.println("WARNING: only use the CharacterFactory class to create characters, this inssures full initialization. ");
		type = t;
		initialLocation = GameConfiguration.INITIAL_SITE;
		hiding = true;
	}

	public CharacterType getType() {
		return type;
	}

	public void setType(CharacterType type) {
		this.type = type;
	}

	public boolean isHiding() {
		return hiding;
	}

	public void setHiding(boolean hiding) {
		this.hiding = hiding;
	}

	public ArrayList<Chit> getBelongings() {
		return belongings;
	}

	public void setBelongings(ArrayList<Chit> belongings) {
		this.belongings = belongings;
	}

	public ValleyChit getInitialLocation() {
		return initialLocation;
	}

	public void setSpecialPhases(ArrayList<Phase> specialPhases) {
		this.specialPhases = specialPhases;
	}

	public ArrayList<Phase> getSpecialPhases() {

		return specialPhases;
	}

}
