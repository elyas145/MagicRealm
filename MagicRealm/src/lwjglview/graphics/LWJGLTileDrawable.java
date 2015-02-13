package lwjglview.graphics;

import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.board.HexTile;
import model.board.enums.TileType;
import model.board.interfaces.HexTileInterface;
import utils.math.Mathf;
import utils.math.Matrix;
import utils.time.Timing;
import view.graphics.Graphics;
import view.graphics.TileDrawable;

public class LWJGLTileDrawable extends TileDrawable {

	public LWJGLTileDrawable(HexTileInterface ht, int norm, int enchant) {
		super(ht.getType(), ht.getBoardRow(), ht.getBoardColumn(), ht.getRotation());
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
		int row = getTileRow();
		int col = getTileColumn();
		float x, y;
		x = row % 2 == 0 ? 0f : 1.5f;
		x += col * 3f;
		y = -row * 0.866025f;
		GLShaderHandler sh = lwgfx.getShaders();
		sh.setUniformIntValue(ShaderType.TILE_SHADER, "index", normalTex);
		
		lwgfx.resetModelMatrix();
		lwgfx.resetViewMatrix();
		lwgfx.rotateModelZ(- Mathf.PI * getTileRotation() / 3f);
		lwgfx.translateModel(x, y, 0f);
		
		float time = Timing.getSeconds() * .6f;
		Matrix tmp = Matrix.rotationX(4, Mathf.PI / 4f);
		float k = (Mathf.sin(time * .6f) + 3f) / 3f;
		tmp = Matrix.translation(new float[] {
				0f, -4f * k, 3f * k
		}).multiply(tmp);
		tmp = Matrix.rotationZ(4, time * .3f).multiply(tmp);
		lwgfx.applyCameraTransform(tmp);
		lwgfx.translateCamera(3.5f, -3.5f, 0f);
		
		lwgfx.updateMVPUniform(ShaderType.TILE_SHADER, "mvpMatrix");
		lwgfx.getShaders().setUniformIntValue(ShaderType.TILE_SHADER, "index", normalTex);
		lwgfx.getPrimitiveTool().drawHexagon();
		
		System.out.println(System.nanoTime());
	}
	
	private int normalTex;
	private int enchantedTex;

}
