package model.character.belonging;

import java.io.Serializable;

import model.enums.ArmorType;
import model.interfaces.BelongingType;

public class Armor extends Belonging{
	private char toughness;
	private ArmorType type;

	public Armor(ArmorType t, char toughness){
		this.type = t;
		this.toughness = toughness;		
	}

	public char getToughness() {
		return toughness;
	}

	public ArmorType getArmorType() {
		return type;
	}

	public void setToughness(char toughness) {
		this.toughness = toughness;
	}
	
	public void setArmorType(ArmorType type) {
		this.type = type;
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
