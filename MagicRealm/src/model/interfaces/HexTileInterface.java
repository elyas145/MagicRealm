package model.interfaces;

import java.util.Collection;

import model.counter.chit.Chit;
import model.enums.TileName;
import model.enums.TileType;

public interface HexTileInterface {

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
	ClearingInterface getClearing(Chit chit);

}
