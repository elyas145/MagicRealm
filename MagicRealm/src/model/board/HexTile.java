package model.board;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;

import model.enums.TileType;
import model.interfaces.HexTileInterface;

import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import config.BoardConfiguration;
import utils.math.Point;
import utils.resources.ResourceHandler;

public class HexTile implements HexTileInterface {

	public HexTile(TileType tp, int rw, int col, int rot) {
		type = tp;
		row = rw;
		column = col;
		rotation = rot;
	}

	public HexTile(TileType tile, int x, int y, int rot, JSONArray arr) {
		type = tile;
		row = x;
		column = y;
		rotation = rot;
		this.arr = arr;
		setClearings();
	}

	public ArrayList<Clearing> getClearings() {
		return clearings;
	}

	public void setClearings(ArrayList<Clearing> clearings) {
		this.clearings = clearings;
	}

	private void setClearings() {
		// TODO get clearing location from external file, and create the
		// clearings that go with this tile.
		for (int i = 0; i < arr.size(); i++) {
			JSONObject obj = (JSONObject) arr.get(i);
			String name = (String) obj.get("tileName");
			if (name.equals(type.name())) {
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
	public TileType getType() {
		return type;
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

	private TileType type;
	private int row;
	private int column;
	private ArrayList<Clearing> clearings = new ArrayList<Clearing>();
	private int rotation;
	private JSONArray arr;
}
