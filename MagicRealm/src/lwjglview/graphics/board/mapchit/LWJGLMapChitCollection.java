package lwjglview.graphics.board.mapchit;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import config.GraphicsConfiguration;
import utils.math.linear.Matrix;
import utils.resources.LWJGLCounterGenerator;
import utils.resources.MapChitImageGenerator;
import utils.resources.ResourceHandler;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.graphics.counters.LWJGLCounterDrawable;
import lwjglview.graphics.counters.LWJGLCounterLocator;
import lwjglview.graphics.shader.ShaderType;
import lwjglview.graphics.textures.LWJGLTextureArrayLoader;
import model.counter.chit.MapChit;
import model.enums.MapChitType;

public class LWJGLMapChitCollection extends LWJGLDrawableNode {

	private static final Color BLANK = GraphicsConfiguration.MAP_CHIT_HIDE_COLOUR;
	private static final Color SHOW = GraphicsConfiguration.MAP_CHIT_SHOW_COLOUR;

	public LWJGLMapChitCollection(LWJGLCounterLocator par, ResourceHandler rh,
			Iterable<MapChit> avail) {
		super(par);
		locations = par;
		resources = rh.getCounterGenerator();
		mapChits = new HashMap<MapChit, LWJGLCounterDrawable>();
		drawables = new HashSet<LWJGLCounterDrawable>();
		textureLocations = new HashMap<MapChitType, Map<Character, Integer>>();
		textures = new LWJGLTextureArrayLoader(
				GraphicsConfiguration.IMAGE_SCALE_WIDTH,
				GraphicsConfiguration.IMAGE_SCALE_HEIGHT);
		loadImages(avail);
	}

	public LWJGLCounterDrawable get(MapChit mc) {
		return mapChits.get(mc);
	}

	public LWJGLCounterDrawable create(MapChit chit) {
		LWJGLCounterDrawable drbl = resources.generate(chit, locations);
		synchronized (mapChits) {
			mapChits.put(chit, drbl);
		}
		synchronized (drawables) {
			drawables.add(drbl);
		}
		return drbl;
	}

	public boolean contains(MapChit mc) {
		synchronized (mapChits) {
			return mapChits.containsKey(mc);
		}
	}

	public void hideAll() {
		synchronized (drawables) {
			for (LWJGLCounterDrawable drbl : drawables) {
				drbl.changeColour(BLANK);
			}
		}
	}

	public void reveal(MapChit chit) {
		System.out.println("Changing color of " + chit + " to show");
		get(chit).changeColour(SHOW);
	}

	public void replace(MapChit chit, Iterable<MapChit> replacements) {
		LWJGLCounterDrawable count = get(chit);
		LWJGLDrawableNode repr = count.getRepresentation();
		remove(chit);
		for (MapChit mc : replacements) {
			create(mc);
		}
	}

	public void remove(MapChit chit) {
		LWJGLCounterDrawable drbl;
		synchronized (mapChits) {
			drbl = mapChits.remove(chit);
		}
		synchronized (drawables) {
			drawables.remove(drbl);
		}
		if (drbl != null) {
			drbl.forget();
		}
	}

	public void getVector(MapChit mc, Matrix dest) {
		get(mc).getVector(dest);
	}

	public void changeColour(MapChit mc, Color col) {
		get(mc).changeColour(col);
	}

	public int getID(MapChit mc) {
		return get(mc).getID();
	}

	public void show(MapChit mc) {
		get(mc).setVisible(true);
	}
	
	public void hide(MapChit mc) {
		get(mc).setVisible(false);
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		updateTransformation();
		gfx.getShaders().useShaderProgram(ShaderType.CHIT_SHADER);
		textures.useTextures(gfx);
		synchronized (drawables) {
			for (LWJGLCounterDrawable drbl : drawables) {
				drbl.draw(gfx);
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
		typeLocations.put(mcid, textures.addImage(new MapChitImageGenerator(mc)));
	}
	
	private LWJGLCounterGenerator resources;
	
	private LWJGLCounterLocator locations;
	private LWJGLTextureArrayLoader textures;
	private Map<MapChit, LWJGLCounterDrawable> mapChits;
	private Set<LWJGLCounterDrawable> drawables;
	private Map<MapChitType, Map<Character, Integer>> textureLocations;

}
