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
package model.board;

import java.util.ArrayList;
import java.util.Random;

import model.board.chit.Chit;
import model.enums.TileType;
import model.enums.TreasureType;

public class Treasure extends Chit {
	private int goldValue;
	private Random gen;

	/*
	private boolean enchanted = false;

	public boolean isEnchanted() {
		return enchanted;
	}

	public void setEnchanted(boolean enchanted) {
		this.enchanted = enchanted;
	}

	public TreasureType getType() {
		return type;
	}

	public void setType(TreasureType type) {
		this.type = type;
	}

	private TreasureType type;

	public Treasure(TileType tile, TreasureType t, boolean e) {
		super(tile);
		type = t;
		enchanted = e;
	}
	*/

	public Treasure(TileType tile, ArrayList<Integer> possibleValues) {
		super(tile);
		goldValue = possibleValues.get(gen.nextInt(possibleValues.size() - 1));
	}

	public int getGoldValue() {
		return goldValue;
	}

	public void setGoldValue(int goldValue) {
		this.goldValue = goldValue;
	}

}
