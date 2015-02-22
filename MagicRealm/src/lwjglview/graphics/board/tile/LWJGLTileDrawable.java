package lwjglview.graphics.board.tile;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;

import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLDrawable;
import lwjglview.graphics.LWJGLDrawableLeaf;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.animator.AnimationQueue;
import lwjglview.graphics.animator.FillerAnimator;
import lwjglview.graphics.animator.TimedAnimator;
import lwjglview.graphics.board.tile.clearing.LWJGLClearingStorage;
import lwjglview.graphics.shader.ShaderType;
import model.EnchantedHolder;
import model.enums.CounterType;
import model.interfaces.ClearingInterface;
import utils.math.Mathf;
import utils.math.Matrix;

public class LWJGLTileDrawable extends LWJGLDrawableNode {

	public static final ShaderType SHADER = ShaderType.TILE_SHADER;

	public LWJGLTileDrawable(LWJGLTileCollection parent, float x, float y,
			float rot, int norm, int enchant,
			Iterable<? extends ClearingInterface> clears) {
		super(parent);
		tiles = parent;
		xPosition = x;
		yPosition = y;
		vector = Matrix.columnVector(xPosition, yPosition, 0f);
		translation = Matrix.translation(vector);
		rotation = Matrix.rotationZ(4, -rot);
		transformation = translation.multiply(rotation);
		textureLocation = new EnchantedHolder<Integer>(norm, enchant);
		System.out.println(textureLocation);
		enchanted = false;
		showEnchanted = enchanted;
		faces = new ArrayList<LWJGLDrawableLeaf>();
		bufferN = BufferUtils.createFloatBuffer(2);
		bufferE = BufferUtils.createFloatBuffer(2);
		bufferT = BufferUtils.createFloatBuffer(2);
		initFaces();
		flipper = new AnimationQueue();
		flipper.start();
		flipper.push(new FillerAnimator(Matrix.identityMatrix(4)));
		clearings = new HashMap<Integer, LWJGLClearingStorage>();
		initClearings(clears);
	}

	public void setTextures(int norm, int ench) {
		textureLocation.set(false, norm);
		textureLocation.set(true, ench);
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

	public void relocateChit(CounterType ct, float f, float g) {
		tiles.relocateChit(ct, f, g);
	}

	public LWJGLClearingStorage getClearing(int clr) {
		return clearings.get(clr);
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

		for (LWJGLDrawable tf : faces) {
			tf.draw(lwgfx);
		}

	}

	private void initClearings(Iterable<? extends ClearingInterface> clears) {
		getPosition(bufferT);
		clearings.put(0, new LWJGLClearingStorage(this, bufferT));
		for (ClearingInterface clr : clears) {
			clr.getPosition(false, bufferN);
			clr.getPosition(true, bufferE);
			clearings.put(clr.getClearingNumber(), new LWJGLClearingStorage(
					this, bufferT, bufferN, bufferE));
		}
	}

	private FloatBuffer bufferN, bufferE, bufferT;

	private void initFaces() {
		float halfThick = GraphicsConfiguration.TILE_THICKNESS * .5f;
		Matrix mat = Matrix.translation(0f, 0f, halfThick);
		faces.add(new LWJGLDrawableLeaf(this, new HexTileFace(mat, false)));
		mat = Matrix.rotationX(4, Mathf.PI).multiply(mat);
		faces.add(new LWJGLDrawableLeaf(this, new HexTileFace(mat, true)));
		// scale square
		mat = Matrix.dilation(.5f, halfThick, 1f, 1f);
		// rotate rectangle
		mat = Matrix.rotationX(4, Mathf.PI * .5f).multiply(mat);
		// move rectange to top
		mat = Matrix.translation(0f, 0.866025f, 0f).multiply(mat);
		for (int i = 0; i < 6; ++i) {
			Matrix tmp = Matrix.rotationZ(4, i * Mathf.PI / 3f).multiply(mat);
			faces.add(new LWJGLDrawableLeaf(this, new SideFace(tmp)));
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
		
		public abstract int getIndex();

		public TileFace(Matrix mat) {
			transform = mat;
		}

		@Override
		public void updateUniforms(LWJGLGraphics lwgfx) {
			lwgfx.updateModelViewUniform(SHADER, "modelViewMatrix");
			lwgfx.updateMVPUniform(SHADER, "mvpMatrix");
			lwgfx.getShaders().setUniformIntValue("index", getIndex());
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
			tex = textureLocation.get(ench);
		}

		@Override
		public int getIndex() {
			return tex;
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
		public int getIndex() {
			return -1;
		}

		@Override
		public void draw(LWJGLGraphics lwgfx) {
			lwgfx.getPrimitiveTool().drawSquare();
		}

	}

	private Map<Integer, LWJGLClearingStorage> clearings;
	private LWJGLTileCollection tiles;
	private Collection<LWJGLDrawableLeaf> faces;
	private Matrix transformation;
	private Matrix rotation;
	private float xPosition;
	private float yPosition;
	private Matrix vector;
	private Matrix translation;
	private EnchantedHolder<Integer> textureLocation;
	private boolean enchanted;
	private boolean showEnchanted;
	private AnimationQueue flipper;

}
