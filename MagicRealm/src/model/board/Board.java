/**
 * This is the board class.. in case you didn't know already
 * array layout: top left corner is 0,0 
 * the hardCodeTiles() method literaly hardcodes the tiles into the array
 * I added a new tile type called "empty" to represent an index with no tile.
 */

package model.board;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import utils.resources.ResourceHandler;

import model.enums.TileType;
import model.interfaces.BoardInterface;

public class Board implements Iterable<HexTile>, BoardInterface {

	public Board() {
		collectionOfTiles = new HashSet<HexTile>();
		// get tile info
				//System.getProperty("user.dir") + "\\bin\\resources\\data\\data.json";
		try {
			String path = new ResourceHandler().getResource(ResourceHandler.joinPath("data", "data.json")).getPath();
			FileReader reader = new FileReader(path);
			JSONParser parser = new JSONParser();
			arr = (JSONArray) parser.parse(reader);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		hardCodeTiles();
		initTreasures();
		
		
	}

	public static void main(String[] args){
		Board b = new Board();
	}
	private void initTreasures() {
		ArrayList<Integer> possibleValues = new ArrayList<Integer>();
		
		for(int i = 1; i < 6; i++){
			possibleValues.add(i*10);
		}
		
		for (int i = 0; i < treasures.size(); i++){
			treasures.add(new Treasure(possibleValues));
			possibleValues.remove(treasures.get(treasures.size()-1));
		}	
	}

	private void hardCodeTiles() {
		// this setup is based on the picture found here, rotated left to match
		// video:
		// http://people.scs.carleton.ca/~jeanpier//304W15/Board%20for%20iteration%201/IMG_4841.jpg
		// I am rotating the actual image files, so they look like they fit
		// together.

		setTile(TileType.CURST_VALLEY, 1, 0, 4);
		setTile(TileType.AWFUL_VALLEY, 2, 0, 4);

		setTile(TileType.DARK_VALLEY, 0, 1, 0);
		setTile(TileType.NUT_WOODS, 1, 1, 1);
		setTile(TileType.LINDEN_WOODS, 2, 1, 5);

		setTile(TileType.DEEP_WOODS, 1, 2, 4);
		setTile(TileType.RUINS, 2, 2, 0);

		setTile(TileType.CRAG, 0, 3, 1);
		setTile(TileType.MAPLE_WOODS, 1, 3, 0);

		setTile(TileType.OAK_WOODS, 1, 4, 2);
		setTile(TileType.CAVES, 2, 4, 0);

		setTile(TileType.LEDGES, 0, 5, 2);
		setTile(TileType.BAD_VALLEY, 1, 5, 3);
		setTile(TileType.PINE_WOODS, 2, 5, 4);

		setTile(TileType.CLIFF, 0, 6, 3);
		setTile(TileType.BORDER_LAND, 1, 6, 3);
		setTile(TileType.MOUNTAIN, 2, 6, 3);

		setTile(TileType.EVIL_VALLEY, 0, 7, 0);
		setTile(TileType.CAVERN, 1, 7, 5);

		setTile(TileType.HIGH_PASS, 1, 8, 0);

	}

	@Override
	public Iterator<HexTile> iterator() {
		return collectionOfTiles.iterator();
	}

	private void setTile(TileType tile, int x, int y, int rot) {
		collectionOfTiles.add(new HexTile(tile, x, y, rot, arr));
	}

	private Collection<HexTile> collectionOfTiles;
	private ArrayList<Treasure> treasures = new ArrayList<Treasure>();
	private JSONArray arr;
}
