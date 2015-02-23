package lwjglview.graphics.board;

import java.awt.Color;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;

import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLDrawableLeaf;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.TransformationDrawable;
import lwjglview.graphics.animator.AnimationQueue;
import lwjglview.graphics.animator.FadeAnimator;
import lwjglview.graphics.animator.FollowAnimator;
import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import lwjglview.graphics.animator.matrixcalculator.StaticMatrixCalculator;
import lwjglview.graphics.board.counter.LWJGLCounterCollection;
import lwjglview.graphics.board.mapchit.LWJGLMapChitCollection;
import lwjglview.graphics.board.tile.LWJGLCounterStorage;
import lwjglview.graphics.board.tile.LWJGLTileCollection;
import lwjglview.graphics.board.tile.LWJGLTileDrawable;
import lwjglview.graphics.board.tile.clearing.LWJGLClearingStorage;
import lwjglview.graphics.model.ModelData;
import lwjglview.graphics.shader.GLShaderHandler;
import model.EnchantedHolder;
import model.counter.chit.MapChit;
import model.enums.CounterType;
import model.enums.TileName;
import model.enums.TimeOfDay;
import model.interfaces.ClearingInterface;
import utils.math.Mathf;
import utils.math.Matrix;
import utils.resources.ResourceHandler;
import utils.time.Timing;
import view.controller.game.BoardView;

public class LWJGLBoardDrawable extends LWJGLDrawableNode implements BoardView {

	public static final Matrix[] AMBIENT_COLOURS = new Matrix[] {
			Matrix.columnVector(new float[] { .3f, .3f, .3f, 1f // 00:00
			}), Matrix.columnVector(new float[] { .3f, .3f, .3f, 1f // 03:00
					}), Matrix.columnVector(new float[] { .3f, .3f, .5f, 1f // 06:00
					}), Matrix.columnVector(new float[] { .9f, .9f, .5f, 1f // 09:00
					}), Matrix.columnVector(new float[] { 1f, 1f, 1f, 1f // 12:00
					}), Matrix.columnVector(new float[] { 1f, .8f, .8f, 1f // 15:00
					}), Matrix.columnVector(new float[] { .7f, .7f, .8f, 1f // 18:00
					}), Matrix.columnVector(new float[] { .4f, .4f, .5f, 1f // 21:00
					}) };

	public LWJGLBoardDrawable(ResourceHandler rh, Iterable<MapChit> chits)
			throws IOException {
		super(null);
		resources = rh;
		counterLocations = new HashMap<Integer, CounterStorage>();
		Matrix pos = Matrix.columnVector(3.5f, -3.5f, 0f);
		cameraFocus = new FollowAnimator(pos, new StaticMatrixCalculator(pos),
				GraphicsConfiguration.CAMERA_SPEED);
		buffer4 = BufferUtils.createFloatBuffer(4);

		// initialize tiles
		System.out.println("Loading tile images");
		tiles = new LWJGLTileCollection(this);
		System.out.println("Finished loading tile images");

		System.out.println("Loading chit model data");

		roundCounter = new LWJGLDrawableLeaf(this, ModelData.loadModelData(
				resources, "circle_counter.obj"));

		squareCounter = new LWJGLDrawableLeaf(this, ModelData.loadModelData(
				resources, "square_counter.obj"));

		LWJGLDrawableNode tmp = new LWJGLDrawableLeaf(null,
				ModelData.loadModelData(resources, "knight4.obj"));
		knightCounter = new TransformationDrawable(this, tmp, Matrix.dilation(
				2.7f, 2.7f, 2.7f, 1f).multiply(
				Matrix.rotationX(4, Mathf.PI * .5f)));
		tmp.setParent(knightCounter);

		tmp = new LWJGLDrawableLeaf(null, ModelData.loadModelData(resources,
				"captain.obj"));
		captainCounter = new TransformationDrawable(this, tmp, Matrix.dilation(
				1.3f, 1.3f, 1.3f, 1f).multiply(
				Matrix.rotationX(4, Mathf.PI * .5f)));
		tmp.setParent(captainCounter);

		tmp = new LWJGLDrawableLeaf(null, ModelData.loadModelData(resources,
				"lara.obj"));
		amazonCounter = new TransformationDrawable(this, tmp, Matrix.dilation(
				1.3f, 1.3f, 1.3f, 1f).multiply(
				Matrix.rotationX(4, Mathf.PI * .5f)));
		tmp.setParent(amazonCounter);

		System.out.println("Finished loading chit model data");

		counterID = 0;

		// initialize chits
		System.out.println("Loading chit images");
		counters = new LWJGLCounterCollection(this);
		mapChits = new LWJGLMapChitCollection(this, chits);
		for (MapChit mc : chits) {
			mapChits.create(mc, squareCounter);
		}
		System.out.println("Finished loading chit images");

		ambientColour = new AnimationQueue();
		ambientColour.start();
		timeOfDay = TimeOfDay.DUSK;
		setTimeOfDay(timeOfDay);
	}

