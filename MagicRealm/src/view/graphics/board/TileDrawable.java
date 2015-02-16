package view.graphics.board;

import model.enums.TileType;

public abstract class TileDrawable extends BoardItemDrawable {

	public TileType getTileType() {
		return type;
	}

	protected TileDrawable(BoardDrawable bd, TileType tt) {
		super(bd);
		type = tt;
	}

	private TileType type;

}
