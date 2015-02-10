package lwjglview.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;

import config.GraphicsConfiguration;
import lwjglview.graphics.shader.ShaderType;
import model.board.Board;
import model.board.HexTile;
import model.board.TileType;
import utils.resources.ResourceHandler;
import utils.resources.TileImages;
import view.graphics.BoardDrawable;
import view.graphics.Graphics;

public class LWJGLBoardDrawable extends BoardDrawable {

	public LWJGLBoardDrawable(Board bo, ResourceHandler rh) throws IOException {
		super(bo);
		resources = rh;
		numTiles = TileType.values().length * 2;
		height = GraphicsConfiguration.TILE_HEIGHT;
		width = GraphicsConfiguration.TILE_WIDTH;
		rawData = new byte[numTiles*height*width*4];
		index = 0;
		readData(false);
		readData(true);
		tiles = new HashSet<LWJGLTileDrawable>();
		for(HexTile ht: bo) {
			int norm = getTileIndex(ht.getType(), false);
			int ench = getTileIndex(ht.getType(), true);
			tiles.add(new LWJGLTileDrawable(ht, norm, ench));
		}
	}
	
	@Override
	public void draw(Graphics gfx) {
		LWJGLGraphics lwgfx = (LWJGLGraphics) gfx;
		if(location < 0) {
			loadTextures(lwgfx);
		}
		lwgfx.bindTextureArray(location);
		lwgfx.loadShaderProgram(ShaderType.TILE_SHADER);
	}
	
	// get the index of the texture in the texture array
	private static int getTileIndex(TileType tile, boolean enchant) {
		int i = 0;
		for(TileType tt: TileType.values()) {
			if(tt == tile) {
				return enchant ? i + TileType.values().length : i;
			}
			++i;
		}
		return -1;
	}
	
	// load the textures and get the location
	// then free the memory
	private void loadTextures(LWJGLGraphics gfx) {
		location = gfx.loadTextureArray(rawData, numTiles, height, width);
		rawData = null;
	}
	
	// read the images into the rawData buffer
	private void readData(boolean enchanted) throws IOException {
		for(TileType type: TileType.values()) {
			BufferedImage img = TileImages.getTileImage(resources, type, enchanted);
			for(int y = 0; y < height; ++y) {
				for(int x = 0; x < width; ++x) {
					int i = img.getRGB(x, y);
					ByteBuffer.wrap(rawData, index, 4).putInt(i);
					index += 4;
				}
			}
		}
	}
	
	private int numTiles;
	private int height;
	private int width;
	private int index;
	private byte[] rawData;
	private int location;
	private ResourceHandler resources;
	private Collection<LWJGLTileDrawable> tiles;

}
