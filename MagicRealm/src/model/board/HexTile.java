package model.board;

import java.awt.List;

import model.enums.TileType;
import model.interfaces.HexTileInterface;

public class HexTile implements HexTileInterface {

	public HexTile(TileType tp, int rw, int col, int rot) {
		type = tp;
		row = rw;
		column = col;
		clearings = setClearings();
		rotation = rot;
	}

	public List getClearings() {
		return clearings;
	}

	public void setClearings(List clearings) {
		this.clearings = clearings;
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
	private List clearings;
	private int rotation;

}
