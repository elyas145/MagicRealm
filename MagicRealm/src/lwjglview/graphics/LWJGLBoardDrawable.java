package lwjglview.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import config.GraphicsConfiguration;
import lwjglview.graphics.model.ModelData;
import lwjglview.graphics.shader.GLShaderHandler;
import lwjglview.graphics.shader.ShaderType;
import model.board.Board;
import model.board.HexTile;
import model.enums.TileType;
import utils.images.ImageTools;
import utils.math.Mathf;
import utils.math.Matrix;
import utils.resources.ResourceHandler;
import utils.resources.TileImages;
import utils.time.Timing;
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
		loadImages(false);
		loadImages(true);
		System.out.println("Finished loading images");
		System.out.println("Loading model data");
		chit = ModelData.loadModelData(resources, "chit.obj");
		System.out.println("Finished loading model data");
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
		float ar = lwgfx.getAspectRatio();
		float fovScale = 1f;
		shaders.setUniformFloatValue(st, "xScale", 1f / fovScale / ar);
		shaders.setUniformFloatValue(st, "yScale", 1f / fovScale);
		shaders.setUniformFloatValue(st, "nearRadius", .1f);
		shaders.setUniformFloatValue(st, "oneOverRadiusDifference", 1f/20f);
		
		lwgfx.resetViewMatrix();
		float time = Timing.getSeconds() * .6f;
		Matrix tmp = Matrix.rotationX(4, Mathf.PI / 5f);
		float k = (Mathf.sin(time * .6f) + 3f) / 3f;
		tmp = Matrix.translation(new float[] {
				0f, -4f * k, 3f * k
		}).multiply(tmp);
		tmp = Matrix.rotationZ(4, time * .3f).multiply(tmp);
		lwgfx.applyCameraTransform(tmp);
		lwgfx.translateCamera(3.5f, -3.5f, 0f);
		
		for(LWJGLTileDrawable tile: tiles) {
			tile.draw(lwgfx);
		}
		
		lwgfx.resetModelMatrix();
		//lwgfx.rotateModelX(Mathf.PI / 2f);
		lwgfx.updateModelViewUniform(st, "modelViewMatrix");
		chit.draw(gfx);
		
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
	
	private void loadImages(boolean enchanted) throws IOException {
		for(TileType type: TileType.values()) {
			BufferedImage img = TileImages.getTileImage(resources, type, enchanted);
			index = ImageTools.loadRawImage(img, index, width, height, rawData);
		}
	}
	
	private int index;
	private int numTiles;
	private int height;
	private int width;
	private int location;
	private ByteBuffer rawData;
	private ResourceHandler resources;
	private Collection<LWJGLTileDrawable> tiles;
	private ModelData chit;
	private Map<TileType, BufferedImage> normal;
	private Map<TileType, BufferedImage> enchanted;

}
