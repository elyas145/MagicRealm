package lwjglview.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;

import config.GraphicsConfiguration;
import lwjglview.graphics.model.ModelData;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.board.Board;
import model.board.HexTile;
import model.enums.ChitType;
import model.enums.TileType;
import utils.images.ImageTools;
import utils.math.Mathf;
import utils.math.Matrix;
import utils.resources.ChitImages;
import utils.resources.ResourceHandler;
import utils.resources.TileImages;
import utils.time.Timing;
import view.graphics.BoardDrawable;
import view.graphics.Drawable;
import view.graphics.Graphics;

public class LWJGLBoardDrawable extends BoardDrawable {

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

		tiles = new HashSet<Drawable>();
		for (HexTile ht : bo) {
			TileType type = ht.getType();
			tiles.add(new LWJGLTileDrawable(ht, getTextureIndex(type, false),
					getTextureIndex(type, true)));
		}

		chits = new HashSet<Drawable>();
		chits.add(new LWJGLChitDrawable(3f, -3f, chit, 0));
		chits.add(new LWJGLChitDrawable(4.8f, -2.2f, chit, 1));
		chits.add(new LWJGLChitDrawable(4f, -4.5f, chit, 2));
	}

	@Override
	public void draw(Graphics gfx) {
		LWJGLGraphics lwgfx = (LWJGLGraphics) gfx;

		// reset the view matrix
		lwgfx.resetViewMatrix();
		float time = Timing.getSeconds() * .6f;
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

		// draw all chits
		for (Drawable chit : chits) {
			chit.draw(lwgfx);
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
			System.out.println(img);
			chitIndex = ImageTools.loadRawImage(img, chitIndex, chitWidth,
					chitHeight, rawChitData);
		}
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

	private Collection<Drawable> tiles;
	private Collection<Drawable> chits;

	private ResourceHandler resources;

}
