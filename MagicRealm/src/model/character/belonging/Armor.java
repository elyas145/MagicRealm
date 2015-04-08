package model.character.belonging;

import java.io.Serializable;
import model.enums.BelongingType;

public class Armor extends Belonging{
	private char toughness;

	public Armor(BelongingType t, char toughness){
		super(null, true, t);
		this.toughness = toughness;		
	}

	public char getToughness() {
		return toughness;
	}

	public void setToughness(char toughness) {
		this.toughness = toughness;
	}

	@Override
	public BelongingType getType() {
		return null;// type.toBelonging();
	}

	@Override
	public void setType(BelongingType type) {
		// TODO Auto-generated method stub
		
	}

}
