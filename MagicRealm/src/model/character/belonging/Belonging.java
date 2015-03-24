package model.character.belonging;

import model.counter.chit.Chit;
import model.enums.CharacterType;
import model.interfaces.BelongingType;

public abstract class Belonging {
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
