/**
 * This is the board class.. in case you didn't know already
 * array layout: top left corner is 0,0 
 * the hardCodeTiles() method literaly hardcodes the tiles into the array
 * I added a new tile type called "empty" to represent an index with no tile.
 */

package model.board;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import communication.handler.server.serialized.SerializedBoard;
import communication.handler.server.serialized.SerializedClearing;
import communication.handler.server.serialized.SerializedMapChit;
import communication.handler.server.serialized.SerializedTile;
import config.GraphicsConfiguration;
import utils.math.Point;
import utils.resources.ResourceHandler;
import model.EnchantedHolder;
import model.board.clearing.Clearing;
import model.board.tile.HexTile;
import model.counter.chit.MapChit;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.LandType;
import model.enums.MapChitType;
import model.enums.TileName;
import model.interfaces.BoardInterface;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;

public class Board implements BoardInterface {

	public static void main(String[] args) {
		new Board(new ResourceHandler());
	}

	public Board(ResourceHandler rh) {
		mapChitLocations = new ConcurrentHashMap<MapChit, TileName>();
		surround = new TileName[6];
		tileLocations = new HashMap<TileName, int[]>();
		mapOfTileLocations = new HashMap<Integer, Map<Integer, TileName>>();
		mapOfTiles = new HashMap<TileName, HexTile>();
		clearingLocations = new HashMap<TileName, Map<Integer, EnchantedHolder<ClearingData>>>();
		counterPositions = new HashMap<CounterType, Clearing>();
		mapChitsToLoad = new ArrayList<MapChit>();
		
		try {
			String path = rh.getResource(ResourceHandler.joinPath("data",
					"data.json"));
			FileReader reader = new FileReader(path);
			JSONParser parser = new JSONParser();
			arr = (JSONArray) parser.parse(reader);
			for (Object ob : arr) {
				JSONObject js = (JSONObject) ob;
				boolean en = (Boolean) js.get("enchanted");
				TileName tn = TileName.valueOf((String) js.get("tileName"));
				Object tmp = js.get("type");
				String terrain = (String) tmp;
				{
					JSONObject ns = (JSONObject) js.get("numbers");
					if (!clearingLocations.containsKey(tn)) {
						clearingLocations.put(tn,
								new HashMap<Integer, EnchantedHolder<ClearingData>>());
					}
					Map<Integer, EnchantedHolder<ClearingData>> pts = clearingLocations
							.get(tn);
					for (Object key : ns.keySet()) {
						int val = Integer.parseInt((String) key);
						if (!pts.containsKey(val)) {
							pts.put(val, new EnchantedHolder<ClearingData>());
						}
						EnchantedHolder<ClearingData> pt = pts.get(val);
						JSONObject jpt = (JSONObject) ns.get(key);
						long x = (Long) jpt.get("x");
						long y = (Long) jpt.get("y");
						tmp = jpt.get("type");
						if(tmp != null) {
							terrain = (String) tmp;
						}
						pt.set(en, new ClearingData(x, y, terrain));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		hardCodeTiles();

	}

	/**
	 * generates a board only good enough to be drawn on the screen. the board
	 * generated does not have tiles connected and has no mechanism. The
	 * legality of phases is done on the server side. this method should only be
	 * called on the client side once the client receives a new board.
	 * 
	 * @param sboard
	 */
	public Board(SerializedBoard sboard) {
		Map<TileName, SerializedTile> stiles = sboard.getsMapOfTiles();
		mapOfTiles = new HashMap<TileName, HexTile>();
		for (TileName name : stiles.keySet()) {
			mapOfTiles.put(name, new HexTile(stiles.get(name), false));
		}
		counterPositions = new HashMap<CounterType, Clearing>();
		for (CounterType type : sboard.getCounterPositions().keySet()) {
			counterPositions.put(type, new Clearing(sboard
					.getCounterPositions().get(type)));
		}
		mapChitsToLoad = new ArrayList<MapChit>();
		mapChitLocations = new ConcurrentHashMap<MapChit, TileName>();
		for (SerializedMapChit name : sboard.getMapChitLocations().keySet()) {
			MapChit c = new MapChit(name);
			mapChitLocations.put(c,sboard.getMapChitLocations()
					.get(name));
			mapChitsToLoad.add(c);
		}
		mapOfTileLocations = sboard.getMapOfTileLocations();
		tileLocations = sboard.getTileLocations();
		
		boolean flag = false;
		for(SerializedMapChit c : sboard.getMapChitsToLoad()){
			for(MapChit mc : mapChitsToLoad){
				if(c.getType() == mc.getType() && c.getIdentifier() == mc.getIdentifier() && c.getTile() == mc.getTile()){
					flag = true;
					break;
				}
			}
			if(!flag){
				mapChitsToLoad.add(new MapChit(c));
			}
			flag = false;
		}
	}

	public HexTile getTile(TileName tile) {
		return mapOfTiles.get(tile);
	}

	public void moveCharacter(CharacterType character, TileName tt, int clearing) {
		removeCharacter(character);
		counterPositions.put(character.toCounter(), getClearing(tt, clearing));
	}

	public void removeCharacter(CharacterType character) {
		counterPositions.remove(character.toCounter());
	}

	public Clearing getLocationOfCounter(CounterType ct) {
		return counterPositions.get(ct);
	}

	public void setLocationOfCounter(CounterType ct, TileName tn, int clearing) {
		HexTile ht = mapOfTiles.get(tn);
		Clearing cl = ht.getClearing(clearing);
		setClearingOfCounter(ct, cl);
	}

	public void setLocationOfCounter(CounterType ct, CounterType counterType) {
		Clearing ci = getLocationOfCounter(counterType);
		setClearingOfCounter(ct, ci);
	}

	public void setClearingOfCounter(CounterType ct, Clearing cl) {

		counterPositions.put(ct, cl);

	}

	public void removeCounter(CounterType ct) {
		counterPositions.remove(ct);
	}

	public Iterable<CounterType> getCounters() {
		return counterPositions.keySet();
	}

	public Set<TileName> getAllTiles() {
		return mapOfTiles.keySet();
	}

	public ArrayList<ClearingInterface> getConntectedClearings(
			ClearingInterface clearing) {
		ArrayList<ClearingInterface> clearings = new ArrayList<ClearingInterface>();
		for (HexTile tile : mapOfTiles.values()) {
			for (ClearingInterface c : tile.getClearings()) {
				if (clearing.isConnectedTo(c)) {
					clearings.add(c);
				}
			}
		}
		return clearings;
	}

	public boolean isValidMove(CharacterType ct, TileName destTile,
			int destClearing) {
		return getLocationOfCounter(ct.toCounter()).isConnectedTo(
				getClearing(destTile, destClearing));
	}

	public Clearing getClearing(TileName tile, int clearing) {
		HexTile hexTile = mapOfTiles.get(tile);
		return hexTile.getClearing(clearing);
	}

	public void setLocationOfMapChit(MapChit mc, TileName tile) {
		mapChitLocations.put(mc, tile);
		mc.setTile(tile);
	}

	@Override
	public Iterable<? extends HexTileInterface> iterateTiles() {
		return mapOfTiles.values();
	}

	private void hardCodeTiles() {
		// this setup is based on the picture found here, rotated left to match
		// video:
		// http://people.scs.carleton.ca/~jeanpier//304W15/Board%20for%20iteration%201/IMG_4841.jpg
		// I am rotating the actual image files, so they look like they fit
		// together.

		setTile(TileName.CURST_VALLEY, 1, 0, 4);
		setTile(TileName.AWFUL_VALLEY, 2, 0, 4);

		setTile(TileName.DARK_VALLEY, 0, 1, 0);
		setTile(TileName.NUT_WOODS, 1, 1, 1);
		setTile(TileName.LINDEN_WOODS, 2, 1, 5);

		setTile(TileName.DEEP_WOODS, 1, 2, 4);
		setTile(TileName.RUINS, 2, 2, 0);

		setTile(TileName.CRAG, 0, 3, 1);
		setTile(TileName.MAPLE_WOODS, 1, 3, 0);

		setTile(TileName.OAK_WOODS, 1, 4, 2);
		setTile(TileName.CAVES, 2, 4, 0);

		setTile(TileName.LEDGES, 0, 5, 2);
		setTile(TileName.BAD_VALLEY, 1, 5, 3);
		setTile(TileName.PINE_WOODS, 2, 5, 4);

		setTile(TileName.CLIFF, 0, 6, 3);
		setTile(TileName.BORDERLAND, 1, 6, 3);
		setTile(TileName.MOUNTAIN, 2, 6, 3);

		setTile(TileName.EVIL_VALLEY, 0, 7, 0);
		setTile(TileName.CAVERN, 1, 7, 5);

		setTile(TileName.HIGH_PASS, 1, 8, 0);

	}

	private void setTile(TileName tile, int x, int y, int rot) {
		getSurround(x, y, tile);
		Map<Integer, EnchantedHolder<ClearingData>> locs = clearingLocations.get(tile);
		HexTile ht = new HexTile(this, tile, x, y, rot, locs, surround);
		mapOfTiles.put(tile, ht);
		tileLocations.put(tile, new int[] { x, y });
		if (!mapOfTileLocations.containsKey(y)) {
			mapOfTileLocations.put(y, new HashMap<Integer, TileName>());
		}
		Map<Integer, TileName> row = mapOfTileLocations.get(y);
		row.put(x, tile);
	}

	private void clearSurround() {
		for (int i = 0; i < 6; ++i) {
			surround[i] = null;
		}
	}

	private void getSurround(int x, int y, TileName name) {
		clearSurround();
		for (int i = 0; i < surround.length; ++i) {
			setSurround(x, y, i);
		}
	}

	private void setSurround(int x, int y, int rot) {
		rot %= surround.length;
		int nx, ny;
		nx = ny = -1;
		switch (rot) {
		case 0: // up tile
			nx = x;
			ny = y - 2;
			break;
		case 1: // NE tile
			nx = y % 2 == 0 ? x : x + 1;
			ny = y - 1;
			break;
		case 2: // SE tile
			nx = y % 2 == 0 ? x : x + 1;
			ny = y + 1;
			break;
		case 3: // down tile
			nx = x;
			ny = y + 2;
			break;
		case 4: // SW tile
			nx = y % 2 == 0 ? x - 1 : x;
			ny = y + 1;
			break;
		case 5: // NW tile
			nx = y % 2 == 0 ? x - 1 : x;
			ny = y - 1;
			break;
		}
		Map<Integer, TileName> row = mapOfTileLocations.get(ny);
		if (row != null) {
			surround[rot] = row.get(nx);
		}
	}
	
	public class ClearingData {
		public ClearingData(long x, long y, String tp) {
			float a = x
					/ (float) GraphicsConfiguration.TILE_IMAGE_WIDTH;
			float b = y
					/ (float) GraphicsConfiguration.TILE_IMAGE_HEIGHT;
			point = new Point(a, b);
			type = LandType.valueOf(tp);
		}
		public final Point point;
		public final LandType type;
	}

	private Map<TileName, Map<Integer, EnchantedHolder<ClearingData>>> clearingLocations;
	private TileName[] surround;
	// row column
	private Map<Integer, Map<Integer, TileName>> mapOfTileLocations;
	private Map<TileName, HexTile> mapOfTiles;
	private Map<TileName, int[]> tileLocations;
	private Map<MapChit, TileName> mapChitLocations;
	private ArrayList<MapChit> mapChitsToLoad;
	
	public Map<MapChit, TileName> getMapChitLocations() {
		return mapChitLocations;
	}

	public void setMapChitLocations(Map<MapChit, TileName> mapChitLocations) {
		this.mapChitLocations = mapChitLocations;
		mapChitsToLoad = new ArrayList<MapChit>();
		for(MapChit c : mapChitLocations.keySet()){
			mapChitsToLoad.add(c);
		}
	}

	private Map<CounterType, Clearing> counterPositions;
	private JSONArray arr;

	public SerializedBoard getSerializedBoard() {
		SerializedBoard sboard = new SerializedBoard();
		sboard.setMapOfTileLocations(mapOfTileLocations);
		Map<TileName, SerializedTile> sMapOfTiles = new ConcurrentHashMap<TileName, SerializedTile>();
		for (TileName name : mapOfTiles.keySet()) {
			sMapOfTiles.put(name, mapOfTiles.get(name).getSerializedTile());
		}
		sboard.setMapOfTiles(sMapOfTiles);
		sboard.setTileLocations(tileLocations);
		Map<SerializedMapChit, TileName> sMapChits = new HashMap<SerializedMapChit, TileName>();
		for (MapChit name : mapChitLocations.keySet()) {
			sMapChits.put(name.getSerializedChit(), mapChitLocations.get(name));
		}
		ArrayList<SerializedMapChit> toLoad = new ArrayList<SerializedMapChit>();
		for(MapChit c : mapChitsToLoad){
			toLoad.add(c.getSerializedChit());
		}
		for(MapChit c : mapChitLocations.keySet()){
			toLoad.add(c.getSerializedChit());
		}
		sboard.setMapChitsToLoad(toLoad);
		sboard.setMapChitLocations(sMapChits);
		Map<CounterType, SerializedClearing> sCounterPositions = new HashMap<CounterType, SerializedClearing>();
		for (CounterType type : counterPositions.keySet()) {
			sCounterPositions.put(type, counterPositions.get(type)
					.getSerializedClearing());
		}
		sboard.setCounterPositions(sCounterPositions);

		return sboard;
	}

	public CounterType confirmLocationOfDwelling(TileName tile, int clearing) {
		for(CounterType ct : counterPositions.keySet()){
			Clearing c = counterPositions.get(ct);
			if(c.getClearingNumber() == clearing && c.getParentTile().getName() == tile){
				return ct;
			}
		}
		return null;
	}

	public void removeMapChit(MapChit site) {
		mapChitLocations.remove(site);
		
	}

	public void removeMapChit(MapChitType type) {
		for(MapChit c : mapChitLocations.keySet()){
			if(c.getType() == type){
				mapChitLocations.remove(c);
			}
		}
		
	}

	public TileName getMapChitTile(MapChitType type) {
		for(MapChit c : mapChitLocations.keySet()){
			if(c.getType() == type){
				return mapChitLocations.get(c);
			}
		}
		return null;
		
	}

	public MapChit getMapChit(MapChitType type) {
		for(MapChit c : mapChitLocations.keySet()){
			if(c.getType() == type){
				return c;
			}
		}
		return null;
	}

	public ArrayList<MapChit> getMapChitsToLoad() {
		return mapChitsToLoad;
	}

	public void setMapChitsToLoad(Collection<MapChit> mapChitsToLoad) {
		this.mapChitsToLoad = new ArrayList<MapChit> (mapChitsToLoad);
	}

	public void addChitsToLoad(ArrayList<MapChit> array) {
		mapChitsToLoad.addAll(array);
		
	}

	public boolean setEnchantedTile(TileName tile) {
		if(mapOfTiles.get(tile).isEnchanted())
			return mapOfTiles.get(tile).setEnchanted(false);
		else
			return mapOfTiles.get(tile).setEnchanted(true);
		
	}
}
