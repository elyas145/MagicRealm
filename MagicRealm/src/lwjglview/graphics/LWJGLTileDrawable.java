package lwjglview.graphics;

import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.board.HexTile;
import view.graphics.Graphics;
import view.graphics.TileDrawable;

public class LWJGLTileDrawable extends TileDrawable {

	public LWJGLTileDrawable(HexTile ht, int norm, int enchant) {
		super(ht);
		normalTex = norm;
		enchantedTex = enchant;
	}
	
	public void setTextures(int norm, int ench) {
		normalTex = norm;
		enchantedTex = ench;
	}

	@Override
	public void draw(Graphics gfx) {
		draw((LWJGLGraphics) gfx);
	}
	
	public void draw(LWJGLGraphics lwgfx) {
		HexTile tile = getTile();
		int row = tile.getBoardRow();
		int col = tile.getBoardColumn();
		float x, y;
		x = col % 2 == 0 ? 1.5f : 0f;
		x += row * 3f;
		y = -col * 0.866025f;
		GLShaderHandler sh = lwgfx.getShaders();
		sh.setUniformIntValue(ShaderType.TILE_SHADER, "index", normalTex);
		lwgfx.resetModel();
		lwgfx.translateModel(x, y, 0f);
		lwgfx.translateModel(-3f, 3f, -0.5f);
		lwgfx.getShaders().setUniformIntValue(ShaderType.TILE_SHADER, "index", normalTex);
		lwgfx.getPrimitiveTool().drawHexagon();
	}
	
	private int normalTex;
	private int enchantedTex;

}
