/**
 * This is the board class.. in case you didn't know already
 * array layout: top left corner is 0,0 
 * the hardCodeTiles() method literaly hardcodes the tiles into the array
 * I added a new tile type called "empty" to represent an index with no tile.
 */

package model.board;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class Board implements Iterable<HexTile>, BoeardInterface {
	private Collection<HexTile> collectionOfTiles;
	
	public Board(){
		collectionOfTiles = new HashSet<HexTile>();
		hardCodeTiles();
	}
	private void hardCodeTiles() {
		// this setup is based on the picture found here, rotated left to match video:
		//http://people.scs.carleton.ca/~jeanpier//304W15/Board%20for%20iteration%201/IMG_4841.jpg
		//I am rotating the actual image files, so they look like they fit together.

		setTile(TileType.DARK_VALLEY, 0, 2);
		setTile(TileType.CURST_VALLEY, 0, 3);
		
		setTile(TileType.CRAG, 1, 1);
		setTile(TileType.DEEP_WOODS, 1, 2);
		setTile(TileType.NUT_WOODS, 1, 3);
		setTile(TileType.AWFUL_VALLEY, 1, 4);
		
		setTile(TileType.CLIFF, 2, 0);
		setTile(TileType.LEDGES, 2, 1);
		setTile(TileType.OAK_WOODS, 2, 2);
		setTile(TileType.MAPLE_WOODS, 2, 3);
		setTile(TileType.RUINS, 2, 4);
		setTile(TileType.LINDEN_WOODS, 2, 5);
		
		setTile(TileType.EVIL_VALLEY, 3, 0);
		setTile(TileType.BORDER_LAND, 3, 1);
		setTile(TileType.BAD_VALLEY, 3, 2);
		setTile(TileType.CAVES, 3, 3);
		
		setTile(TileType.HIGH_PASS, 4, 1);
		setTile(TileType.CAVERN, 4, 2);
		setTile(TileType.MOUNTAIN, 4, 3);
		setTile(TileType.PINE_WOODS, 4, 4);	
	}
	
	@Override
	public Iterator<HexTile> iterator() {
		return collectionOfTiles.iterator();
	}
	
	private void setTile(TileType tile, int x, int y) {
		collectionOfTiles.add(new HexTile(tile, x, y));
	}

}
