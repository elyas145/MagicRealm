package model.interfaces;

import java.nio.FloatBuffer;

import model.enums.PathType;

public interface ClearingInterface {
	
	/**
	 * @return the clearing number
	 */
	int getClearingNumber();
	
	/**
	 * @return into destination
	 * 	       the position of the clearing in the unit square
	 */
	void getPosition(boolean enchanted, FloatBuffer dest);
	
	/**
	 * @return the parent tile
	 */
	HexTileInterface getParentTile();
	
	/**
	 * @return random connected clearing for testing
	 */
	ClearingInterface getRandomConnection();

	void connectTo(ClearingInterface other, boolean ench, PathType pt);

	void connectTo(HexTileInterface other, int entr, boolean ench);

	boolean isConnectedTo(ClearingInterface other);

}
