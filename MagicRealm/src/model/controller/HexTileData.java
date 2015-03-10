package model.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import communication.handler.server.SerializedTile;

import model.board.clearing.Clearing;
import model.enums.TileName;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;

public class HexTileData implements HexTileInterface {
	private static final long serialVersionUID = -8029897396449586795L;
	public HexTileData(HexTileInterface other){
		clearings = new HashMap<Integer, ClearingData>();
		this.col = other.getBoardColumn();
		this.row = other.getBoardRow();
		this.rot = other.getRotation();
		this.name = other.getName();
		for(ClearingInterface c : other.getClearings()){
			clearings.put(c.getClearingNumber(), new ClearingData(c, this));
		}
	}
	@Override
	public TileName getName() {
		return name;
	}

	@Override
	public boolean isEnchanted() {
		return enchanted;
	}

	@Override
	public int getBoardColumn() {
		return col;
	}

	@Override
	public int getBoardRow() {
		return row;
	}

	@Override
	public int getRotation() {
		return rot;
	}

	@Override
	public Collection<? extends ClearingInterface> getClearings() {
		return clearings.values();
	}

	@Override
	public ClearingInterface getClearing(int clearingNum) {
		return clearings.get(clearingNum);
	}

	@Override
	public Clearing getEntryClearing(int rot) {
		return null;
	}

	@Override
	public Clearing getEntryClearing(int rot, boolean enchant) {
		return null;
	}

	@Override
	public void connectTo(TileName tile, int exit) {

	}

	@Override
	public Iterable<TileName> getSurrounding() {
		return null;
	}
	@Override
	public SerializedTile getSerializedTile() {
		return null;
	}
	private Map<Integer, ClearingData> clearings;
	private TileName name;
	private int row;
	private int col;
	private int rot;
	private boolean enchanted;

}
