package view.graphics;

import model.board.HexTile;
import model.board.TileType;


public abstract class TileDrawable implements Drawable {
	
	public HexTile getTile() {
		return tile;
	}
	
	public TileType getTileType() {
		return tile.getType();
	}
	
	protected TileDrawable(HexTile ht) {
		tile = ht;
	}
	
	private HexTile tile;
	
}
