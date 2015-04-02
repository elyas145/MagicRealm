package model.character;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import config.GameConfiguration;
import model.EnchantedHolder;
import model.character.belonging.Belonging;
import model.counter.chit.Chit;
import model.enums.CharacterType;
import model.enums.PathType;
import model.enums.ValleyChit;
import model.interfaces.CharacterInterface;
import model.interfaces.ClearingInterface;

public class Character implements CharacterInterface, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3972396707482617354L;
	private ArrayList<Belonging> belongings;
	private boolean hiding;
	private ValleyChit initialLocation;
	private ArrayList<Phase> specialPhases;
	private CharacterType type;

	public Character(CharacterType t) {
		type = t;
		initialLocation = GameConfiguration.INITIAL_SITE;
		hiding = true;
	}
	public void setInitialLocation(ValleyChit chit){
		initialLocation = chit;
	}
	public void AddBelonging(Belonging b) {
		belongings.add(b);
	}

	public ArrayList<Belonging> getBelongings() {
		return belongings;
	}

	public ValleyChit getInitialLocation() {
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

}
