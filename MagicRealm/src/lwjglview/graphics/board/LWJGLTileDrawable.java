package lwjglview.graphics.board;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;

import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLDrawable;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.animator.AnimationQueue;
import lwjglview.graphics.animator.FillerAnimator;
import lwjglview.graphics.animator.StaticAnimator;
import lwjglview.graphics.animator.TimedAnimator;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.enums.TileName;
import utils.math.Mathf;
import utils.math.Matrix;
import view.graphics.Graphics;
import view.graphics.board.TileDrawable;

public class LWJGLTileDrawable extends LWJGLDrawableNode {

	public static final ShaderType SHADER = ShaderType.TILE_SHADER;

	public LWJGLTileDrawable(LWJGLDrawable parent, float x, float y, float rot, int norm, int enchant) {
		super(parent);
		xPosition = x;
		yPosition = y;
		vector = Matrix.columnVector(xPosition, yPosition, 0f);
		translation = Matrix.translation(vector);
		rotation = Matrix.rotationZ(4, -rot);
		transformation = translation.multiply(rotation);
		normalTex = norm;
		enchantedTex = enchant;
		enchanted = false;
		showEnchanted = enchanted;
		faces = new ArrayList<TileFace>();
		initFaces();
		flipper = new AnimationQueue();
		flipper.start();
		flipper.push(new FillerAnimator(Matrix.identityMatrix(4)));
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

	public void getPosition(FloatBuffer position) {
		position.put(0, xPosition);
		position.put(1, yPosition);
	}

	public boolean isEnchanted() {
		return enchanted;
	}

	public Matrix getVector() {
		return vector;
	}

	@Override
	public void applyNodeTransformation(LWJGLGraphics lwgfx) {
		Matrix mat = flipper.apply();
		transformation = translation.multiply(mat).multiply(rotation);
		lwgfx.applyModelTransform(transformation);
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics lwgfx) {
	}

	@Override
	public void draw(LWJGLGraphics lwgfx) {

		for (TileFace tf : faces) {
			drawChild(lwgfx, tf);
		}

	}

	private void initFaces() {
		float halfThick = GraphicsConfiguration.TILE_THICKNESS * .5f;
		Matrix mat = Matrix.translation(0f, 0f, halfThick);
		faces.add(new HexTileFace(mat, false));
		mat = Matrix.rotationX(4, Mathf.PI).multiply(mat);
		faces.add(new HexTileFace(mat, true));
		// scale square
		mat = Matrix.dilation(.5f, halfThick, 1f, 1f);
		// rotate rectangle
		mat = Matrix.rotationX(4, Mathf.PI * .5f).multiply(mat);
		// move rectange to top
		mat = Matrix.translation(0f, 0.866025f, 0f).multiply(mat);
		for (int i = 0; i < 6; ++i) {
			Matrix tmp = Matrix.rotationZ(4, i * Mathf.PI / 3f).multiply(mat);
			faces.add(new SideFace(tmp));
		}
	}

	private void setShowEnchanted(boolean set) {
		showEnchanted = set;
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

	private abstract class TileFace extends LWJGLDrawable {

		public TileFace(Matrix mat) {
			transform = mat;
		}

		@Override
		public void updateUniforms(LWJGLGraphics lwgfx) {
			lwgfx.updateModelViewUniform(SHADER, "modelViewMatrix");
			lwgfx.updateMVPUniform(SHADER, "mvpMatrix");
		}

		@Override
		public void applyTransformation(LWJGLGraphics gfx) {
			gfx.resetModelMatrix();
			gfx.applyModelTransform(transform);
		}

		private Matrix transform;

	}

	private class HexTileFace extends TileFace {

		public HexTileFace(Matrix mat, boolean ench) {
			super(mat);
			tex = ench ? enchantedTex : normalTex;
		}

		@Override
		public void updateUniforms(LWJGLGraphics lwgfx) {
			super.updateUniforms(lwgfx);
			lwgfx.getShaders().setUniformIntValue(SHADER, "index", tex);
		}

		@Override
		public void draw(LWJGLGraphics lwgfx) {
			lwgfx.getPrimitiveTool().drawHexagon();
		}

		private int tex;

	}

	private class SideFace extends TileFace {

		public SideFace(Matrix mat) {
			super(mat);
		}

		@Override
		public void updateUniforms(LWJGLGraphics lwgfx) {
			super.updateUniforms(lwgfx);
			lwgfx.getShaders().setUniformIntValue(SHADER, "index", -1);
		}

		@Override
		public void draw(LWJGLGraphics lwgfx) {
			lwgfx.getPrimitiveTool().drawSquare();
		}

	}

	private Collection<TileFace> faces;
	private Matrix transformation;
	private Matrix rotation;
	private float xPosition;
	private float yPosition;
	private Matrix vector;
	private Matrix translation;
	private int normalTex;
	private int enchantedTex;
	private boolean enchanted;
	private boolean showEnchanted;
	private AnimationQueue flipper;

}
