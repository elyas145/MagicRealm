package lwjglview.graphics.board;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;

import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLDrawable;
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
import lwjglview.graphics.board.counter.LWJGLCounterDrawable;
import lwjglview.graphics.board.tile.LWJGLTileCollection;
import lwjglview.graphics.board.tile.LWJGLTileDrawable;
import lwjglview.graphics.board.tile.clearing.LWJGLClearingStorage;
import lwjglview.graphics.model.ModelData;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.EnchantedHolder;
import model.counter.chit.MapChit;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.MapChitType;
import model.enums.TileName;
import model.enums.TimeOfDay;
import model.enums.ValleyChit;
import model.interfaces.ClearingInterface;
import utils.images.ImageTools;
import utils.math.Mathf;
import utils.math.Matrix;
import utils.resources.ChitGenerator;
import utils.resources.CounterImages;
import utils.resources.ResourceHandler;
import utils.resources.TileImages;
import utils.time.Timing;
import utils.tools.ForEach;
import utils.tools.Function;
import utils.tools.IterationTools;
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
		counterLocations = new HashMap<CounterType, CounterLocation>();
		Matrix pos = Matrix.columnVector(3.5f, -3.5f, 0f);
		cameraFocus = new FollowAnimator(pos, new StaticMatrixCalculator(pos),
				GraphicsConfiguration.CAMERA_SPEED);
		buffer4 = BufferUtils.createFloatBuffer(4);

		textureWidth = GraphicsConfiguration.IMAGE_SCALE_WIDTH;
		textureHeight = GraphicsConfiguration.IMAGE_SCALE_HEIGHT;

		// initialize tiles
		System.out.println("Loading tile images");
		tiles = new LWJGLTileCollection(this);
		System.out.println("Finished loading tile images");

		// initialize chits
		numChits = CounterType.values().length + 10;
		rawCounterData = BufferUtils.createByteBuffer(numChits * textureHeight
				* textureWidth * 4);
		counterIndex = 0;
		counterTextureIndex = 0;
		counterTextureIndices = new HashMap<CounterType, Integer>();
		chitTextureLocation = -1;
		System.out.println("Loading chit images");
		loadCounterImages();
		loadMapChits(chits);
		System.out.println("Finished loading chit images");
		System.out.println("Loading chit model data");
		roundCounter = new LWJGLDrawableLeaf(this, ModelData.loadModelData(
				resources, "circle_counter.obj"));
		squareCounter = new LWJGLDrawableLeaf(this, ModelData.loadModelData(
				resources, "square_counter.obj"));
		LWJGLDrawableNode knight = new LWJGLDrawableLeaf(null,
				ModelData.loadModelData(resources, "knight4.obj"));
		knightCounter = new TransformationDrawable(this, knight, Matrix
				.dilation(2f, 2f, 2f, 1f).multiply(
						Matrix.rotationX(4, Mathf.PI * .5f)));
		knight.setParent(knightCounter);
		System.out.println("Finished loading chit model data");

		counters = new LWJGLCounterCollection(this);

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
		CounterType ct = cd.getCounterType();
		CounterLocation cl = counterLocations.get(ct);
		getClearing(cl.tile, cl.clearing).getLocation(ct, position);
	}

	public void changeColour(CounterType ct, Color col) {
		counters.changeColour(ct, col);
	}

	public synchronized void relocateChit(CounterType type, float x, float y) {
		counters.moveTo(type, x, y);
	}

	public ResourceHandler getResourceHandler() {
		return resources;
	}

	/*
	 * ***********************************************************************
	 * OVERRIDE METHODS
	 * ***********************************************************************
	 */

	@Override
	public synchronized void loadMapChits(Iterable<MapChit> chits) {
		System.out.println("Generating map chits");
		/*for (MapChit chit : chits) {
			loadMapChitImage(ChitGenerator.generateMapChit(chit),
					chit.getType());
		}*/

		System.out.println("Finished generating map chits");
		// TODO check if this actually works
	}

	@Override
	public void setMapChit(MapChit mc) {
		// TODO test this
		//setCounter(mc.getType().toCounter(), mc.getTile());
	}

	@Override
	public void enchantTile(TileName tile) {
		synchronized (tiles) {
			tiles.setEnchanted(tile, true);
		}
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
		GLShaderHandler shaders = gfx.getShaders();
		synchronized (buffer4) {
			buffer4.clear();
			ambientColour.apply().toFloatBuffer(buffer4);
			shaders.setUniformFloatArrayValue("ambientColour",
					4, buffer4);
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

		// TODO draw map chits ?
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

	/*
	 * ***********************************************************************
	 * PROTECTED METHODS
	 * ***********************************************************************
	 */

	protected LWJGLClearingStorage getClearing(TileName tl, int clr) {
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

	private void setCounter(LWJGLCounterDrawable counter, TileName tile,
			int clearing) {
		CounterType tp = counter.getCounterType();

		if (!counterLocations.containsKey(tp)) {
			counterLocations.put(tp, new CounterLocation(tile, clearing));
		} else {
			CounterLocation loc = counterLocations.get(tp);
			getClearing(loc.tile, loc.clearing).remove(tp);
			loc.tile = tile;
			loc.clearing = clearing;
		}

		getClearing(tile, clearing).put(tp);
	}

	private void resetView(LWJGLGraphics lwgfx) {

		// reset the view matrix
		lwgfx.resetViewMatrix();
		float time = Timing.getSeconds() * .1f;
		Matrix tmp = Matrix.rotationX(4, Mathf.PI / 4f);
		float k = (Mathf.sin(time * .6f) + 6f) / 6f;
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
			if (tp == CounterType.CHARACTER_SWORDSMAN) {
				return knightCounter;
			}
			return roundCounter;
		} else if (tp.isValley()) {
			return squareCounter;
		}
		return squareCounter;
	}

	private void loadTextures(LWJGLGraphics gfx) {
		if (chitTextureLocation < 0) {
			chitTextureLocation = gfx.loadTextureArray(rawCounterData,
					numChits, textureHeight, textureWidth);
			rawCounterData = null;
		}
	}

	private void loadCounterImages() throws IOException {
		for (CharacterType type : CharacterType.values()) {
			BufferedImage img = CounterImages.getCounterImage(resources,
					type.toCounter());
			loadRawCounterImage(img, type.toCounter());
		}
		for (ValleyChit type : ValleyChit.values()) {
			BufferedImage img = CounterImages.getCounterImage(resources,
					type.toCounterType());
			loadRawCounterImage(img, type.toCounterType());
		}
	}

	private void loadMapChitImage(BufferedImage bi, MapChitType mct) {
		loadRawCounterImage(bi, mct.toCounter());
	}

	private void loadRawCounterImage(BufferedImage bi, CounterType ct) {
		counterIndex = ImageTools.loadRawImage(bi, counterIndex, bi.getWidth(),
				bi.getHeight(), rawCounterData);
		counterTextureIndices.put(ct, counterTextureIndex);
		++counterTextureIndex;
	}

	private int getCounterTextureIndex(CounterType ct) {
		int idx = 0;
		// if (ct == CounterType.CHARACTER_SWORDSMAN) {
		// return -1;
		// }
		for (CounterType tp : CounterType.values()) {
			if (tp == ct) {
				return idx;
			}
			++idx;
		}
		return idx;
	}

	private Matrix getCounterVector(CounterType counter) {
		return getCounter(counter).getVector();
	}

	private static class CounterLocation {
		public CounterLocation(TileName tt, int clear) {
			tile = tt;
			clearing = clear;
		}

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
			LWJGLClearingStorage store = getClearing(tl, clr);
			synchronized (buffer4) {
				addPosition(store, false);
				addPosition(store, true);
			}
		}

		@Override
		public Matrix calculateMatrix() {
			return position.get(tile.isEnchanted());
		}

		private void addPosition(LWJGLClearingStorage store, boolean ench) {
			store.getLocation(buffer4, false);
			position.set(false,
					Matrix.columnVector(buffer4.get(0), buffer4.get(1), 0f));
		}

		private EnchantedHolder<Matrix> position;
		private LWJGLTileDrawable tile;
	}

	private int textureWidth;
	private int textureHeight;

	private int counterIndex;
	private int numChits;
	private int chitTextureLocation;

	private ByteBuffer rawCounterData;

	private LWJGLDrawableNode roundCounter;
	private LWJGLDrawableNode squareCounter;
	private LWJGLDrawableNode knightCounter;

	private LWJGLTileCollection tiles;
	private LWJGLCounterCollection counters;
	private Map<CounterType, Integer> counterTextureIndices;
	private int counterTextureIndex;

	private Map<CounterType, CounterLocation> counterLocations;

	private ResourceHandler resources;

	private AnimationQueue ambientColour;

	private FloatBuffer buffer4;

	private FollowAnimator cameraFocus;

	private TimeOfDay timeOfDay;

	private ShaderType currentShader;

}
