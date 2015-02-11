/**
 * This class identifies a treasure card. 2nd edition page 67
 * 1. if a card is enchanted, it must be turned face up and active for the rest of the game, even when it is sold to a denizen or abandoned
 * 1.1 if an enchanted card has an effect, this effects every character in its clearing.
 * 1.2 has to do with magic. (not implemented)
 * 2. armor treasure not implemented
 * 3. weapon treasure not implemented
 * potion treasure not implemented
 * 
 * We are only implementing treasures affecting the use of tables:
 * First encounter:
 * Deft gloves
 * Lost keys
 * lucky charm
 * magic wand?
 * Map of lost castle
 * map of lost city
 * map of ruins
 * shoes of stealth
 * 
 * fourth encounter:
 * phantom glass
 * 
 */
package model.board;

public class Treasure extends Chit{
	private boolean enchanted = false;
	
	public Treasure(){
		super();
		//super.type = new TreasureType(); this would be cool! TODO
	}	
}
