package model.board;

import java.util.ArrayList;

import model.enums.CharacterType;
import model.enums.SiteType;
import model.interfaces.CharacterInterface;

public class Character implements CharacterInterface {
	private CharacterType type;
	private SiteType initialLocation;
	private boolean hiding;
	private ArrayList<Chit> belongings;

	public Character(CharacterType t){
		type = t;
		initialLocation = SiteType.INN;
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

	public SiteType getInitialLocation() {
		return initialLocation;
	}
}
