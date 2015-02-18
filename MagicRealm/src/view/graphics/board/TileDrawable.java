package view.graphics.board;

import view.graphics.Drawable;
import model.enums.TileName;

public abstract class TileDrawable implements Drawable {

	public TileName getTileName() {
		return type;
	}

	protected TileDrawable(TileName tt) {
		type = tt;
	}

	private TileName type;

}
