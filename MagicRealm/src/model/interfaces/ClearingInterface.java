package model.interfaces;

import java.nio.FloatBuffer;

import model.board.clearing.Clearing;
import model.board.tile.HexTile;

public interface ClearingInterface {
	
	/**
	 * @return the clearing number
	 */
	int getClearingNumber();
	
	/**
	 * @return into destination
	 * 	       the position of the clearing in the unit square
	 */
	void getTilePosition(boolean enchanted, FloatBuffer dest);
	
	/**
	 * @return random connected clearing for testing
	 */
	ClearingInterface getRandomConnection();

	void connectTo(Clearing other, boolean ench, boolean hid);

	void connectTo(HexTileInterface other, int entr, boolean ench);

	boolean isConnectedTo(Clearing other);

}
