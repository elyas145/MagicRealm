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
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import config.BoardConfiguration;
import utils.random.Random;
import utils.resources.ResourceHandler;
import model.board.chit.Chit;
import model.board.tile.HexTile;
import model.enums.CharacterType;
import model.enums.ChitType;
import model.enums.SiteType;
import model.enums.TileType;
import model.interfaces.BoardInterface;
import model.interfaces.ChitInterface;
import model.interfaces.HexTileInterface;

public class Board implements BoardInterface {

	public static void main(String[] args) {
		Board b = new Board(new ResourceHandler());
	}

	public Board(ResourceHandler rh) {
		collectionOfTiles = new HashSet<HexTile>();
		try {
			String path = rh.getResource(
					ResourceHandler.joinPath("data", "data.json")).getPath();
			FileReader reader = new FileReader(path);
			JSONParser parser = new JSONParser();
			arr = (JSONArray) parser.parse(reader);
			//TODO fix array loop.
		} catch (Exception e) {
			e.printStackTrace();
		}
		hardCodeTiles();
		initTreasures();
		initSound();

	}
	
	private void initSound() {
		
		
	}

	public void moveCharacter(Character character, TileType tt, int clearing) {
		// TODO move character
		//HexTile loc = mapOfChitPositions.get(character);
		//Clearing clr = loc.getClearing(character);
	}
	
	public void removeCharacter(Character character) {
		// TODO Auto-generated method stub
		
	}

	private void initTreasures() {
		ArrayList<Integer> possibleValues = new ArrayList<Integer>();

		for (int i = 1; i <= BoardConfiguration.MAX_TREASURES; i++) {
			possibleValues.add(i * 10);
		}
		
		ArrayList<SiteType> sites = new ArrayList<SiteType>();
		for(SiteType tt: SiteType.values()) {
			sites.add(tt);
		}

		for (int i = 0; i < BoardConfiguration.MAX_TREASURES; i++) {
			treasures.add(new Treasure(Random.choose(sites), Random.remove(possibleValues)));
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
	public Iterable<? extends HexTileInterface> iterateTiles() {
		return collectionOfTiles;
	}

	@Override
	public Iterable<? extends ChitInterface> iterateChits() {
		return collectionOfChits;
	}

	private void setTile(TileType tile, int x, int y, int rot) {
		collectionOfTiles.add(new HexTile(tile, x, y, rot, arr));
	}

	private Collection<HexTile> collectionOfTiles;
	private Collection<Chit> collectionOfChits;
	private Map<ChitType, HexTile> mapOfChitPositions;
	private ArrayList<Treasure> treasures = new ArrayList<Treasure>();
	private JSONArray arr;
}
