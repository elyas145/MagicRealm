package model.board;

import java.awt.List;

import model.board.enums.TileType;

public class HexTile {

	private TileType type;
	private int row;
	private int column;
	private List clearings;

	public List getClearings() {
		return clearings;
	}

	public void setClearings(List clearings) {
		this.clearings = clearings;
	}

	public HexTile(TileType tp, int rw, int col) {
		type = tp;
		row = rw;
		column = col;
		clearings = setClearings();
	}

	private List setClearings() {
		// TODO get clearing location from external file, and create the
		// clearings that go with this tile.
		return null;
	}

	public int getBoardRow() {
		return row;
	}

	public int getBoardColumn() {
		return column;
	}

	public TileType getType() {
		return type;
	}

}
