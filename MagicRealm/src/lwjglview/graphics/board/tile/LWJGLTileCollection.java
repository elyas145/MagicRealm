package lwjglview.graphics.board.tile;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import utils.math.Mathf;
import utils.math.Matrix;
import utils.resources.ResourceHandler;
import utils.resources.TileImages;
import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.LWJGLTextureArrayLoader;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.graphics.shader.ShaderType;
import model.EnchantedHolder;
import model.enums.CounterType;
import model.enums.TileName;
import model.interfaces.ClearingInterface;

public class LWJGLTileCollection extends LWJGLDrawableNode {

	public LWJGLTileCollection(LWJGLBoardDrawable par) throws IOException {
		super(par);
		board = par;
		textures = new LWJGLTextureArrayLoader(
				GraphicsConfiguration.IMAGE_SCALE_WIDTH,
				GraphicsConfiguration.IMAGE_SCALE_HEIGHT);
		tiles = new HashMap<TileName, LWJGLTileDrawable>();
		textureLocations = new HashMap<TileName, EnchantedHolder<Integer>>();
		loadTileImages();
	}

	public boolean isEnchanted(TileName name) {
		return get(name).isEnchanted();
	}

	public void getPosition(TileName name, FloatBuffer position) {
		get(name).getPosition(position);
	}
	
	public Matrix getVector(TileName tile) {
		return get(tile).getVector();
	}

	public void setEnchanted(TileName tile, boolean ench) {
		get(tile).setEnchanted(ench);
	}
	
	public void setTile(TileName tile, int rw, int cl, int rot,
			Iterable<? extends ClearingInterface> clears) {
		int row = rw;
		int col = cl;
		float x, y, r;
		x = row % 2 == 0 ? 0f : 1.5f;
		x += col * 3f;
		y = -row * 0.866025f;
		r = Mathf.PI * rot / 3f;
		EnchantedHolder<Integer> loc = textureLocations.get(tile);
		tiles.put(tile, new LWJGLTileDrawable(this, x, y, r,
				loc.get(false), loc.get(true), clears));
	}

	public void relocateChit(CounterType ct, float f, float g) {
		board.relocateChit(ct, f, g);
	}
	
	public LWJGLTileDrawable get(TileName tl) {
		return tiles.get(tl);
	}

	@Override
	public void applyNodeTransformation(LWJGLGraphics gfx) {
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		gfx.getShaders().useShaderProgram(ShaderType.TILE_SHADER);
		textures.useTextures(gfx);
		// draw all tiles
		for (LWJGLTileDrawable tile : tiles.values()) {
			tile.draw(gfx);
		}
	}

	private void loadTileImages() throws IOException {
		ResourceHandler rh = board.getResourceHandler();
		for (TileName type : TileName.values()) {
			textureLocations
					.put(type,
							new EnchantedHolder<Integer>(textures
									.addImage(TileImages.getTileImage(rh, type,
											false)), textures
									.addImage(TileImages.getTileImage(rh, type,
											true))));
		}
		textures.loadImages();
	}

	private Map<TileName, EnchantedHolder<Integer>> textureLocations;
	private Map<TileName, LWJGLTileDrawable> tiles;
	private LWJGLTextureArrayLoader textures;
	private LWJGLBoardDrawable board;

}
