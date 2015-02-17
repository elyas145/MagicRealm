package model.board.clearing;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.board.tile.HexTile;
import model.counter.chit.Chit;
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
		internalConnections = new HashMap<Clearing, char[]>();
		externalConnections = new HashMap<HexTileInterface, ClearingResolver[]>();
		number = num;
	}

	@Override
	public void connectTo(Clearing other, boolean ench, boolean hid) {
		if (!internalConnections.containsKey(other)) {
			char[] conns = new char[2];
			conns[0] = conns[1] = 0;
			internalConnections.put(other, conns);
		}
		internalConnections.get(other)[ench ? 1 : 0] = hid ? 'h' : 'n';
	}

	@Override
	public void connectTo(HexTileInterface other, int entr, boolean ench) {
		if (other == getParent()) {
			connectTo(other.getEntryClearing(entr, ench), ench, false);
		} else {
			if(!externalConnections.containsKey(other)) {
				externalConnections.put(other, new ClearingResolver[2]);
			}
			ClearingResolver[] resv = externalConnections.get(other);
			resv[ench ? 1 : 0] = new ClearingResolver(other, entr);
		}
	}

	@Override
	public boolean isConnectedTo(Clearing other) {
		char[] res = internalConnections.get(other);
		int ench = isEnchanted() ? 1 : 0;
		switch (res[ench]) {
		case 'h':
		case 'n':
			return true;
		}
		ClearingResolver[] reslvrs = externalConnections.get(other.getParent());
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
	public void getTilePosition(boolean enchanted, FloatBuffer dest) {
		if (enchanted) {
			dest.put(0, location_e.getX());
			dest.put(1, location_e.getY());
		} else {
			dest.put(0, location.getX());
			dest.put(1, location.getY());
		}
	}
	
	@Override
	public Clearing getRandomConnection() {
		List<Clearing> possible = new ArrayList<Clearing>();
		for(Map.Entry<Clearing, char[]> ent: internalConnections.entrySet()) {
			if(ent.getValue()[0] != 0) {
				possible.add(ent.getKey());
			}
		}
		for(ClearingResolver[] cr: externalConnections.values()) {
			if(cr[0] != null) {
				possible.add(cr[0].getClearing());
			}
		}
		System.out.println(externalConnections);
		return Random.choose(possible);
	}

	public boolean isEnchanted() {
		return getParent().isEnchanted();
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

	private Map<Clearing, char[]> internalConnections;
	private Map<HexTileInterface, ClearingResolver[]> externalConnections;

	private List<Chit> chits;
	private int number;

	private Point location;
	private Point location_e;
}