	public boolean isTileEnchanted(TileName name) {
		return tiles.isEnchanted(name);
	}

	/*
	 * Tiles can request their position from the board
	 */
	public void getTilePosition(TileName tt, FloatBuffer position) {
		synchronized (tiles) {
			tiles.getPosition(tt, position);
		}
	}

	/*
	 * counters can request their position from the board
	 */
	public void getCounterPosition(LWJGLCounterDrawable cd, FloatBuffer position) {
		int id = cd.getID();
		CounterStorage cl;
		synchronized (counterLocations) {
			cl = counterLocations.get(id);
		}
		getClearing(cl.tile, cl.clearing).getLocation(id, position);
	}

	public void changeColour(CounterType ct, Color col) {
		counters.changeColour(ct, col);
	}

	public synchronized void relocateChit(int id, float x, float y) {
		counterLocations.get(id).counter.moveTo(x, y);
	}

	public ResourceHandler getResourceHandler() {
		return resources;
	}

	public int addCounterDrawable(LWJGLCounterDrawable counter) {
		int id = getNextID();
		counterLocations.put(id, new CounterStorage(counter));
		return id;
	}
	
	public void removeCounterDrawable(int id) {
		counterLocations.remove(id);
	}

	/*
	 * ***********************************************************************
	 * OVERRIDE METHODS
	 * ***********************************************************************
	 */

	@Override
	public synchronized void setMapChit(MapChit mc) {
		setCounter(mapChits.getID(mc), mc.getTile(), 0);
	}

	@Override
	public synchronized void enchantTile(TileName tile) {
		synchronized (tiles) {
			tiles.setEnchanted(tile, true);
		}
	}

	@Override
	public synchronized void updateNodeUniforms(LWJGLGraphics gfx) {
		GLShaderHandler shaders = gfx.getShaders();
		synchronized (buffer4) {
			buffer4.clear();
			ambientColour.apply().toFloatBuffer(buffer4);
			shaders.setUniformFloatArrayValue("ambientColour", 4, buffer4);
		}
	}

	@Override
	public void applyNodeTransformation(LWJGLGraphics lwgfx) {
	}

	@Override
	public void draw(LWJGLGraphics lwgfx) {
		resetView(lwgfx);

		synchronized (tiles) {
			tiles.draw(lwgfx);
		}

		synchronized (counters) {
			counters.draw(lwgfx);
		}

		synchronized (mapChits) {
			mapChits.draw(lwgfx);
		}
	}

	@Override
	public synchronized void focusOn(TileName tile) {
		Matrix vector = tiles.getVector(tile);
		MatrixCalculator calc = new StaticMatrixCalculator(vector);
		cameraFocus.changeFocus(calc);
	}

	@Override
	public synchronized void focusOn(CounterType counter) {
		cameraFocus.changeFocus(new CounterFocus(counter));
	}

	@Override
	public synchronized void focusOn(TileName tile, int clearing) {
		cameraFocus.changeFocus(new ClearingFocus(tile, clearing));
	}

	@Override
	public boolean isAnimationFinished(CounterType ct) {
		return counters.isAnimationFinished(ct);
	}

	@Override
	public synchronized void hideCounter(CounterType ct) {
		changeColour(ct, Color.GREEN);
	}

	@Override
	public synchronized void unHideCounter(CounterType ct) {
		changeColour(ct, Color.WHITE);
	}

	@Override
	public synchronized void setTimeOfDay(TimeOfDay time) {
		ambientColour.push(new FadeAnimator(
				GraphicsConfiguration.DAY_CHANGE_TIME,
				getColourOfDay(timeOfDay), getColourOfDay(time)));
		timeOfDay = time;
	}

	@Override
	public void setTile(TileName tile, int rw, int cl, int rot,
			Iterable<? extends ClearingInterface> clears) {
		tiles.setTile(tile, rw, cl, rot, clears);
	}

	@Override
	public synchronized void setCounter(CounterType tp, TileName tile,
			int clearing) {
		LWJGLCounterDrawable drwble;
		if (!counters.contains(tp)) {
			drwble = counters.create(tp, getCounterRepresentation(tp));
		} else {
			drwble = counters.get(tp);
		}

		setCounter(drwble, tile, clearing);
	}

	@Override
	public void setCounter(CounterType counter, TileName tile) {
		setCounter(counter, tile, 0);
	}

	@Override
	public void hideAllMapChits() {
		mapChits.hideAll();
	}

	@Override
	public void revealMapChit(MapChit chit) {
		mapChits.reveal(chit);
	}

	@Override
	public void replaceMapChit(MapChit chit, Iterable<MapChit> replacements) {
		mapChits.replace(chit, replacements);
	}

	@Override
	public void removeMapChit(MapChit chit) {
		mapChits.remove(chit);
	}

	/*
	 * ***********************************************************************
	 * PROTECTED METHODS
	 * ***********************************************************************
	 */

