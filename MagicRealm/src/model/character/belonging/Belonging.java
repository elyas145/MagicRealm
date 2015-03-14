package model.character.belonging;

import model.counter.chit.Chit;
import model.enums.CharacterType;

public abstract class Belonging {
	private Chit chit;
	private boolean active;
	private CharacterType owner;
	
	public boolean isActive(){
		return active;
	}
	
	public Chit getChit(){
		return chit;
	}
	public void setChit(Chit c){
		chit = c;
	}

	public void setOwner(CharacterType t) {
		owner = t;		
	}
	
	public CharacterType getOwner(){
		return owner;
	}
}
