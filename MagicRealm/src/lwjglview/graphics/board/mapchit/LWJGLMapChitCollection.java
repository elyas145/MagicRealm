package lwjglview.graphics.board.mapchit;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import config.GraphicsConfiguration;
import utils.math.Matrix;
import utils.resources.ChitGenerator;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.LWJGLTextureArrayLoader;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.graphics.board.LWJGLCounterDrawable;
import lwjglview.graphics.shader.ShaderType;
import model.counter.chit.MapChit;
import model.enums.MapChitType;

public class LWJGLMapChitCollection extends LWJGLDrawableNode {

	private static final Color BLANK = new Color(1f, 1f, 1f, 0f);

	public LWJGLMapChitCollection(LWJGLBoardDrawable par,
			Iterable<MapChit> avail) {
		super(par);
		board = par;
		mapChits = new HashMap<MapChitType, Map<Character, LWJGLCounterDrawable>>();
		textureLocations = new HashMap<MapChitType, Map<Character, Integer>>();
		textures = new LWJGLTextureArrayLoader(
				GraphicsConfiguration.IMAGE_SCALE_WIDTH,
				GraphicsConfiguration.IMAGE_SCALE_HEIGHT);
		imageWidth = GraphicsConfiguration.IMAGE_SCALE_WIDTH;
		imageHeight = GraphicsConfiguration.IMAGE_SCALE_HEIGHT;
		loadImages(avail);
	}

	public LWJGLCounterDrawable get(MapChit mc) {
		return get(mc.getType(), mc.getIdentifier());
	}

	public LWJGLCounterDrawable get(MapChitType type, char identifier) {
		return mapChits.get(type).get(identifier);
	}

	public LWJGLCounterDrawable create(MapChit chit, LWJGLDrawableNode repr) {
		return create(chit.getType(), chit.getIdentifier(), repr);
	}

	public LWJGLCounterDrawable create(MapChitType type, char identifier,
			LWJGLDrawableNode repr) {
		if (!mapChits.containsKey(type)) {
			mapChits.put(type, new HashMap<Character, LWJGLCounterDrawable>());
		}
		Map<Character, LWJGLCounterDrawable> typeMap = mapChits.get(type);
		typeMap.put(identifier,
				new LWJGLCounterDrawable(board, repr,
						textureLocations.get(type).get(identifier), BLANK));
		return get(type, identifier);
	}

	public boolean contains(MapChit mc) {
		return contains(mc.getType(), mc.getIdentifier());
	}

	public boolean contains(MapChitType mct, char identifier) {
		if(mapChits.containsKey(mct)) {
			return mapChits.get(mct).containsKey(identifier);
		}
		return false;
	}

	public Matrix getVector(MapChit mc) {
		return get(mc).getVector();
	}

	public Matrix getVector(MapChitType mct, char identifier) {
		return get(mct, identifier).getVector();
	}

	public void changeColour(MapChit mc, Color col) {
		get(mc).changeColour(col);
	}

	public void changeColour(MapChitType mct, char id, Color col) {
		get(mct, id).changeColour(col);
	}
	
	public int getID(MapChit mc) {
		return get(mc).getID();
	}
	
	public int getID(MapChitType mct, char id) {
		return get(mct, id).getID();
	}

	@Override
	public void applyNodeTransformation(LWJGLGraphics gfx) {
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		gfx.getShaders().useShaderProgram(ShaderType.CHIT_SHADER);
		textures.useTextures(gfx);
		for(Map<Character, LWJGLCounterDrawable> row: mapChits.values()) {
			for(LWJGLCounterDrawable chit: row.values()) {
				chit.draw(gfx);
			}
		}
	}

	private void loadImages(Iterable<MapChit> mapChits) {
		for (MapChit mc : mapChits) {
			loadImage(mc);
		}
		textures.loadImages();
	}

	private void loadImage(MapChit mc) {
		MapChitType mct = mc.getType();
		if (!textureLocations.containsKey(mct)) {
			textureLocations.put(mct, new HashMap<Character, Integer>());
		}
		Map<Character, Integer> typeLocations = textureLocations.get(mct);
		char mcid = mc.getIdentifier();
		typeLocations.put(mcid, textures.addImage(new ChitGenerator(mc, imageWidth, imageHeight)));
	}

	private int imageWidth;
	private int imageHeight;
	private LWJGLBoardDrawable board;
	private LWJGLTextureArrayLoader textures;
	private Map<MapChitType, Map<Character, LWJGLCounterDrawable>> mapChits;
	private Map<MapChitType, Map<Character, Integer>> textureLocations;

}