	protected LWJGLCounterStorage getClearing(TileName tl, int clr) {
		System.out.println(tl);
		return getTile(tl).getClearing(clr);
	}

	public LWJGLTileDrawable getTile(TileName tl) {
		return tiles.get(tl);
	}

	private LWJGLCounterDrawable getCounter(CounterType counter) {
		return counters.get(counter);
	}

	/*
	 * ***********************************************************************
	 * PRIVATE METHODS
	 * ***********************************************************************
	 */

	private int getNextID() {
		return counterID++;
	}

	private void setCounter(LWJGLCounterDrawable counter, TileName tile,
			int clearing) {
		setCounter(counter.getID(), tile, clearing);
	}

	private void setCounter(int id, TileName tile, int clear) {
		CounterStorage loc = counterLocations.get(id);
		if (!loc.isNull()) {
			getClearing(loc.tile, loc.clearing).remove(id);
		}
		loc.tile = tile;
		loc.clearing = clear;
		getClearing(tile, clear).put(id);
	}

	private void resetView(LWJGLGraphics lwgfx) {

		// reset the view matrix
		lwgfx.resetViewMatrix();
		float time = Timing.getSeconds() * .1f;
		Matrix tmp = Matrix.rotationX(4, Mathf.PI / 4f);
		float k = (Mathf.sin(time * .6f) + 6f) / 10f;
		tmp = Matrix.translation(new float[] { 0f, -1f * k, 1f * k }).multiply(
				tmp);
		tmp = Matrix.rotationZ(4, time * .3f).multiply(tmp);
		lwgfx.applyCameraTransform(tmp);
		synchronized (cameraFocus) {
			lwgfx.applyCameraTransform(cameraFocus.apply());
		}

	}

	private Matrix getColourOfDay(TimeOfDay tod) {
		switch (tod) {
		case MIDNIGHT:
			return AMBIENT_COLOURS[0];
		case DUSK:
			return AMBIENT_COLOURS[2];
		case NOON:
			return AMBIENT_COLOURS[4];
		case DAWN:
			return AMBIENT_COLOURS[6];
		default:
			return getColourOfDay(TimeOfDay.MIDNIGHT);
		}
	}

	private LWJGLDrawableNode getCounterRepresentation(CounterType tp) {
		if (tp.isCharacter()) {
			switch (tp) {
			case CHARACTER_SWORDSMAN:
				return knightCounter;
			case CHARACTER_CAPTAIN:
				return captainCounter;
			case CHARACTER_AMAZON:
				return amazonCounter;
			default:
				return roundCounter;
			}
		} else if (tp.isValley()) {
			return squareCounter;
		}
		return squareCounter;
	}

	private Matrix getCounterVector(CounterType counter) {
		return getCounter(counter).getVector();
	}

	private static class CounterStorage {
		public CounterStorage(LWJGLCounterDrawable cd) {
			counter = cd;
			tile = null;
			clearing = 0;
		}

		public CounterStorage(LWJGLCounterDrawable cd, TileName tt, int clear) {
			counter = cd;
			tile = tt;
			clearing = clear;
		}

		public boolean isNull() {
			return tile == null;
		}

		public final LWJGLCounterDrawable counter;
		public TileName tile;
		public int clearing;
	}

	private class CounterFocus implements MatrixCalculator {
		public CounterFocus(CounterType ct) {
			counter = ct;
		}

		@Override
		public Matrix calculateMatrix() {
			return getCounterVector(counter);
		}

		private CounterType counter;
	}

	private class ClearingFocus implements MatrixCalculator {
		public ClearingFocus(TileName tl, int clr) {
			tile = getTile(tl);
			position = new EnchantedHolder<Matrix>();
			LWJGLCounterStorage store = getClearing(tl, clr);
			synchronized (buffer4) {
				addPosition(store, false);
				addPosition(store, true);
			}
		}

		@Override
		public Matrix calculateMatrix() {
			return position.get(tile.isEnchanted());
		}

		private void addPosition(LWJGLCounterStorage store, boolean ench) {
			store.getLocation(buffer4, false);
			position.set(false,
					Matrix.columnVector(buffer4.get(0), buffer4.get(1), 0f));
		}

		private EnchantedHolder<Matrix> position;
		private LWJGLTileDrawable tile;
	}

	private int counterID;

	private LWJGLDrawableNode roundCounter;
	private LWJGLDrawableNode squareCounter;
	private LWJGLDrawableNode knightCounter;
	private LWJGLDrawableNode captainCounter;
	private LWJGLDrawableNode amazonCounter;

	private LWJGLTileCollection tiles;
	private LWJGLCounterCollection counters;
	private LWJGLMapChitCollection mapChits;

	private Map<Integer, CounterStorage> counterLocations;

	private ResourceHandler resources;

	private AnimationQueue ambientColour;

	private FloatBuffer buffer4;

	private FollowAnimator cameraFocus;

	private TimeOfDay timeOfDay;

}
