package model.character.belonging;

import model.enums.ArmorType;

public class Armor extends Belonging{
	private char toughness;
	private ArmorType armorType;

	public Armor(ArmorType helmet, char toughness){
		this.armorType = helmet;
		this.toughness = toughness;		
	}

	public char getToughness() {
		return toughness;
	}

	public ArmorType getArmorType() {
		return armorType;
	}

	public void setToughness(char toughness) {
		this.toughness = toughness;
	}
	
	public void setArmorType(ArmorType type) {
		this.armorType = type;
	}

}
