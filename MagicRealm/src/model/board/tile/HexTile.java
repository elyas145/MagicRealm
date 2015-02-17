package model.board.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import model.board.clearing.Clearing;
import model.counter.chit.Chit;
import model.enums.TileName;
import model.enums.TileType;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import config.BoardConfiguration;
import utils.math.Point;

public class HexTile implements HexTileInterface {

	public HexTile(TileName tp, int rw, int col, int rot) {
		name = tp;
		row = rw;
		column = col;
		rotation = rot;
	}

	public HexTile(TileName tile, int x, int y, int rot, JSONArray arr) {
		name = tile;
		row = x;
		column = y;
		rotation = rot;
		this.arr = arr;
		setClearings();
	}

	@Override
	public Collection<? extends ClearingInterface> getClearings() {
		return clearings;
	}
	
	@Override
	public ClearingInterface getClearing(Chit chit) {
		return chitMap.get(chit);
	}

	public void setClearings(ArrayList<Clearing> clearings) {
		this.clearings = clearings;
	}

	/**
	 * reads the data provided by the JSONArray "arr"
	 * locates the tile, and reads the clearings and their corresponding locations
	 */
	private void setClearings() {
		// TODO get clearing location from external file, and create the
		// clearings that go with this tile.
		for (int i = 0; i < arr.size(); i++) {
			JSONObject obj = (JSONObject) arr.get(i);
			String nm = (String) obj.get("tileName");
			if (nm.equals(name.name())) {
				//System.out.println("-------------------------------");
				//System.out.println("tile: " + name);
				JSONObject numbers = (JSONObject) obj.get("numbers");
				Boolean enchanted = (Boolean) obj.get("enchanted");
				//System.out.println("enchanted: " + enchanted);
				for (int j = 0; j <= BoardConfiguration.MAX_CLEARINGS_IN_TILE; j++) {
					JSONObject n = (JSONObject) numbers.get("" + j);
					if (n != null) {
						// create a point instance
						//System.out.println("setting clearing: " + j);
						Point p = new Point();
						p.setX((Long) n.get("x"));
						p.setY((Long) n.get("y"));
						if (!containsClearing(j)) {
							// create the clearing
							//System.out.println("creating the clearing: " + j);
							Clearing c = new Clearing();
							if (enchanted)
								c.setLocation_e(p);
							else
								c.setLocation(p);
							c.setNumber(j);
							clearings.add(c);
						} else {
							//System.out.println("clearing already exists: " + j);
							setClearingLocation(j, p, true);
						}
					}
				}

			}
		}
	}

	private boolean containsClearing(int j) {
		for (Clearing c : clearings) {
			if (c.getNumber() == j)
				return true;
		}
		return false;
	}

	private void setClearingLocation(int clearingNumber, Point p,
			boolean enchanted) {
		for (Clearing c : clearings) {
			if (c.getNumber() == clearingNumber) {
				if (enchanted)
					c.setLocation_e(p);
				else
					c.setLocation(p);
			}
		}

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

	private TileName name;
	private int row;
	private int column;
	private ArrayList<Clearing> clearings = new ArrayList<Clearing>();
	private Map<Chit, Clearing> chitMap;
	private int rotation;
	private JSONArray arr;
}
