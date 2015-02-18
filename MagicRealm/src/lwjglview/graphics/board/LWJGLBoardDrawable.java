package lwjglview.graphics.board;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;

import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.model.ModelData;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.board.Board;
import model.enums.CounterType;
import model.enums.TileName;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;
import utils.images.ImageTools;
import utils.math.Mathf;
import utils.math.Matrix;
import utils.random.Random;
import utils.resources.CounterImages;
import utils.resources.ResourceHandler;
import utils.resources.TileImages;
import utils.time.Timing;
import view.graphics.Drawable;
import view.graphics.Graphics;
import view.graphics.board.BoardDrawable;
import view.graphics.board.CounterDrawable;

public class LWJGLBoardDrawable extends BoardDrawable {

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

	public LWJGLBoardDrawable(Board bo, ResourceHandler rh) throws IOException {
		super(bo);
		resources = rh;
		tiles = new HashMap<TileName, LWJGLTileDrawable>();
		clearings = new HashMap<TileName, Map<Integer, ClearingStorage>>();
		counterLocations = new HashMap<CounterType, CounterLocation>();

		// initialize tiles
		numTiles = TileName.values().length * 2;
		tileHeight = GraphicsConfiguration.TILE_IMAGE_HEIGHT;
		tileWidth = GraphicsConfiguration.TILE_IMAGE_WIDTH;
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
		chitWidth = GraphicsConfiguration.CHIT_IMAGE_WIDTH;
		chitHeight = GraphicsConfiguration.CHIT_IMAGE_HEIGHT;
		rawChitData = ByteBuffer.allocateDirect(numChits * chitHeight
				* chitWidth * 4);
		chitIndex = 0;
		chitTextureLocation = -1;
		System.out.println("Loading chit images");
		loadChitImages();
		System.out.println("Finished loading chit images");
		System.out.println("Loading chit model data");
		roundCounter = ModelData.loadModelData(resources, "chit_circle.obj");
		System.out.println("Finished loading chit model data");

		// initialize tile and clearings info
		FloatBuffer bufferN, bufferE, bufferT;
		bufferN = BufferUtils.createFloatBuffer(2);
		bufferE = BufferUtils.createFloatBuffer(2);
		bufferT = BufferUtils.createFloatBuffer(2);
		for (HexTileInterface ht : bo.iterateTiles()) {
			TileName type = ht.getName();
			int row = ht.getBoardColumn();
			int col = ht.getBoardRow();
			float x, y, r;
			x = row % 2 == 0 ? 0f : 1.5f;
			x += col * 3f;
			y = -row * 0.866025f;
			r = Mathf.PI * ht.getRotation() / 3f;
			TileName tn = ht.getName();
			Map<Integer, ClearingStorage> tileClr = new HashMap<Integer, ClearingStorage>();
			clearings.put(tn, tileClr);
			tiles.put(tn, new LWJGLTileDrawable(this, tn, x, y, r,
					getTextureIndex(type, false), getTextureIndex(type, true)));
			bufferT.put(0, x);
			bufferT.put(1, y);
			for(ClearingInterface clr: ht.getClearings()) {
				clr.getPosition(false, bufferN);
				clr.getPosition(true, bufferE);
				tileClr.put(clr.getClearingNumber(), new ClearingStorage(tn, bufferT, bufferN, bufferE));
			}
		}
		
		counterDrawables = new HashMap<CounterType, LWJGLCounterDrawable>();
		bland = bo.getTile(TileName.BORDERLAND);
		setCounter(CounterType.CHARACTER_AMAZON, TileName.BORDERLAND, 1);
		setCounter(CounterType.CHARACTER_CAPTAIN, TileName.BORDERLAND, 2);
		setCounter(CounterType.CHARACTER_SWORDSMAN, TileName.BORDERLAND, 3);

		ambientColour = BufferUtils.createFloatBuffer(4);
	}
	
	private HexTileInterface bland;
	
	@Override
	public void setCounter(CounterType tp, TileName tile, int clearing) {
		
		if(!counterDrawables.containsKey(tp)) {
			counterDrawables.put(tp, new LWJGLCounterDrawable(
				this, tp, roundCounter, getCounterTextureIndex(tp), bland.getClearing(clearing)));
		}
		
		LWJGLCounterDrawable drw = counterDrawables.get(tp);
		
		if(!counterLocations.containsKey(tp)) {
			counterLocations.put(tp, new CounterLocation(tile, clearing));
		}
		else {
			CounterLocation loc = counterLocations.get(tp);
			clearings.get(loc.tile).get(loc.clearing).remove(tp);
			loc.tile = tile;
			loc.clearing = clearing;
		}
		
		clearings.get(tile).get(clearing).put(tp);
		
	}

	@Override
	public boolean isTileEnchanted(TileName name) {
		return tiles.get(name).isEnchanted();
	}

