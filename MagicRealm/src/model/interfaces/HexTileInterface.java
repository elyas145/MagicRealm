package model.interfaces;

import java.io.Serializable;
import java.util.Collection;

import communication.handler.server.serialized.SerializedTile;
import model.board.clearing.Clearing;
import model.counter.chit.Chit;
import model.enums.TileName;
import model.enums.LandType;

public interface HexTileInterface extends Serializable{

	/**
	 * @return name of hex tile
	 */
	TileName getName();

	/**
	 * @return type of hex tile
	 */
	boolean isEnchanted();

	/**
	 * @return tile column on the board
	 */
	int getBoardColumn();

	/**
	 * @return tile row on the board
	 */
	int getBoardRow();

	/**
	 * @return rotation of the tile 0 for up, 1 for 60deg clockwise, 3 for down
	 */
	int getRotation();

	/**
	 * @return list of clearings
	 */
	Collection<? extends ClearingInterface> getClearings();

	/**
	 * @return list of clearings
	 */
	ClearingInterface getClearing(int clearingNum);

	/**
	 * @return the Clearing that is at the tiles entrance
	 */
	Clearing getEntryClearing(int rot);
	Clearing getEntryClearing(int rot, boolean enchant);
	
	void connectTo(TileName tile, int exit);
	Iterable<TileName> getSurrounding();

	SerializedTile getSerializedTile();
}
