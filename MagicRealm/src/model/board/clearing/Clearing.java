package model.board.clearing;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.board.tile.HexTile;
import model.counter.chit.Chit;
import model.enums.PathType;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;

import utils.math.Point;
import utils.random.Random;

public class Clearing implements ClearingInterface {

	// nloc and eloc distance from center of tile
	public Clearing(HexTile par, int num, Point nloc, Point eloc) {
		parent = par;
		location = nloc;
		location_e = eloc;
		internalConnections = new HashMap<ClearingInterface, PathType[]>();
		externalConnections = new HashMap<HexTileInterface, ClearingResolver[]>();
		number = num;
	}
	
	@Override
	public HexTileInterface getParentTile() {
		return getParent();
	}

	@Override
	public void connectTo(ClearingInterface other, boolean ench, PathType pt) {
		if (!internalConnections.containsKey(other)) {
			PathType[] conns = new PathType[2];
			internalConnections.put(other, conns);
		}
		internalConnections.get(other)[ench ? 1 : 0] = pt;
	}

	@Override
	public void connectTo(HexTileInterface other, int entr, boolean ench) {
		if (other == getParent()) {
			connectTo(other.getEntryClearing(entr, ench), ench, PathType.NORMAL);
		} else {
			if(!externalConnections.containsKey(other)) {
				externalConnections.put(other, new ClearingResolver[2]);
			}
			ClearingResolver[] resv = externalConnections.get(other);
			resv[ench ? 1 : 0] = new ClearingResolver(other, entr);
		}
	}

	@Override
	public boolean isConnectedTo(ClearingInterface other) {
		PathType[] res = internalConnections.get(other);
		int ench = isEnchanted() ? 1 : 0;
		if(res[ench] != null) {
			return true;
		}
		ClearingResolver[] reslvrs = externalConnections.get(other.getParentTile());
		ClearingResolver rsv = reslvrs[ench];
		if (rsv != null) {
			if (rsv.getClearing() == other) {
				return true;
			}
		}
		return false;
	}

	public Object getLocation_e() {
		return location_e;
	}

	public void setLocation_e(Point location_e) {
		this.location_e = location_e;
		// System.out.println("Set Location_e: " + location_e.toSring());
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
		// System.out.println("Set Location: " + location.toSring());
	}

	public List<Chit> getChits() {
		return chits;
	}

	public void setChits(List<Chit> chits) {
		this.chits = chits;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public int getClearingNumber() {
		return number;
	}

	@Override
	public void getPosition(boolean enchanted, FloatBuffer dest) {
		if (enchanted) {
			dest.put(0, location_e.getX());
			dest.put(1, location_e.getY());
		} else {
			dest.put(0, location.getX());
			dest.put(1, location.getY());
		}
	}
	
	@Override
	public ClearingInterface getRandomConnection() {
		List<ClearingInterface> possible = new ArrayList<ClearingInterface>();
		for(Map.Entry<ClearingInterface, PathType[]> ent: internalConnections.entrySet()) {
			if(ent.getValue()[0] != null) {
				possible.add(ent.getKey());
			}
		}
		for(ClearingResolver[] cr: externalConnections.values()) {
			if(cr[0] != null) {
				possible.add(cr[0].getClearing());
			}
		}
		return Random.choose(possible);
	}

	public boolean isEnchanted() {
		return getParent().isEnchanted();
	}
	
	@Override
	public String toString() {
		return "Clearing: Tile: " + getParent().getName() + ", Number: " + getNumber();
	}

	protected HexTileInterface getParent() {
		return parent;
	}

	private class ClearingResolver {
		public ClearingResolver(HexTileInterface other, int loc) {
			tile = other;
			entrance = loc % 6;
		}

		public Clearing getClearing() {
			return tile.getEntryClearing(entrance);
		}

		private HexTileInterface tile;
		private int entrance;
	}

	private HexTileInterface parent;

	private Map<ClearingInterface, PathType[]> internalConnections;
	private Map<HexTileInterface, ClearingResolver[]> externalConnections;

	private List<Chit> chits;
	private int number;

	private Point location;
	private Point location_e;
}
