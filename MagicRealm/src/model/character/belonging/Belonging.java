package model.character.belonging;

import java.io.Serializable;

import model.counter.chit.Chit;
import model.enums.CharacterType;
import model.interfaces.BelongingType;

public abstract class Belonging implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9216766079985487056L;
	private Chit chit;
	private boolean active;
	
	public abstract BelongingType getType();

	public abstract void setType(BelongingType type);

	public boolean isActive() {
		return active;
	}

	public Chit getChit() {
		return chit;
	}

	public void setChit(Chit c) {
		chit = c;
	}
}
