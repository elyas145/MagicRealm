package model.board.tile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import model.board.Board;
import model.board.clearing.Clearing;
import model.counter.chit.Chit;
import model.enums.TileName;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;

import org.json.simple.JSONArray;

import utils.math.Mathf;
import utils.math.Matrix;
import utils.math.Point;

public class HexTile implements HexTileInterface {

	public HexTile(Board par, TileName tp, int rw, int col, int rot) {
		parent = par;
		name = tp;
		row = rw;
		column = col;
		rotation = rot;
		clearings = new HashMap<Integer, Clearing>();
		exits = new int[2][6];
		clearExits();
	}

	public HexTile(Board par, TileName tile, int x, int y, int rot,
			Map<Integer, Point[]> locations, TileName[] surrounding) {
		parent = par;
		name = tile;
		row = x;
		column = y;
		rotation = rot;
		clearings = new HashMap<Integer, Clearing>();
		for (Map.Entry<Integer, Point[]> ents : locations.entrySet()) {
			Point[] pts = ents.getValue();
			int num = ents.getKey();
			if (pts[0] == null) {
				throw new RuntimeException(
						"Missing normal position data for tile " + name
								+ ", clearing " + num);
			}
			if (pts[1] == null) {
				throw new RuntimeException(
						"Missing enchanted position data for tile " + name
								+ ", clearing " + num);
			}
			Clearing cl = new Clearing(this, num, transform(pts[0]),
					transform(pts[1]));
			clearings.put(num, cl);
		}
		exits = new int[2][6];
		clearExits();
		connectClearings();
		for(int i = 0; i < surrounding.length; ++i) {
			HexTileInterface other = getParent().getTile(surrounding[i]);
			if(other != null) {
				int entr = i + 3 % 6;
				ClearingInterface thc = getEntryClearing(i, false);
				thc.connectTo(other, entr, false);
				thc = getEntryClearing(i, true);
				thc.connectTo(other, entr, true);
				//other.connectTo(this);
			}
		}
	}
	
	@Override
	public Clearing getEntryClearing(int rot) {
		return getEntryClearing(rot, isEnchanted());
	}
	
	@Override
	public Clearing getEntryClearing(int rot, boolean enchant) {
		int entr = exits[enchant ? 1 : 0][rot % 6];
		return clearings.get(entr);
	}

	/*
	 * public HexTile(TileName tile, int x, int y, int rot, JSONArray arr) {
	 * name = tile; row = x; column = y; rotation = rot; this.arr = arr;
	 * clearings = new HashMap<Integer, Clearing>(); //setClearings(); }
	 */

	@Override
	public Collection<? extends ClearingInterface> getClearings() {
		return clearings.values();
	}

	@Override
	public ClearingInterface getClearing(int num) {
		return clearings.get(num);
	}

	public int getBoardRow() {
		return row;
	}

	public int getBoardColumn() {
		return column;
	}

	public void setRotation(int rot) {
		rotation = rot;
	}

	@Override
	public TileName getName() {
		return name;
	}

	@Override
	public int getRotation() {
		return rotation;
	}

