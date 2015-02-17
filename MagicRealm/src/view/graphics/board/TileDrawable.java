package view.graphics.board;

import model.enums.TileName;

public abstract class TileDrawable extends BoardItemDrawable {

	public TileName getTileName() {
		return type;
	}

	protected TileDrawable(BoardDrawable bd, TileName tt) {
		super(bd);
		type = tt;
	}

	private TileName type;

}
