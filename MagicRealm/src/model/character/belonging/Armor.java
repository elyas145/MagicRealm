package model.character.belonging;

import model.enums.BelongingType;

public class Armor extends Belonging {

	private char toughness;

	public Armor(BelongingType t, char toughness) {
		super(null, true, t);
		this.toughness = toughness;
	}

	public char getToughness() {
		return toughness;
	}

	public void setToughness(char toughness) {
		this.toughness = toughness;
	}

	private static final long serialVersionUID = -1717487024742129493L;

}
