package model.interfaces;

import model.enums.TileName;

public abstract class ChitInterface {
	
	public boolean onTile() {
		return getTile() != null;
	}
	
	public boolean onClearing() {
		return getClearing() != 0;
	}
	
	/**
	 * @return the tile that the chit is on
	 */
	public abstract TileName getTile();

	/**
	 * @return the clearing that the chit is on
	 *         return 0 if the chit is on the tile
	 */
	public abstract int getClearing();
  
}
