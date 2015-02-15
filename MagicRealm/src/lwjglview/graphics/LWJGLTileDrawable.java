package lwjglview.graphics;

import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.interfaces.HexTileInterface;
import utils.math.Mathf;
import view.graphics.Graphics;
import view.graphics.TileDrawable;

public class LWJGLTileDrawable extends TileDrawable {
	
	public static final ShaderType SHADER = ShaderType.TILE_SHADER;

	public LWJGLTileDrawable(HexTileInterface ht, int norm, int enchant) {
		super(ht.getType(), ht.getBoardRow(), ht.getBoardColumn(), ht.getRotation());
		normalTex = norm;
		enchantedTex = enchant;
		enchanted = false;
	}
	
	public void setTextures(int norm, int ench) {
		normalTex = norm;
		enchantedTex = ench;
	}
	
	public void setEnchanted(boolean ench) {
		enchanted = ench;
	}
	
	public void swap() {
		enchanted ^= true;
	}

	@Override
	public void draw(Graphics gfx) {
		draw((LWJGLGraphics) gfx);
	}
	
	private void draw(LWJGLGraphics lwgfx) {
		int row = getTileRow();
		int col = getTileColumn();
		float x, y;
		x = row % 2 == 0 ? 0f : 1.5f;
		x += col * 3f;
		y = -row * 0.866025f;
		GLShaderHandler sh = lwgfx.getShaders();
		sh.setUniformIntValue(SHADER, "index", enchanted ? enchantedTex : normalTex);
		
		lwgfx.resetModelMatrix();
		lwgfx.rotateModelZ(- Mathf.PI * getTileRotation() / 3f);
		lwgfx.translateModel(x, y, 0f);
		
		lwgfx.updateModelViewUniform(SHADER, "modelViewMatrix");
		lwgfx.updateMVPUniform(SHADER, "mvpMatrix");
		lwgfx.getPrimitiveTool().drawHexagon();
		
	}
	
	private int normalTex;
	private int enchantedTex;
	private boolean enchanted;

}
