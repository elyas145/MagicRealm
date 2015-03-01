package model.controller;

import utils.math.linear.Matrix;
import model.EnchantedHolder;
import model.enums.LandType;
import model.enums.PathType;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;

public class ClearingData implements ClearingInterface {
	private static final long serialVersionUID = -7775017845933729130L;

	public ClearingData(ClearingInterface other, HexTileData parent) {
		positions = new EnchantedHolder<Matrix>(Matrix.zeroVector(3),
				Matrix.zeroVector(3));
		other.getPosition(false, positions.get(false));
		other.getPosition(true, positions.get(true));
		number = other.getClearingNumber();
		this.parent = parent;
		
	}

	@Override
	public int getClearingNumber() {
		return number;
	}

	@Override
	public void getPosition(boolean enchanted, Matrix dest) {
		dest.copyFrom(positions.get(enchanted));
	}

	@Override
	public HexTileInterface getParentTile() {
		return parent;
	}

	@Override
	public void connectTo(ClearingInterface other, boolean ench, PathType pt) {

	}

	@Override
	public void connectTo(HexTileInterface other, int entr, boolean ench) {

	}

	@Override
	public boolean isConnectedTo(ClearingInterface other) {
		return false;
	}

	@Override
	public boolean isConnectedTo(ClearingInterface other, PathType pt) {
		return false;
	}

	@Override
	public Iterable<ClearingInterface> getSurrounding() {
		return null;
	}

	@Override
	public Iterable<ClearingInterface> getSurrounding(PathType pt) {
		return null;
	}

	@Override
	public boolean isEnchanted() {
		return parent.isEnchanted();
	}

	private EnchantedHolder<Matrix> positions;
	private LandType type;
	private HexTileData parent;
	private int number;
}
