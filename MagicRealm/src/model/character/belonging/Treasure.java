/**
 * This class identifies a treasure card. 2nd edition page 67
 * 1. if a card is enchanted, it must be turned face up and active for the rest of the game, even when it is sold to a denizen or abandoned
 * 1.1 if an enchanted card has an effect, this effects every character in its clearing.
 * 1.2 has to do with magic. (not implemented)
 * 2. armor treasure not implemented
 * 3. weapon treasure not implemented
 * potion treasure not implemented
 * 
 * We are only implementing treasures affecting the use of tables.
 * 
 */
package model.character.belonging;

import model.enums.CharacterType;
import model.enums.ValleyChit;

public class Treasure extends Belonging{
	private int goldValue;
	private ValleyChit site;

	public Treasure(ValleyChit siteType, Integer gv) {
		goldValue = gv;
		site = siteType;
	}
	public void setOwner(CharacterType t){
		super.setOwner(t);
	}
	public ValleyChit getSite() {
		return site;
	}

	public void setSite(ValleyChit site) {
		this.site = site;
	}

	public int getGoldValue() {
		return goldValue;
	}

	public void setGoldValue(int goldValue) {
		this.goldValue = goldValue;
	}

}
