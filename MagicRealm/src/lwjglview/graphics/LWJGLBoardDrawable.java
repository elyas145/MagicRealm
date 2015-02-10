package lwjglview.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import config.GraphicsConfiguration;
import model.board.Board;
import model.board.TileType;
import utils.resources.ResourceHandler;
import utils.resources.TileImages;
import view.graphics.BoardDrawable;

public class LWJGLBoardDrawable extends BoardDrawable {

	protected LWJGLBoardDrawable(Board bo, ResourceHandler rh) throws IOException {
		super(bo);
		resources = rh;
		int[][][] rawData = new int
				[TileType.values().length * 2]
						[GraphicsConfiguration.TILE_HEIGHT]
								[GraphicsConfiguration.TILE_WIDTH];
		int i = readData(0, false);
		readData(i, true);
	}
	
	private int readData(int i, boolean enchanted) throws IOException {
		for(TileType type: TileType.values()) {
			int[][] buf = rawData[i];
			BufferedImage img = TileImages.getTileImage(resources, type, enchanted);
			for(int y = 0; y < buf.length; ++y) {
				int[] line = buf[y];
				for(int x = 0; x < line.length; ++x) {
					line[x] = img.getRGB(x, y);
				}
			}
		}
		return i;
	}
	
	private int[][][] rawData;
	private ResourceHandler resources;

}
