package model.character.belonging;

import java.io.Serializable;

import model.counter.chit.Chit;
import model.enums.BelongingType;
import model.enums.CharacterType;

public abstract class Belonging implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9216766079985487056L;
	private Chit chit;
	private boolean active;
	private BelongingType type;
	
	public Belonging(Chit chit, boolean active, BelongingType type){
		
		this.chit = chit;
		this.active = active;
		this.type = type;
	}
	public void setType(BelongingType type){
		this.type = type;
	}
	public BelongingType getType(){
		return this.type;
	}

	public boolean isActive() {
		return active;
	}

	public Chit getChit() {
		return chit;
	}

	public void setChit(Chit c) {
		chit = c;
	}
	public String toString(){
		return type.toString();
	}
}
