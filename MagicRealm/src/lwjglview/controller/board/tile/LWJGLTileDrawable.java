package lwjglview.controller.board.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import config.BoardConfiguration;
import config.GraphicsConfiguration;
import lwjglview.graphics.GLPrimitives;
import lwjglview.graphics.LWJGLDrawable;
import lwjglview.graphics.LWJGLDrawableLeaf;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.animator.AnimationQueue;
import lwjglview.graphics.animator.FillerAnimator;
import lwjglview.graphics.animator.TimedAnimator;
import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import lwjglview.controller.board.tile.clearing.LWJGLClearingStorage;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import lwjglview.selection.SelectionFrame;
import model.EnchantedHolder;
import model.interfaces.ClearingInterface;
import utils.math.Mathf;
import utils.math.linear.Matrix;

public class LWJGLTileDrawable extends LWJGLDrawableNode implements
		MatrixCalculator {

	public LWJGLTileDrawable(LWJGLTileCollection parent, Matrix pos, float rot,
			int id, EnchantedHolder<LWJGLTextureLoader> textureLocs,
			EnchantedHolder<LWJGLTextureLoader> selectLocs,
			Iterable<? extends ClearingInterface> clears) {
		super(parent);
		tiles = parent;
		position = Matrix.clone(pos);
		translation = Matrix.translation(position);
		rotation = Matrix.rotationZ(4, -rot);
		reverseRotation = rotation.inverse();
		transformation = Matrix.clone(translation);
		textureLocation = textureLocs;
		selectionLocation = selectLocs;
		enchanted = false;
		faces = new ArrayList<LWJGLDrawableLeaf>();
		vec3N = Matrix.zeroVector(3);
		vec3E = Matrix.zeroVector(3);
		vec3T = Matrix.zeroVector(3);
		initFaces();
		flipper = new AnimationQueue();
		flipper.start();
		flipper.push(new FillerAnimator(Matrix.identity(4)));
		clearings = new HashMap<Integer, LWJGLClearingStorage>();
		initClearings(clears);
		setCalculator(this);
		identifier = id;
	}

	public void setTextures(LWJGLTextureLoader norm, LWJGLTextureLoader ench) {
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

	public void getPosition(Matrix pos) {
		pos.copyFrom(position);
	}

	public boolean isEnchanted() {
		return enchanted;
	}

	public Matrix getVector() {
		return position;
	}

	public void relocateChit(int id, Matrix pos) {
		tiles.relocateChit(id, pos);
	}

	public LWJGLCounterStorage getClearing(int clr) {
		if (clr <= 0 || clr > BoardConfiguration.MAX_CLEARINGS_IN_TILE) {
			return freeSpace;
		}
		return clearings.get(clr);
	}

	public Iterable<Integer> getClearings() {
		return clearings.keySet();
	}

	public void refreshCounters() {
		for (int clr : getClearings()) {
			getClearing(clr).resetAll();
		}
		freeSpace.resetAll();
	}

	@Override
	public Matrix calculateMatrix() {
		Matrix mat = flipper.apply();
		translation.multiply(mat, transformation);
		return transformation;
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics lwgfx) {
		SelectionFrame sf = tiles.getSelectionFrame();
		if (sf.isSelectionPass()) {
			sf.loadID(identifier, lwgfx);
			selectionLocation.get(isEnchanted()).useTexture(lwgfx);
		}
	}

	@Override
	public void draw(LWJGLGraphics lwgfx) {
		updateTransformation();

		for (LWJGLDrawable tf : faces) {
			tf.draw(lwgfx);
		}

	}

	private void initClearings(Iterable<? extends ClearingInterface> clears) {
		getPosition(vec3T);
		for (ClearingInterface clr : clears) {
			clr.getPosition(false, vec3N);
			clr.getPosition(true, vec3E);
			vec3N.add(vec3T, vec3N);
			vec3E.add(vec3T, vec3E);
			clearings.put(clr.getClearingNumber(), new LWJGLClearingStorage(
					this, vec3N, vec3E));
		}
		freeSpace = new LWJGLTileStorage(this, vec3T, clearings.values());
	}

	private Matrix vec3T, vec3N, vec3E;

	private void initFaces() {
		float halfThick = GraphicsConfiguration.TILE_THICKNESS * .5f;
		Matrix mat = Matrix.translation(0f, 0f, halfThick);
		Matrix rot = mat.multiply(rotation);
		faces.add(new LWJGLDrawableLeaf(this, new HexTileFace(this, mat, rot,
				false)));
		Matrix tr = Matrix.rotationX(4, Mathf.PI);
		tr.multiply(mat, mat);
		tr.multiply(rot, rot);
		faces.add(new LWJGLDrawableLeaf(this, new HexTileFace(this, mat, rot, true)));
		// scale square
		mat = Matrix.dilation(.5f, halfThick, 1f, 1f);
		// rotate rectangle
		mat = Matrix.rotationX(4, Mathf.PI * .5f).multiply(mat);
		// move rectange to top
		mat = Matrix.translation(0f, 0.866025f, 0f).multiply(mat);
		for (int i = 0; i < 6; ++i) {
			Matrix tmp = Matrix.rotationZ(4, i * Mathf.PI / 3f).multiply(mat);
			faces.add(new LWJGLDrawableLeaf(this, new SideFace(this, tmp)));
		}
	}

	private class TileFlipper extends TimedAnimator {

		public TileFlipper(float time, boolean flip) {
			super(time);
			ench = flip;
			mat = Matrix.identity(4);
		}

		@Override
		protected Matrix calculateTransform() {
			float i = getInterval();
			mat.rotateX(i * Mathf.PI + (ench ? 0 : Mathf.PI));
			return mat;
		}

		@Override
		public void finish() {
		}

		private boolean ench;
		private Matrix mat;

	}

	private abstract class TileFace extends LWJGLDrawableLeaf {

		public abstract void loadTexture(LWJGLGraphics gfx);

		public TileFace(LWJGLDrawableNode parent, Matrix mat, LWJGLDrawable repr) {
			super(parent, mat, repr);
		}

		@Override
		public void updateNodeUniforms(LWJGLGraphics lwgfx) {
			lwgfx.updateModelViewUniform("modelViewMatrix");
			lwgfx.updateMVPUniform("mvpMatrix");
			loadTexture(lwgfx);
		}
	}

	private class HexTileFace extends TileFace implements MatrixCalculator {

		public HexTileFace(LWJGLDrawableNode par, Matrix nm, Matrix rt,
				boolean ench) {
			super(par, nm, GLPrimitives.HEXAGON);
			tex = textureLocation.get(ench);
			sel = selectionLocation.get(ench);
			norm = Matrix.clone(nm);
			rot = Matrix.clone(rt);
			setCalculator(this);
		}

		@Override
		public Matrix calculateMatrix() {
			SelectionFrame sf = tiles.getSelectionFrame();
			if (sf.isSelectionPass()) {
				return norm;
			}
			return rot;
		}

		@Override
		public void loadTexture(LWJGLGraphics gfx) {
			SelectionFrame sf = tiles.getSelectionFrame();
			if (sf.isSelectionPass()) {
				sel.useTexture(gfx);
			} else {
				tex.useTexture(gfx);
			}
		}

		private LWJGLTextureLoader tex;
		private LWJGLTextureLoader sel;
		private Matrix norm;
		private Matrix rot;

	}

	private class SideFace extends TileFace {

		public SideFace(LWJGLDrawableNode par, Matrix mat) {
			super(par, mat, GLPrimitives.SQUARE);
		}

		@Override
		public void loadTexture(LWJGLGraphics gfx) {
			gfx.getShaders().setUniformIntValue("index", -1);
		}

	}

	private Map<Integer, LWJGLClearingStorage> clearings;
	private LWJGLTileStorage freeSpace;
	private LWJGLTileCollection tiles;
	private Collection<LWJGLDrawableLeaf> faces;
	private Matrix transformation;
	private Matrix rotation;
	private Matrix reverseRotation;
	private Matrix position;
	// private Matrix vector;
	private Matrix translation;
	private EnchantedHolder<LWJGLTextureLoader> textureLocation;
	private EnchantedHolder<LWJGLTextureLoader> selectionLocation;
	private boolean enchanted;
	private AnimationQueue flipper;
	private int identifier;
}
