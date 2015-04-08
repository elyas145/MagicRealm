package model.interfaces;

import java.io.Serializable;

import communication.handler.server.serialized.SerializedClearing;

import utils.math.linear.Matrix;
import model.enums.PathType;

public interface ClearingInterface extends Serializable{
	
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

	boolean isConnectedTo(ClearingInterface other);

	boolean isConnectedTo(ClearingInterface other, PathType pt);

	Iterable<ClearingInterface> getSurrounding();
	
	Iterable<ClearingInterface> getSurrounding(PathType pt);

	boolean isEnchanted();

	SerializedClearing getSerializedClearing();

}
