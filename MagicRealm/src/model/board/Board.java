/**
 * This is the board class.. in case you didn't know already
 * array layout: top left corner is 0,0 
 * the hardCodeTiles() method literaly hardcodes the tiles into the array
 * I added a new tile type called "empty" to represent an index with no tile.
 */

package model.board;

import java.util.Iterator;

public class Board implements Iterable<HexTile> {
	private HexTile[][] tiles= new HexTile[5][6]; // based on longest row and longest column
	
	public Board(){
		hardCodeTiles();
	}
	private void hardCodeTiles() {
		// this setup is based on the picture found here, rotated left to match video:
		//http://people.scs.carleton.ca/~jeanpier//304W15/Board%20for%20iteration%201/IMG_4841.jpg
		//I have rotated the actual image files, so they look like they fit together.
		
		tiles[0][0] = new HexTile(TileType.EMPTY, 0, 0);
		tiles[0][1] = new HexTile(TileType.EMPTY, 0, 0);
		tiles[0][2] = new HexTile(TileType.DARK_VALLEY, 0, 0);
		tiles[0][3] = new HexTile(TileType.CURST_VALLEY, 0, 0);
		tiles[0][4] = new HexTile(TileType.EMPTY, 0, 0);
		tiles[0][5] = new HexTile(TileType.EMPTY, 0, 0);
		
		tiles[0][0] = new HexTile(TileType.EMPTY, 0, 0);
		tiles[0][1] = new HexTile(TileType.CRAG, 0, 0);
		tiles[0][2] = new HexTile(TileType.DEEP_WOODS, 0, 0);
		tiles[0][3] = new HexTile(TileType.NUT_WOODS, 0, 0);
		tiles[0][4] = new HexTile(TileType.AWFUL_VALLEY, 0, 0);
		tiles[0][5] = new HexTile(TileType.EMPTY, 0, 0);
		
		tiles[0][0] = new HexTile(TileType.CLIFF, 0, 0);
		tiles[0][1] = new HexTile(TileType.LEDGES, 0, 0);
		tiles[0][2] = new HexTile(TileType.OAK_WOODS, 0, 0);
		tiles[0][3] = new HexTile(TileType.MAPLE_WOODS, 0, 0);
		tiles[0][4] = new HexTile(TileType.RUINS, 0, 0);
		tiles[0][5] = new HexTile(TileType.LINDEN_WOODS, 0, 0);
		
		tiles[0][0] = new HexTile(TileType.EVIL_VALLEY, 0, 0);
		tiles[0][1] = new HexTile(TileType.BORDER_LAND, 0, 0);
		tiles[0][2] = new HexTile(TileType.BAD_VALLEY, 0, 0);
		tiles[0][3] = new HexTile(TileType.CAVES, 0, 0);
		tiles[0][4] = new HexTile(TileType.EMPTY, 0, 0);
		tiles[0][5] = new HexTile(TileType.EMPTY, 0, 0);
		
		tiles[0][0] = new HexTile(TileType.EMPTY, 0, 0);
		tiles[0][1] = new HexTile(TileType.HIGH_PASS, 0, 0);
		tiles[0][2] = new HexTile(TileType.CAVERN, 0, 0);
		tiles[0][3] = new HexTile(TileType.MOUNTAIN, 0, 0);
		tiles[0][4] = new HexTile(TileType.PINE_WOODS, 0, 0);
		tiles[0][5] = new HexTile(TileType.EMPTY, 0, 0);		
	}
	@Override
	public Iterator<HexTile> iterator() {
		return null;
	}

}
