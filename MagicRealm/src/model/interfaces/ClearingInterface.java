package model.interfaces;

import utils.math.linear.Matrix;

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
	void getPosition(boolean enchanted, Matrix dest);
	
	/**
	 * @return the parent tile
	 */
	HexTileInterface getParentTile();

	void connectTo(ClearingInterface other, boolean ench, PathType pt);

	void connectTo(HexTileInterface other, int entr, boolean ench);

	boolean isConnectedTo(ClearingInterface other);

	boolean isConnectedTo(ClearingInterface other, PathType pt);

	Iterable<ClearingInterface> getSurrounding();
	
	Iterable<ClearingInterface> getSurrounding(PathType pt);

	boolean isEnchanted();

}