	@Override
	public boolean isEnchanted() {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected Board getParent() {
		return parent;
	}

	/**
	 * reads the data provided by the JSONArray "arr" locates the tile, and
	 * reads the clearings and their corresponding locations
	 */
	/*
	 * private void setClearings() { // TODO get clearing location from external
	 * file, and create the // clearings that go with this tile. for (int i = 0;
	 * i < arr.size(); i++) { JSONObject obj = (JSONObject) arr.get(i); String
	 * nm = (String) obj.get("tileName"); if (nm.equals(name.name())) { //
	 * System.out.println("-------------------------------"); //
	 * System.out.println("tile: " + name); JSONObject numbers = (JSONObject)
	 * obj.get("numbers"); Boolean enchanted = (Boolean) obj.get("enchanted");
	 * // System.out.println("enchanted: " + enchanted); for (int j = 0; j <=
	 * BoardConfiguration.MAX_CLEARINGS_IN_TILE; j++) { JSONObject n =
	 * (JSONObject) numbers.get("" + j); if (n != null) { // create a point
	 * instance // System.out.println("setting clearing: " + j); Point p = new
	 * Point(); p.setX((Long) n.get("x")); p.setY((Long) n.get("y")); if
	 * (!containsClearing(j)) { // create the clearing //
	 * System.out.println("creating the clearing: " + // j); Clearing c = new
	 * Clearing(); if (enchanted) c.setLocation_e(p); else c.setLocation(p);
	 * c.setNumber(j); // clearings.add(c); } else { //
	 * System.out.println("clearing already exists: " + // j);
	 * setClearingLocation(j, p, true); } } }
	 * 
	 * } } }
	 */

	private void clearExits() {
		for (int i = 0; i < exits.length; ++i) {
			int[] tmp = exits[i];
			for (int j = 0; j < tmp.length; ++j) {
				tmp[j] = 0;
			}
		}
	}

	private void connectClearings() {
		switch (name) {
		case AWFUL_VALLEY:
			connectAlways(1, 4);
			connectAlways(2, 5);
			exitAlways(5, 0);
			exitAlways(4, 1);
			exitAlways(4, 2);
			exitAlways(2, 4);
			exitAlways(1, 5);
			break;
		case BAD_VALLEY:
			connectAlways(1, 4);
			connectAlways(2, 5);
			exitAlways(4, 0);
			exitAlways(5, 1);
			exitAlways(1, 2);
			exitAlways(2, 4);
			exitAlways(4, 5);
			break;
		case BORDERLAND:
			connectAlways(1, 6);
			connectAlways(2, 3);
			connectAlways(4, 5, true);
			connectAlways(4, 6);
			exitAlways(1, 0);
			exitAlways(2, 1);
			exitAlways(4, 2);
			exitAlways(2, 3);
			exitAlways(1, 4);
			exitAlways(5, 5);
			break;
		case CAVERN:
			connect(1, 2, true, true);
			connect(1, 3, true, true);
			connect(1, 4, false, true);
			connect(1, 4, true, false);
			connect(2, 3, false, false);
			connect(2, 6, true, false);
			connect(3, 5, false, true);
			connect(3, 5, true, false);
			connectAlways(3, 6);
			connect(4, 5, false, false);
			connect(4, 5, true, true);
			connectAlways(4, 6);
			exitAlways(2, 0);
			exitAlways(1, 1);
			exitAlways(5, 5);
			break;
		case CAVES:
			connect(1, 4, true, false);
			connect(1, 6, false, false);
			connect(1, 6, false, true);
			connect(2, 3, false, true);
			connect(2, 3, true, false);
			connect(2, 4, false, false);
			connect(3, 4, true, false);
			connect(3, 5, false, false);
			connect(5, 6, true, false);
			exitAlways(1, 2);
			exitAlways(2, 4);
			exitAlways(5, 5);
			break;
		case CLIFF:
			connectAlways(1, 6);
			connectAlways(2, 3);
			connectAlways(2, 5, true);
			connectAlways(3, 5);
			connectAlways(3, 6, true);
			connectAlways(4, 6);
			exitAlways(4, 1);
			exitAlways(5, 2);
			exitAlways(2, 4);
			exitAlways(1, 5);
			break;
		case CRAG:
			connectAlways(1, 4);
			connect(1, 6, false, true);
			connectAlways(2, 3, true);
			connectAlways(2, 5);
			connectAlways(3, 5);
			connectAlways(3, 6);
			connect(4, 5, true, true);
			connectAlways(4, 6);
			exitAlways(2, 0);
			break;
		case CURST_VALLEY:
			connectAlways(1, 4);
			connectAlways(2, 5);
			exitAlways(1, 0);
			exitAlways(5, 1);
			exitAlways(4, 2);
			exitAlways(4, 4);
			exitAlways(2, 5);
			break;
		case DARK_VALLEY:
			connectAlways(1, 4);
			connectAlways(2, 5);
			exitAlways(2, 0);
			exitAlways(1, 1);
			exitAlways(5, 2);
			exitAlways(4, 4);
			exitAlways(4, 5);
			break;
		case DEEP_WOODS:
			connect(1, 3, true, false);
			connect(1, 4, false, true);
			connect(1, 4, true, false);
			connect(1, 6, true, false);
			connect(2, 3, false, false);
			connect(2, 5, true, false);
			connect(2, 6, true, false);
			connect(3, 4, true, true);
			connectAlways(3, 5);
			connectAlways(3, 6, true);
			connect(4, 5, false, false);
			connect(4, 6, false, false);
			connect(4, 6, true, true);
			exit(1, 0, false);
			exit(2, 1, false);
			exit(2, 2, false);
			exit(5, 4, false);
			exit(1, 5, false);
			exit(6, 0, true);
			exit(6, 1, true);
			exit(3, 2, true);
			exit(4, 4, true);
			exit(4, 5, true);
			break;
		case EVIL_VALLEY:
			connectAlways(1, 4);
			connectAlways(2, 5);
			exitAlways(4, 0);
			exitAlways(4, 1);
			exitAlways(5, 2);
			exitAlways(1, 4);
			exitAlways(2, 5);
			break;
		case HIGH_PASS:
			connectAlways(1, 4);
			connectAlways(1, 5);
			connectAlways(2, 4);
			connectAlways(3, 6);
			exitAlways(2, 0);
			exitAlways(3, 1);
			exitAlways(5, 3);
			exitAlways(6, 5);
			break;
		case LEDGES:
			connectAlways(1, 3, true);
			connectAlways(1, 4);
			connect(1, 6, false, false);
			connectAlways(2, 5);
			connectAlways(3, 6);
			connectAlways(4, 6, true);
			exitAlways(4, 0);
			exitAlways(2, 1);
			exitAlways(3, 2);
			exitAlways(5, 5);
			break;
		case LINDEN_WOODS:
			connect(2, 4, false, false);
			connect(4, 5, true, false);
			exitAlways(5, 0);
			exitAlways(5, 1);
			exitAlways(2, 2);
			exitAlways(2, 4);
			exitAlways(4, 5);
			break;
		case MAPLE_WOODS:
			connect(2, 4, false, false);
			connect(4, 5, true, false);
			exitAlways(2, 0);
			exitAlways(2, 1);
			exitAlways(4, 2);
			exitAlways(5, 4);
			exitAlways(5, 5);
			break;
		case MOUNTAIN:
			connectAlways(1, 3);
			connect(1, 4, true, true);
			connectAlways(2, 4);
			connectAlways(2, 5);
			connect(3, 6, false, false);
			connect(3, 6, true, true);
			connect(4, 6, false, true);
			connect(4, 6, true, false);
			connect(5, 6, false, false);
			connect(5, 6, true, true);
			exitAlways(4, 0);
			exitAlways(5, 2);
			exitAlways(2, 4);
			break;
		case NUT_WOODS:
			connect(2, 4, false, false);
			connect(4, 5, true, false);
			exitAlways(2, 0);
			exitAlways(4, 1);
			exitAlways(5, 2);
			exitAlways(5, 4);
			exitAlways(2, 5);
			break;
		case OAK_WOODS:
			connect(2, 4, false, false);
			connect(4, 5, true, false);
			exitAlways(5, 0);
			exitAlways(2, 1);
			exitAlways(2, 2);
			exitAlways(4, 4);
			exitAlways(5, 5);
			break;
		case PINE_WOODS:
			connect(2, 4, false, false);
			connect(4, 5, true, false);
			exitAlways(4, 0);
			exitAlways(5, 1);
			exitAlways(5, 2);
			exitAlways(2, 4);
			exitAlways(2, 5);
			break;
		case RUINS:
			connect(1, 2, false, false);
			connect(1, 2, true, true);
			connect(1, 4, false, false);
			connect(1, 4, true, true);
			connect(1, 5, false, true);
			connect(1, 5, true, false);
			connect(2, 6, true, true);
			connectAlways(3, 5);
			connect(3, 6, false, false);
			connect(3, 6, true, true);
			connectAlways(4, 6);
			exitAlways(2, 0);
			exit(2, 1, false);
			exit(6, 1, true);
			exitAlways(3, 2);
			exitAlways(5, 4);
			exitAlways(1, 5);
			break;
		default:
			break;
		}
	}

	private void connectAlways(int a, int b) {
		connectAlways(a, b, false);
	}

	private void connectAlways(int a, int b, boolean hidden) {
		connect(a, b, false, hidden);
		connect(a, b, true, hidden);
	}

	private void connect(int a, int b, boolean enchanted, boolean hidden) {
		clearings.get(a).connectTo(clearings.get(b), enchanted, hidden);
	}

	private void exitAlways(int clear, int rot) {
		exit(clear, rot, false);
		exit(clear, rot, true);
	}

	private void exit(int clear, int rot, boolean ench) {
		rot += getRotation();
		rot %= exits.length;
		exits[ench ? 1 : 0][rot] = clear;
	}

	private Point transform(Point point) {
		Matrix p = Matrix
				.columnVector(new float[] { point.getX(), point.getY() });
		p = p.multiply(2f)
				.subtract(Matrix.columnVector(new float[] { 1f, 1f }));
		p = Matrix.dilation(new float[] { 1f, 0.866025f }).multiply(p);
		p = Matrix.rotation(-Mathf.PI * getRotation() / 3f).multiply(p);
		return new Point(p.get(0, 0), p.get(1, 0));
	}

	private Board parent;
	private int[][] exits;
	private TileName name;
	private int row;
	private int column;
	private Map<Integer, Clearing> clearings;
	private Map<Chit, Clearing> chitMap;
	private int rotation;
}
