package lwjglview.graphics;

import config.GraphicsConfiguration;
import lwjglview.graphics.animator.Animator;
import lwjglview.graphics.animator.TimedAnimator;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.interfaces.HexTileInterface;
import utils.math.Mathf;
import utils.math.Matrix;
import view.graphics.Graphics;
import view.graphics.TileDrawable;

public class LWJGLTileDrawable extends TileDrawable {

	public static final ShaderType SHADER = ShaderType.TILE_SHADER;

	public LWJGLTileDrawable(HexTileInterface ht, int norm, int enchant) {
		super(ht.getType(), ht.getBoardRow(), ht.getBoardColumn(), ht
				.getRotation());
		normalTex = norm;
		enchantedTex = enchant;
		enchanted = false;
		flipper = null;
	}

	public void setTextures(int norm, int ench) {
		normalTex = norm;
		enchantedTex = ench;
	}

	public void setEnchanted(boolean ench) {
		if (ench && !enchanted || !ench && enchanted) {
			flipper = new TileFlipper(1f);
			flipper.start();
		}
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

		Matrix mat;
		if (flipper == null) {
			mat = Matrix.identityMatrix(4);
		} else if (flipper.isFinished()) {
			swap();
			mat = Matrix.identityMatrix(4);
			flipper = null;
		} else {
			mat = flipper.apply();
		}

		GLShaderHandler sh = lwgfx.getShaders();
		sh.setUniformIntValue(SHADER, "index", enchanted ? enchantedTex
				: normalTex);

		float halfThick = GraphicsConfiguration.TILE_THICKNESS * .5f;

		lwgfx.resetModelMatrix();
		lwgfx.rotateModelZ(-Mathf.PI * getTileRotation() / 3f);
		lwgfx.translateModel(0f, 0f, halfThick);

		lwgfx.applyModelTransform(mat);

		lwgfx.translateModel(x, y, 0f);

		lwgfx.updateModelViewUniform(SHADER, "modelViewMatrix");
		lwgfx.updateMVPUniform(SHADER, "mvpMatrix");
		lwgfx.getPrimitiveTool().drawHexagon();

		sh.setUniformIntValue(SHADER, "index", enchanted ? normalTex
				: enchantedTex);

		lwgfx.resetModelMatrix();
		lwgfx.rotateModelX(Mathf.PI);
		lwgfx.rotateModelZ(Mathf.PI * getTileRotation() / 3f);
		lwgfx.translateModel(0f, 0f, -halfThick);

		lwgfx.applyModelTransform(mat);

		lwgfx.translateModel(x, y, 0f);

		lwgfx.updateModelViewUniform(SHADER, "modelViewMatrix");
		lwgfx.updateMVPUniform(SHADER, "mvpMatrix");
		lwgfx.getPrimitiveTool().drawHexagon();
	}

	private class TileFlipper extends TimedAnimator {

		public TileFlipper(float time) {
			super(time);
		}

		@Override
		public Matrix apply() {
			float i = getInterval();
			System.out.println(i);
			return Matrix.rotationX(4, i * Mathf.PI);
		}

	}

	private int normalTex;
	private int enchantedTex;
	private boolean enchanted;
	private Animator flipper;

}
