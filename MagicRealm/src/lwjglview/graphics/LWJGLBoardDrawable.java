package lwjglview.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import config.GraphicsConfiguration;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.board.Board;
import model.board.HexTile;
import model.board.enums.TileType;
import utils.resources.ResourceHandler;
import utils.resources.TileImages;
import view.graphics.BoardDrawable;
import view.graphics.Graphics;

public class LWJGLBoardDrawable extends BoardDrawable {

	public LWJGLBoardDrawable(Board bo, ResourceHandler rh) throws IOException {
		super(bo);
		normal = new HashMap<TileType, BufferedImage>();
		enchanted = new HashMap<TileType, BufferedImage>();
		resources = rh;
		numTiles = TileType.values().length * 2;
		height = GraphicsConfiguration.TILE_HEIGHT;
		width = GraphicsConfiguration.TILE_WIDTH;
		rawData = ByteBuffer.allocateDirect(numTiles * height * width * 4);
		System.out.println("Loading tile images");
		index = 0;
		readImages(false);
		readImages(true);
		System.out.println("Finished loading images");
		tiles = new HashSet<LWJGLTileDrawable>();
		for(HexTile ht: bo) {
			TileType type = ht.getType();
			tiles.add(new LWJGLTileDrawable(ht, getTextureIndex(type, false),
					getTextureIndex(type, true)));
		}
		location = -1;
	}
	
	@Override
	public void draw(Graphics gfx) {
		LWJGLGraphics lwgfx = (LWJGLGraphics) gfx;
		if(location < 0) {
			loadTextures(lwgfx);
		}
		lwgfx.bindTextureArray(location);
		GLShaderHandler shaders = lwgfx.getShaders();
		ShaderType st = ShaderType.TILE_SHADER;
		if(!shaders.hasProgram(st)) {
			try {
				shaders.loadShaderProgram(st);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		shaders.useShaderProgram(st);
		shaders.setUniformFloatValue(st, "time", System.nanoTime() * 1e-9f);
		for(LWJGLTileDrawable tile: tiles) {
			tile.draw(lwgfx);
		}
	}
	
	private int getTextureIndex(TileType type, boolean enchanted) {
		int i = 0;
		for(TileType tt: TileType.values()) {
			if(tt == type) {
				return enchanted ? i + TileType.values().length : i;
			}
			++i;
		}
		return -1;
	}
	
	private void loadTextures(LWJGLGraphics gfx) {
		location = gfx.loadTextureArray(rawData, numTiles, height, width);
	}
	
	private ByteBuffer readImages(boolean enchanted) throws IOException {
		for(TileType type: TileType.values()) {
			BufferedImage img = TileImages.getTileImage(resources, type, enchanted);
			for(int y = 0; y < height; ++y) {
				for(int x = 0; x < width; ++x) {
					int i = img.getRGB(x, y);
					rawData.putInt(index, i);
					index += 4;
				}
			}
		}
		System.out.println(rawData);
		return rawData;
	}
	
	private int index;
	private int numTiles;
	private int height;
	private int width;
	private int location;
	private ByteBuffer rawData;
	private ResourceHandler resources;
	private Collection<LWJGLTileDrawable> tiles;
	private Map<TileType, BufferedImage> normal;
	private Map<TileType, BufferedImage> enchanted;

}
