package model.board.clearing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.EnchantedHolder;
import model.board.tile.HexTile;
import model.counter.chit.Chit;
import model.enums.PathType;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;
import utils.math.linear.Matrix;
import utils.tools.Function;
import utils.tools.IterationTools;

public class Clearing implements ClearingInterface {

	// nloc and eloc distance from center of tile
	public Clearing(HexTile par, int num, Matrix nloc, Matrix eloc) {
		parent = par;
		locations = new EnchantedHolder<Matrix>(Matrix.clone(nloc), Matrix.clone(eloc));
		internalConnections = new HashMap<ClearingInterface, EnchantedHolder<PathType>>();
		externalConnections = new HashMap<HexTileInterface, EnchantedHolder<ClearingResolver>>();
		number = num;
	}

	@Override
	public HexTileInterface getParentTile() {
		return getParent();
	}

	@Override
	public void connectTo(ClearingInterface other, boolean ench, PathType pt) {
		if (!internalConnections.containsKey(other)) {
			internalConnections.put(other, new EnchantedHolder<PathType>());
		}
		internalConnections.get(other).set(ench, pt);
	}

	@Override
	public void connectTo(HexTileInterface other, int entr, boolean ench) {
		if (other == getParent()) {
			connectTo(other.getEntryClearing(entr, ench), ench, PathType.NORMAL);
		} else {
			if (!externalConnections.containsKey(other)) {
				externalConnections.put(other, new EnchantedHolder<ClearingResolver>());
			}
			externalConnections.get(other).set(ench, new ClearingResolver(other, entr));
		}
	}

	@Override
	public boolean isConnectedTo(ClearingInterface other) {
		for (PathType pt : PathType.values()) {
			if (isConnectedTo(other, pt)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isConnectedTo(ClearingInterface other, PathType pt) {
		EnchantedHolder<PathType> res = internalConnections.get(other);
		boolean ench = isEnchanted();
		if (res != null && res.has(ench)) {
			if (res.get(ench) == pt) {
				return true;
			}
		}
		if (pt != PathType.NORMAL) {
			return false;
		}
		EnchantedHolder<ClearingResolver> rsv = externalConnections.get(other
				.getParentTile());
		if (rsv != null && rsv.has(ench)) {
			if (rsv.get(ench).getClearing() == other) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterable<ClearingInterface> getSurrounding() {
		ArrayList<ClearingInterface> surround = new ArrayList<ClearingInterface>();
		boolean ench = isEnchanted();
		for (Map.Entry<ClearingInterface, EnchantedHolder<PathType>> entr : internalConnections
				.entrySet()) {
			ClearingInterface clr = entr.getKey();
			EnchantedHolder<PathType> val = entr.getValue();
			if (val.has(ench)) {
				surround.add(clr);
			}
		}
		for (EnchantedHolder<ClearingResolver> rslv : externalConnections.values()) {
			if (rslv.has(ench)) {
				surround.add(rslv.get(ench).getClearing());
			}
		}
		return surround;
	}

	@Override
	public Iterable<ClearingInterface> getSurrounding(PathType pt) {
		return IterationTools.filter(getSurrounding(), new FilterFunction(pt));
	}

	private class FilterFunction implements
			Function<ClearingInterface, Boolean> {

		public FilterFunction(PathType pt) {
			path = pt;
		}

		@Override
		public Boolean apply(ClearingInterface other) {
			return isConnectedTo(other, path);
		}

		private PathType path;

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
	public void getPosition(boolean enchanted, Matrix dest) {
		dest.copyFrom(locations.get(enchanted));
	}

	@Override
	public boolean isEnchanted() {
		return getParent().isEnchanted();
	}

	@Override
	public String toString() {
		return "Clearing: Tile: " + getParent().getName() + ", Number: "
				+ getNumber();
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

	private Map<ClearingInterface, EnchantedHolder<PathType>> internalConnections;
	private Map<HexTileInterface, EnchantedHolder<ClearingResolver>> externalConnections;

	private List<Chit> chits;
	private int number;

	private EnchantedHolder<Matrix> locations;
}
