package lwjglview.graphics;

import config.GraphicsConfiguration;
import lwjglview.graphics.animator.AnimationQueue;
import lwjglview.graphics.animator.TimedAnimator;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.enums.TileType;
import utils.math.Mathf;
import utils.math.Matrix;
import view.graphics.Graphics;
import view.graphics.board.TileDrawable;

public class LWJGLTileDrawable extends TileDrawable {

	public static final ShaderType SHADER = ShaderType.TILE_SHADER;

	public LWJGLTileDrawable(LWJGLBoardDrawable bd, TileType tt, float x,
			float y, float rot, int norm, int enchant) {
		super(bd, tt);
		xPosition = x;
		yPosition = y;
		rotation = rot;
		normalTex = norm;
		enchantedTex = enchant;
		enchanted = false;
		showEnchanted = enchanted;
		flipper = new AnimationQueue();
		flipper.start();
	}

	public void setTextures(int norm, int ench) {
		normalTex = norm;
		enchantedTex = ench;
	}

	public void setEnchanted(boolean ench) {
		if (ench ^ enchanted) {
			enchanted = ench;
			TileFlipper tf = new TileFlipper(1f, enchanted);
			flipper.push(tf);
		}
	}

	public void swap() {
		setEnchanted(!enchanted);
	}

	@Override
	public void draw(Graphics gfx) {
		draw((LWJGLGraphics) gfx);
	}

	private void setShowEnchanted(boolean set) {
		showEnchanted = set;
	}

	private void draw(LWJGLGraphics lwgfx) {

		Matrix mat;
		if (flipper.isFinished()) {
			mat = Matrix.identityMatrix(4);
		} else {
			mat = flipper.apply();
		}

		GLShaderHandler sh = lwgfx.getShaders();
		sh.setUniformIntValue(SHADER, "index", showEnchanted ? enchantedTex
				: normalTex);

		float halfThick = GraphicsConfiguration.TILE_THICKNESS * .5f;

		lwgfx.resetModelMatrix();
		lwgfx.rotateModelZ(-rotation);
		lwgfx.translateModel(0f, 0f, halfThick);

		lwgfx.applyModelTransform(mat);

		lwgfx.translateModel(xPosition, yPosition, 0f);

		lwgfx.updateModelViewUniform(SHADER, "modelViewMatrix");
		lwgfx.updateMVPUniform(SHADER, "mvpMatrix");
		lwgfx.getPrimitiveTool().drawHexagon();

		sh.setUniformIntValue(SHADER, "index", showEnchanted ? normalTex
				: enchantedTex);

		lwgfx.resetModelMatrix();
		lwgfx.rotateModelX(Mathf.PI);
		lwgfx.rotateModelZ(rotation);
		lwgfx.translateModel(0f, 0f, -halfThick);

		lwgfx.applyModelTransform(mat);

		lwgfx.translateModel(xPosition, yPosition, 0f);

		lwgfx.updateModelViewUniform(SHADER, "modelViewMatrix");
		lwgfx.updateMVPUniform(SHADER, "mvpMatrix");
		lwgfx.getPrimitiveTool().drawHexagon();

		// draw sides of tile
		sh.setUniformIntValue(SHADER, "index", -1);

		// scale square
		Matrix transform = Matrix
				.dilation(new float[] { .5f, halfThick, 1f, 1f });
		// rotate rectangle
		transform = Matrix.rotationX(4, Mathf.PI * .5f).multiply(transform);
		// move rectange to top
		transform = Matrix.translation(new float[] { 0f, 0.866025f, 0f })
				.multiply(transform);
		for (int i = 0; i < 6; ++i) {
			lwgfx.resetModelMatrix();
			lwgfx.applyModelTransform(transform);
			lwgfx.rotateModelZ(i * Mathf.PI / 3f);

			lwgfx.applyModelTransform(mat);

			lwgfx.translateModel(xPosition, yPosition, 0f);

			lwgfx.updateModelViewUniform(SHADER, "modelViewMatrix");
			lwgfx.updateMVPUniform(SHADER, "mvpMatrix");

			lwgfx.getPrimitiveTool().drawSquare();
		}
	}

	private class TileFlipper extends TimedAnimator {

		public TileFlipper(float time, boolean flip) {
			super(time);
			whenDone = flip;
		}

		@Override
		protected Matrix calculateTransform() {
			float i = getInterval();
			return Matrix.rotationX(4, i * Mathf.PI);
		}

		@Override
		public void finish() {
			setShowEnchanted(whenDone);
		}

		private boolean whenDone;

	}

	private float xPosition;
	private float yPosition;
	private float rotation;
	private int normalTex;
	private int enchantedTex;
	private boolean enchanted;
	private boolean showEnchanted;
	private AnimationQueue flipper;

}
