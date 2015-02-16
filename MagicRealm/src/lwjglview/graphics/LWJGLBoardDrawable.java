package lwjglview.graphics;

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
import lwjglview.graphics.model.ModelData;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.board.Board;
import model.board.HexTile;
import model.enums.ChitType;
import model.enums.TileType;
import model.interfaces.HexTileInterface;
import utils.images.ImageTools;
import utils.math.Mathf;
import utils.math.Matrix;
import utils.resources.ChitImages;
import utils.resources.ResourceHandler;
import utils.resources.TileImages;
import utils.time.Timing;
import view.graphics.Drawable;
import view.graphics.Graphics;
import view.graphics.board.BoardDrawable;

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

		// initialize tiles
		numTiles = TileType.values().length * 2;
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
		numChits = ChitType.values().length;
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
		Drawable chit = ModelData.loadModelData(resources, "chit.obj");
		System.out.println("Finished loading chit model data");

		tiles = new HashSet<LWJGLTileDrawable>();
		for (HexTileInterface ht : bo) {
			TileType type = ht.getType();
			int row = ht.getBoardColumn();
			int col = ht.getBoardRow();
			float x, y, r;
			x = row % 2 == 0 ? 0f : 1.5f;
			x += col * 3f;
			y = -row * 0.866025f;
			r = Mathf.PI * ht.getRotation() / 3f;
			tiles.add(new LWJGLTileDrawable(this, ht.getType(), x, y, r,
					getTextureIndex(type, false), getTextureIndex(type, true)));
		}

		chitDrawables = new HashMap<ChitType, LWJGLChitDrawable>();
		chitDrawables.put(ChitType.CHARACTER_AMAZON, new LWJGLChitDrawable(
				this, ChitType.CHARACTER_AMAZON, chit, 0));
		chitDrawables.put(ChitType.CHARACTER_CAPTAIN, new LWJGLChitDrawable(
				this, ChitType.CHARACTER_CAPTAIN, chit, 1));
		chitDrawables.put(ChitType.CHARACTER_SWORDSMAN, new LWJGLChitDrawable(
				this, ChitType.CHARACTER_SWORDSMAN, chit, 2));

		ambientColour = BufferUtils.createFloatBuffer(4);
	}

	/*
	 * Tiles can request their position from the board
	 */
	public void getTilePosition(TileType tt, FloatBuffer position) {

	}

	/*
	 * Chits can request their position from the board
	 */
	public void getChitPosition(ChitType ct, FloatBuffer position) {

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
		/*
		 * for fisheye projection float ar = lwgfx.getAspectRatio(); float
		 * fovScale = 1f; shaders.setUniformFloatValue(st, "xScale", 1f /
		 * fovScale / ar); shaders.setUniformFloatValue(st, "yScale", 1f /
		 * fovScale); shaders.setUniformFloatValue(st, "nearRadius", .1f);
		 * shaders.setUniformFloatValue(st, "oneOverRadiusDifference", 1f/20f);
		 */

		updateAmbientColour();
		shaders.setUniformFloatArrayValue(st, "ambientColour", 4, ambientColour);

		// draw all tiles
		for (Drawable tile : tiles) {
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
		for (Drawable chit : chitDrawables.values()) {
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
		if (idx == AMBIENT_COLOURS.length / 4) {
			for (LWJGLTileDrawable tile : tiles) {
				tile.setEnchanted(false);
			}
		} else if (idx == AMBIENT_COLOURS.length * 3 / 4) {
			for (LWJGLTileDrawable tile : tiles) {
				tile.setEnchanted(true);
			}
		}
	}

	private int getTextureIndex(TileType type, boolean enchanted) {
		int i = 0;
		for (TileType tt : TileType.values()) {
			if (tt == type) {
				return enchanted ? i + TileType.values().length : i;
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
		for (TileType type : TileType.values()) {
			BufferedImage img = TileImages.getTileImage(resources, type,
					enchanted);
			tileIndex = ImageTools.loadRawImage(img, tileIndex, tileWidth,
					tileHeight, rawTileData);
		}
	}

	private void loadChitImages() throws IOException {
		for (ChitType type : ChitType.values()) {
			BufferedImage img = ChitImages.getChitImage(resources, type);
			chitIndex = ImageTools.loadRawImage(img, chitIndex, chitWidth,
					chitHeight, rawChitData);
		}
	}

	private void relocateChit(ChitType type, float x, float y) {
		chitDrawables.get(type).moveTo(x, y);
	}

	private class ClearingStorage {
		public ClearingStorage(float x, float y) {
			chits = new ArrayList<ChitType>();
			dim = 0;
			xLocation = x;
			yLocation = y;
			buff = BufferUtils.createFloatBuffer(2);
		}

		public void put(ChitType ct) {
			if (!chits.contains(ct)) {
				chits.add(ct);
				if (changeDim()) {
					relocateAllChits();
				}
			}
		}

		public void remove(ChitType ct) {
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

		public void getLocation(ChitType chit, FloatBuffer loc) {
			int idx = chits.indexOf(chit);
			int row = dim - idx / dim - 1;
			int col = idx % dim;
			int gaps = dim - 1;
			float spacing = 2f * GraphicsConfiguration.CHIT_SCALE
					+ GraphicsConfiguration.CHIT_SPACING;
			float offs = spacing * gaps * .5f;
			loc.put(0, col * spacing + offs + xLocation);
			loc.put(1, row * spacing - offs + yLocation);
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
			for (ChitType type : chits) {
				getLocation(type, buff);
				relocateChit(type, buff.get(0), buff.get(1));
			}
		}

		private float xLocation;
		private float yLocation;
		private int dim;
		private List<ChitType> chits;
		private FloatBuffer buff;
	}

	private static class ChitLocation {
		public ChitLocation(TileType tt, int clear) {
			tile = tt;
			clearing = clear;
		}

		public TileType tile;
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

	private Collection<LWJGLTileDrawable> tiles;
	private Map<ChitType, LWJGLChitDrawable> chitDrawables;
	private Map<TileType, ClearingStorage[]> clearings;
	private Map<ChitType, ChitLocation> chitLocations;

	private ResourceHandler resources;

	private FloatBuffer ambientColour;

}