	/*
	 * Tiles can request their position from the board
	 */
	public void getTilePosition(TileName tt, FloatBuffer position) {
		tiles.get(tt).getPosition(position);
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

	@Override
	public void draw(Graphics gfx) {
		LWJGLGraphics lwgfx = (LWJGLGraphics) gfx;

		// reset the view matrix
		lwgfx.resetViewMatrix();
		float time = Timing.getSeconds() * .1f;
		Matrix tmp = Matrix.rotationX(4, Mathf.PI / 5f);
		float k = (Mathf.sin(time * .6f) + 3f) / 3f;
		tmp = Matrix.translation(new float[] { 0f, -4f * k, 3f * k }).multiply(
				tmp);
		tmp = Matrix.rotationZ(4, time * .3f).multiply(tmp);
		lwgfx.applyCameraTransform(tmp);
		lwgfx.translateCamera(3.5f, -3.5f, 0f);

		// load all textures to GPU
		loadTextures(lwgfx);

		// load tile textures
		lwgfx.bindTextureArray(tileTextureLocation);

		// load tile shader program
		GLShaderHandler shaders = lwgfx.getShaders();
		ShaderType st = ShaderType.TILE_SHADER;
		if (!shaders.hasProgram(st)) {
			try {
				shaders.loadShaderProgram(st);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		shaders.useShaderProgram(st);

		updateAmbientColour();
		shaders.setUniformFloatArrayValue(st, "ambientColour", 4, ambientColour);

		// draw all tiles
		for (Drawable tile : tiles.values()) {
			tile.draw(lwgfx);
		}

		// load chit textures
		lwgfx.bindTextureArray(chitTextureLocation);

		// load chit shader program
		st = ShaderType.CHIT_SHADER;
		if (!shaders.hasProgram(st)) {
			try {
				shaders.loadShaderProgram(st);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		shaders.useShaderProgram(st);
		/*
		 * again, for fisheye shaders.setUniformFloatValue(st, "xScale", 1f /
		 * fovScale / ar); shaders.setUniformFloatValue(st, "yScale", 1f /
		 * fovScale); shaders.setUniformFloatValue(st, "nearRadius", .1f);
		 * shaders.setUniformFloatValue(st, "oneOverRadiusDifference", 1f/20f);
		 */

		// update shader colour
		shaders.setUniformFloatArrayValue(st, "ambientColour", 4, ambientColour);

		// draw all chits
		for (Drawable chit : counterDrawables.values()) {
			chit.draw(lwgfx);
		}

	}

	private void updateAmbientColour() {
		float time = Timing.getSeconds() * .3f;
		int idx = (int) time % AMBIENT_COLOURS.length;
		int nidx = (idx + 1) % AMBIENT_COLOURS.length;
		float scale = time % 1f;
		AMBIENT_COLOURS[idx].multiply(1f - scale)
				.add(AMBIENT_COLOURS[nidx].multiply(scale))
				.toFloatBuffer(ambientColour);
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

	private void relocateChit(CounterType type, float x, float y) {
		counterDrawables.get(type).moveTo(x, y);
	}
	
	private int getCounterTextureIndex(CounterType ct) {
		int idx = 0;
		for(CounterType tp: CounterType.values()) {
			if(tp == ct) {
				return idx;
			}
			++idx;
		}
		return idx;
	}

	private class ClearingStorage {
		public ClearingStorage(TileName tl, FloatBuffer tc, FloatBuffer nl, FloatBuffer el) {
			tile = tl;
			posns = new float[][] {
					{tc.get(0) + nl.get(0), tc.get(1) + nl.get(1)},
					{tc.get(0) + el.get(0), tc.get(1) + el.get(1)}
			};
			chits = new ArrayList<CounterType>();
			dim = 0;
			buff = BufferUtils.createFloatBuffer(2);
		}

		public void put(CounterType ct) {
			if (!chits.contains(ct)) {
				chits.add(ct);
				if (changeDim()) {
					relocateAllChits();
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
						getLocation(chits.get(idx), buff);
						relocateChit(chits.get(idx), buff.get(0), buff.get(1));
					}
				}
			}
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
			loc.put(0, col * spacing + offs + pos[0]);
			loc.put(1, row * spacing - offs + pos[1]);
		}

		private boolean changeDim() {
			double sqrt = Math.sqrt(chits.size());
			int tmp = (int) sqrt;
			if (sqrt % 1. != 0.) {
				tmp += 1;
			}
			if (tmp > dim) {
				dim = tmp;
				return true;
			}
			return false;
		}

		private void relocateAllChits() {
			for (CounterType type : chits) {
				getLocation(type, buff);
				relocateChit(type, buff.get(0), buff.get(1));
			}
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
	
	private ModelData roundCounter;

	private Map<TileName, LWJGLTileDrawable> tiles;
	private Map<CounterType, LWJGLCounterDrawable> counterDrawables;
	
	// stores location information for counters in a clearing
	private Map<TileName, Map<Integer, ClearingStorage>> clearings;
	
	private Map<CounterType, CounterLocation> counterLocations;

	private ResourceHandler resources;

	private FloatBuffer ambientColour;

}
