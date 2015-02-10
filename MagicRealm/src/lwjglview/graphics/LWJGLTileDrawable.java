package lwjglview.graphics;

import model.board.HexTile;
import view.graphics.Graphics;
import view.graphics.TileDrawable;

public class LWJGLTileDrawable extends TileDrawable {

	public LWJGLTileDrawable(HexTile ht, int norm, int enchant) {
		super(ht);
		normalTex = norm;
		enchantedTex = enchant;
	}

	@Override
	public void draw(Graphics gfx) {
		LWJGLGraphics lwgfx = (LWJGLGraphics) gfx;
		HexTile tile = getTile();
		int row = tile.getBoardRow();
		int col = tile.getBoardColumn();
		float x, y;
		x = col % 2 == 0 ? 1.5f : 0f;
		x += row * 3f;
		y = -col * 0.866025f;
		lwgfx.resetModel();
		lwgfx.translateModel(x, y, -1f);
		lwgfx.getPrimitiveTool().drawHexagon();
	}
	
	private int normalTex;
	private int enchantedTex;

}
