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
import lwjglview.graphics.animator.Animator;
import lwjglview.graphics.animator.FadeAnimator;
import lwjglview.graphics.animator.FollowAnimator;
import lwjglview.graphics.animator.MovementAnimator;
import lwjglview.graphics.animator.StaticAnimator;
import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import lwjglview.graphics.animator.matrixcalculator.StaticMatrixCalculator;
import lwjglview.graphics.animator.matrixcalculator.StaticTranslationCalculator;
import lwjglview.graphics.model.ModelData;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.enums.CounterType;
import model.enums.TileName;
import model.enums.TimeOfDay;
import model.interfaces.ClearingInterface;
import utils.images.ImageTools;
import utils.math.Mathf;
import utils.math.Matrix;
import utils.resources.CounterImages;
import utils.resources.ResourceHandler;
import utils.resources.TileImages;
import utils.time.Timing;
import view.controller.game.BoardView;
import view.graphics.Drawable;
import view.graphics.Graphics;

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

	public LWJGLBoardDrawable(ResourceHandler rh) throws IOException {
		super(null);
		resources = rh;
		tiles = new HashMap<TileName, LWJGLTileDrawable>();
		clearings = new HashMap<TileName, Map<Integer, ClearingStorage>>();
		counterLocations = new HashMap<CounterType, CounterLocation>();
		Matrix pos = Matrix.columnVector(3.5f, -3.5f, 0f);
		cameraFocus = new FollowAnimator(pos, new StaticMatrixCalculator(pos),
				GraphicsConfiguration.CAMERA_SPEED);
		buffer4 = BufferUtils.createFloatBuffer(4);
		basis4 = Matrix.columnVector(0f, 0f, 0f, 1f);

		// initialize tiles
		numTiles = TileName.values().length * 2;
		tileHeight = GraphicsConfiguration.IMAGE_SCALE_WIDTH;
		tileWidth = GraphicsConfiguration.IMAGE_SCALE_HEIGHT;
		rawTileData = ByteBuffer.allocateDirect(numTiles * tileHeight
				* tileWidth * 4);
		tileIndex = 0;
		tileTextureLocation = -1;
		System.out.println("Loading tile images");
		loadTileImages(false);
		loadTileImages(true);
		System.out.println("Finished loading tile images");

		// initialize chits
		numChits = CounterType.values().length;
		chitWidth = GraphicsConfiguration.IMAGE_SCALE_WIDTH;
		chitHeight = GraphicsConfiguration.IMAGE_SCALE_HEIGHT;
		rawChitData = ByteBuffer.allocateDirect(numChits * chitHeight
				* chitWidth * 4);
		chitIndex = 0;
		chitTextureLocation = -1;
		System.out.println("Loading chit images");
		loadChitImages();
		System.out.println("Finished loading chit images");
		System.out.println("Loading chit model data");
		roundCounter = new LWJGLDrawableLeaf(this, ModelData.loadModelData(resources, "circle_counter.obj"));
		squareCounter = new LWJGLDrawableLeaf(this, ModelData
				.loadModelData(resources, "square_counter.obj"));
		LWJGLDrawableNode knight = new LWJGLDrawableLeaf(null, ModelData.loadModelData(resources, "knight5.obj"));
		knightCounter = new TransformationDrawable(this, knight,
				Matrix.dilation(3f, 3f, 3f, 1f).multiply(Matrix.rotationX(4, Mathf.PI * .5f)));
		knight.setParent(knightCounter);
		System.out.println("Finished loading chit model data");

		// initialize buffers for tile locations
		bufferN = BufferUtils.createFloatBuffer(2);
		bufferE = BufferUtils.createFloatBuffer(2);
		bufferT = BufferUtils.createFloatBuffer(2);

		counterDrawables = new HashMap<CounterType, LWJGLCounterDrawable>();

		ambientColour = new AnimationQueue();
		ambientColour.start();
		timeOfDay = TimeOfDay.DUSK;
		setTimeOfDay(timeOfDay);
	}

	public boolean isTileEnchanted(TileName name) {
		return tiles.get(name).isEnchanted();
	}

	/*
	 * Tiles can request their position from the board
	 */
	public void getTilePosition(TileName tt, FloatBuffer position) {
		synchronized (tiles) {
			tiles.get(tt).getPosition(position);
		}
	}

	/*
	 * counters can request their position from the board
	 */
	public void getCounterPosition(LWJGLCounterDrawable cd, FloatBuffer position) {
		CounterType ct = cd.getCounterType();
		CounterLocation cl = counterLocations.get(ct);
		Map<Integer, ClearingStorage> stores = clearings.get(cl.tile);
		ClearingStorage store = stores.get(cl.clearing);
		store.getLocation(ct, position);
	}

	/*
	 * ***********************************************************************
	 * OVERRIDE METHODS
	 * ***********************************************************************
	 */

	@Override
	public void enchantTile(TileName tile) {
		synchronized (tiles) {
			tiles.get(tile).setEnchanted(true);
		}
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
		GLShaderHandler shaders = gfx.getShaders();
		buffer4.clear();
		ambientColour.apply().toFloatBuffer(buffer4);
		shaders.setUniformFloatArrayValue(currentShader, "ambientColour", 4, buffer4);
	}

	@Override
	public void applyNodeTransformation(LWJGLGraphics lwgfx) {
	}

	@Override
	public void draw(LWJGLGraphics lwgfx) {
		
		resetView(lwgfx);

		// load all textures to GPU
		loadTextures(lwgfx);

		// load tile textures
		lwgfx.bindTextureArray(tileTextureLocation);

		// load tile shader program
		GLShaderHandler shaders = lwgfx.getShaders();
		currentShader = ShaderType.TILE_SHADER;
		if (!shaders.hasProgram(currentShader)) {
			try {
				shaders.loadShaderProgram(currentShader);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		shaders.useShaderProgram(currentShader);

		synchronized (tiles) {
			// draw all tiles
			for (LWJGLDrawable tile : tiles.values()) {
				tile.draw(lwgfx);
			}
		}

		// load chit textures
		lwgfx.bindTextureArray(chitTextureLocation);

		// load chit shader program
		currentShader = ShaderType.CHIT_SHADER;
		if (!shaders.hasProgram(currentShader)) {
			try {
				shaders.loadShaderProgram(currentShader);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		shaders.useShaderProgram(currentShader);

		synchronized (counterDrawables) {
			// draw all chits
			for (LWJGLCounterDrawable counter : counterDrawables.values()) {
				counter.draw(lwgfx);
			}
		}
	}

	@Override
	public void focusOn(TileName tile) {
		Matrix vector = tiles.get(tile).getVector();
		MatrixCalculator calc = new StaticMatrixCalculator(vector);
		cameraFocus.changeFocus(calc);
	}

	@Override
	public void focusOn(CounterType counter) {
		cameraFocus.changeFocus(new CounterFocus(counter));
	}

	@Override
	public void focusOn(TileName tile, int clearing) {
		cameraFocus.changeFocus(new ClearingFocus(tile, clearing));
	}

	@Override
	public boolean isAnimationFinished(CounterType ct) {
		return counterDrawables.get(ct).isAnimationFinished();
	}

	@Override
	public void hideCounter(CounterType ct) {
		counterDrawables.get(ct).changeColour(Color.GREEN);
	}

	@Override
	public void setTimeOfDay(TimeOfDay time) {
		ambientColour.push(new FadeAnimator(
				GraphicsConfiguration.DAY_CHANGE_TIME,
				getColourOfDay(timeOfDay), getColourOfDay(time)));
		timeOfDay = time;
	}

	@Override
	public void setTile(TileName tile, int rw, int cl, int rot,
			Iterable<? extends ClearingInterface> clears) {
		int row = rw;
		int col = cl;
		float x, y, r;
		x = row % 2 == 0 ? 0f : 1.5f;
		x += col * 3f;
		y = -row * 0.866025f;
		r = Mathf.PI * rot / 3f;
		Map<Integer, ClearingStorage> tileClr = new HashMap<Integer, ClearingStorage>();
		clearings.put(tile, tileClr);
		synchronized (tiles) {
			tiles.put(tile, new LWJGLTileDrawable(this, x, y, r,
					getTextureIndex(tile, false), getTextureIndex(tile, true)));
		}
		bufferT.put(0, x);
		bufferT.put(1, y);
		tileClr.put(0, new ClearingStorage(tile, bufferT));
		for (ClearingInterface clr : clears) {
			clr.getPosition(false, bufferN);
			clr.getPosition(true, bufferE);
			tileClr.put(clr.getClearingNumber(), new ClearingStorage(tile,
					bufferT, bufferN, bufferE));
		}
	}

	@Override
	public void setCounter(CounterType tp, TileName tile, int clearing) {

		if (!counterDrawables.containsKey(tp)) {
			counterDrawables.put(tp, new LWJGLCounterDrawable(this, tp,
					getCounterRepresentation(tp), getCounterTextureIndex(tp)));
		}

		if (!counterLocations.containsKey(tp)) {
			counterLocations.put(tp, new CounterLocation(tile, clearing));
		} else {
			CounterLocation loc = counterLocations.get(tp);
			clearings.get(loc.tile).get(loc.clearing).remove(tp);
			loc.tile = tile;
			loc.clearing = clearing;
		}

		clearings.get(tile).get(clearing).put(tp);

	}

	@Override
	public void setCounter(CounterType counter, TileName tile) {
		setCounter(counter, tile, 0);
	}

	/*
	 * ***********************************************************************
	 * PRIVATE METHODS
	 * ***********************************************************************
	 */
	
	private void resetView(LWJGLGraphics lwgfx) {

		// reset the view matrix
		lwgfx.resetViewMatrix();
		float time = Timing.getSeconds() * .1f;
		Matrix tmp = Matrix.rotationX(4, Mathf.PI / 4f);
		float k = (Mathf.sin(time * .6f) + 6f) / 4f;
		tmp = Matrix.translation(new float[] { 0f, -1f * k, 1f * k }).multiply(
				tmp);
		tmp = Matrix.rotationZ(4, time * .3f).multiply(tmp);
		lwgfx.applyCameraTransform(tmp);
		lwgfx.applyCameraTransform(cameraFocus.apply());

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

	private int getTextureIndex(TileName type, boolean enchanted) {
		int i = 0;
		for (TileName tt : TileName.values()) {
			if (tt == type) {
				return enchanted ? i + TileName.values().length : i;
			}
			++i;
		}
		return -1;
	}

	private void loadTextures(LWJGLGraphics gfx) {
		if (tileTextureLocation < 0) {
			tileTextureLocation = gfx.loadTextureArray(rawTileData, numTiles,
					tileHeight, tileWidth);
		}
		if (chitTextureLocation < 0) {
			chitTextureLocation = gfx.loadTextureArray(rawChitData, numChits,
					chitHeight, chitWidth);
		}
	}

	private void loadTileImages(boolean enchanted) throws IOException {
		for (TileName type : TileName.values()) {
			BufferedImage img = TileImages.getTileImage(resources, type,
					enchanted);
			tileIndex = ImageTools.loadRawImage(img, tileIndex, tileWidth,
					tileHeight, rawTileData);
		}
	}

	private void loadChitImages() throws IOException {
		for (CounterType type : CounterType.values()) {
			BufferedImage img = CounterImages.getCounterImage(resources, type);
			chitIndex = ImageTools.loadRawImage(img, chitIndex, chitWidth,
					chitHeight, rawChitData);
		}
	}

	private synchronized void relocateChit(CounterType type, float x, float y) {
		counterDrawables.get(type).moveTo(x, y);
	}

	private int getCounterTextureIndex(CounterType ct) {
		int idx = 0;
		if(ct == CounterType.CHARACTER_SWORDSMAN) {
			return -1;
		}
		for (CounterType tp : CounterType.values()) {
			if (tp == ct) {
				return idx;
			}
			++idx;
		}
		return idx;
	}

	private Matrix getCounterVector(CounterType counter) {
		return counterDrawables.get(counter).getVector();
	}

	private class ClearingStorage {
		public ClearingStorage(TileName tl, FloatBuffer tc, FloatBuffer nl,
				FloatBuffer el) {
			tile = tl;
			posns = new float[][] {
					{ tc.get(0) + nl.get(0), tc.get(1) + nl.get(1) },
					{ tc.get(0) + el.get(0), tc.get(1) + el.get(1) } };
			chits = new ArrayList<CounterType>();
			dim = 0;
			buff = BufferUtils.createFloatBuffer(2);
		}
		
		public ClearingStorage(TileName tl, FloatBuffer tc) {
			tile = tl;
			posns = new float[][] {
					{ tc.get(0), tc.get(1) },
					{ tc.get(0), tc.get(1) } };
			chits = new ArrayList<CounterType>();
			dim = 0;
			buff = BufferUtils.createFloatBuffer(2);
		}

		public void put(CounterType ct) {
			if (!chits.contains(ct)) {
				chits.add(ct);
				if (changeDim()) {
					relocateAllChits();
				} else {
					moveChit(ct);
				}
			}
		}

		public void remove(CounterType ct) {
			if (chits.contains(ct)) {
				int idx = chits.indexOf(ct);
				chits.remove(ct);
				if (changeDim()) {
					relocateAllChits();
				} else {
					for (; idx < chits.size(); ++idx) {
						moveChit(chits.get(idx));
					}
				}
			}
		}

		public void getLocation(FloatBuffer loc) {
			boolean ench = isTileEnchanted(tile);
			float[] pos = posns[ench ? 1 : 0];
			loc.put(0, pos[0]);
			loc.put(1, pos[1]);
		}

		public void getLocation(CounterType chit, FloatBuffer loc) {
			int idx = chits.indexOf(chit);
			int row = dim - idx / dim - 1;
			int col = idx % dim;
			int gaps = dim - 1;
			float spacing = 2f * GraphicsConfiguration.CHIT_SCALE
					+ GraphicsConfiguration.CHIT_SPACING;
			float offs = spacing * gaps * .5f;
			boolean ench = isTileEnchanted(tile);
			float[] pos = posns[ench ? 1 : 0];
			loc.put(0, col * spacing - offs + pos[0]);
			loc.put(1, row * spacing - offs + pos[1]);
		}

		private boolean changeDim() {
			double sqrt = Math.sqrt(chits.size());
			int tmp = (int) sqrt;
			if (sqrt % 1. > .0001) {
				tmp += 1;
			}
			if (tmp != dim) {
				dim = tmp;
				return true;
			}
			return false;
		}

		private void relocateAllChits() {
			for (CounterType type : chits) {
				moveChit(type);
			}
		}

		private void moveChit(CounterType ct) {
			getLocation(ct, buff);
			relocateChit(ct, buff.get(0), buff.get(1));
		}

		private TileName tile;
		private float[][] posns;
		private int dim;
		private List<CounterType> chits;
		private FloatBuffer buff;
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
			tile = tl;
			store = clearings.get(tl).get(clr);
			updateMatrix();
		}

		@Override
		public Matrix calculateMatrix() {
			if (!enchanted && isTileEnchanted(tile)) {
				updateMatrix();
			}
			return position;
		}

		private void updateMatrix() {
			store.getLocation(buffer4);
			position = Matrix.columnVector(buffer4.get(0), buffer4.get(1), 0f);
		}

		private ClearingStorage store;
		private Matrix position;
		private boolean enchanted;
		private TileName tile;
	}

	private int tileIndex;
	private int numTiles;
	private int tileHeight;
	private int tileWidth;
	private int tileTextureLocation;

	private int chitIndex;
	private int numChits;
	private int chitHeight;
	private int chitWidth;
	private int chitTextureLocation;

	private ByteBuffer rawTileData;
	private ByteBuffer rawChitData;

	private LWJGLDrawableNode roundCounter;
	private LWJGLDrawableNode squareCounter;
	private LWJGLDrawableNode knightCounter;

	private Map<TileName, LWJGLTileDrawable> tiles;
	private Map<CounterType, LWJGLCounterDrawable> counterDrawables;

	// stores location information for counters in a clearing
	private Map<TileName, Map<Integer, ClearingStorage>> clearings;

	private Map<CounterType, CounterLocation> counterLocations;

	private ResourceHandler resources;

	private AnimationQueue ambientColour;

	private FloatBuffer buffer4;
	private Matrix basis4;

	private Matrix cameraLocation;

	private FollowAnimator cameraFocus;

	private TimeOfDay timeOfDay;

	private FloatBuffer bufferN, bufferE, bufferT;
	
	private ShaderType currentShader;

}
