package view.graphics;

import model.enums.TileType;

public abstract class TileDrawable implements Drawable {

	public TileType getTileType() {
		return type;
	}

	public int getTileRotation() {
		return rotation;
	}

	public int getTileRow() {
		return y;
	}

	public int getTileColumn() {
		return x;
	}

	protected TileDrawable(TileType tt, int j, int i, int rot) {
		x = j;
		y = i;
		type = tt;
		rotation = rot;
	}

	private int x;
	private int y;
	private TileType type;
	private int rotation;

}
