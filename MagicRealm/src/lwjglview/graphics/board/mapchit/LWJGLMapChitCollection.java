package lwjglview.graphics.board.mapchit;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
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

	private static final Color BLANK = GraphicsConfiguration.MAP_CHIT_HIDE_COLOUR;
	private static final Color SHOW = GraphicsConfiguration.MAP_CHIT_HIDE_COLOUR;

	public LWJGLMapChitCollection(LWJGLBoardDrawable par,
			Iterable<MapChit> avail) {
		super(par);
		board = par;
		mapChits = new HashMap<MapChitType, Map<Character, LWJGLCounterDrawable>>();
		iter = new ChitIterator();
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
		typeMap.put(identifier, new LWJGLCounterDrawable(board, repr,
				textureLocations.get(type).get(identifier), BLANK));
		return get(type, identifier);
	}

	public boolean contains(MapChit mc) {
		return contains(mc.getType(), mc.getIdentifier());
	}

	public boolean contains(MapChitType mct, char identifier) {
		if (mapChits.containsKey(mct)) {
			return mapChits.get(mct).containsKey(identifier);
		}
		return false;
	}

	public void hideAll() {
		synchronized (iter) {
			while (iter.hasNext()) {
				iter.next().changeColour(BLANK);
			}
			iter.remove();
		}
	}

	public void reveal(MapChit chit) {
		get(chit).changeColour(SHOW);
	}

	public void replace(MapChit chit, Iterable<MapChit> replacements) {
		LWJGLCounterDrawable count = get(chit);
		LWJGLDrawableNode repr = count.getRepresentation();
		remove(chit);
		for (MapChit mc : replacements) {
			create(mc, repr);
		}
	}

	public void remove(MapChit chit) {
		remove(chit.getType(), chit.getIdentifier());
	}

	public void remove(MapChitType type, char id) {
		LWJGLCounterDrawable drbl;
		synchronized (mapChits) {
			Map<Character, LWJGLCounterDrawable> row = mapChits.get(type);
			drbl = row.get(id);
			row.remove(id);
		}
		if(drbl != null) {
			drbl.forget();
		}
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
		synchronized (mapChits) {
			for (Map<Character, LWJGLCounterDrawable> row : mapChits.values()) {
				for (LWJGLCounterDrawable chit : row.values()) {
					chit.draw(gfx);
				}
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
		typeLocations.put(mcid, textures.addImage(new ChitGenerator(mc,
				imageWidth, imageHeight)));
	}

	private class ChitIterator implements Iterator<LWJGLCounterDrawable> {
		@Override
		public boolean hasNext() {
			if (row == null) {
				row = mapChits.values().iterator();
				if (row.hasNext()) {
					items = row.next().values().iterator();
					return items.hasNext();
				}
				return false;
			}
			while (row.hasNext()) {
				if (items.hasNext()) {
					return true;
				}
				items = row.next().values().iterator();
			}
			return false;
		}

		@Override
		public LWJGLCounterDrawable next() {
			return items.next();
		}

		@Override
		public void remove() {
			if (items != null) {
				items.remove();
			}
			if (row != null) {
				row.remove();
			}
		}

		private Iterator<Map<Character, LWJGLCounterDrawable>> row;
		private Iterator<LWJGLCounterDrawable> items;
	}

	private ChitIterator iter;
	private int imageWidth;
	private int imageHeight;
	private LWJGLBoardDrawable board;
	private LWJGLTextureArrayLoader textures;
	private Map<MapChitType, Map<Character, LWJGLCounterDrawable>> mapChits;
	private Map<MapChitType, Map<Character, Integer>> textureLocations;

}
