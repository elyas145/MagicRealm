package lwjglview.controller.board.mapchit;

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
import lwjglview.graphics.counters.LWJGLCounterDrawable;
import lwjglview.graphics.counters.LWJGLCounterLocator;
import lwjglview.graphics.shader.ShaderType;
import lwjglview.graphics.textures.LWJGLTextureArrayLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import model.counter.chit.MapChit;

public class LWJGLMapChitCollection extends LWJGLDrawableNode {

	private static final Color BLANK = GraphicsConfiguration.MAP_CHIT_HIDE_COLOUR;
	private static final Color WHITE = GraphicsConfiguration.MAP_CHIT_WHITE_COLOUR;
	private static final Color RED = GraphicsConfiguration.MAP_CHIT_RED_COLOUR;
	private static final Color ORANGE = GraphicsConfiguration.MAP_CHIT_ORANGE_COLOUR;
	private static final Color YELLOW = GraphicsConfiguration.MAP_CHIT_YELLOW_COLOUR;

	public LWJGLMapChitCollection(LWJGLCounterLocator par, ResourceHandler rh,
			Iterable<MapChit> avail) {
		super(par);
		locations = par;
		resources = rh.getCounterGenerator();
		mapChits = new HashMap<MapChit, LWJGLCounterDrawable>();
		drawables = new HashSet<LWJGLCounterDrawable>();
		textureLocations = new HashMap<MapChit, LWJGLTextureLoader>();
		textures = new LWJGLTextureArrayLoader(
				GraphicsConfiguration.IMAGE_SCALE_WIDTH,
				GraphicsConfiguration.IMAGE_SCALE_HEIGHT);
		loadImages(avail);
	}

	public LWJGLCounterDrawable get(MapChit mc) {
		for(MapChit c : mapChits.keySet()){
			if(c.equals(mc)){
				return mapChits.get(c);
			}
		}
		return null;
	}

	public LWJGLCounterDrawable create(MapChit chit) {
		LWJGLCounterDrawable drbl = resources.generate(chit, locations, BLANK);
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
		Color c;
		switch(chit.getType().type()){
		case SOUND:
			c = RED;
			break;
		case SITE:
			c=ORANGE;
			break;
		case LOST_CITY:
		case LOST_CASTLE:
			c=RED;
			break;
		case WARNING:
			c=YELLOW;
			break;
		default:
			c=WHITE;
		}
		get(chit).changeColour(c);
	}

	public void replace(MapChit chit, Iterable<MapChit> replacements) {
		for(MapChit m : mapChits.keySet()){
			if(chit.equals(m)){
				get(m).setVisible(false);
			}
		}		
		for (MapChit mc : replacements) {
			for(MapChit uc : mapChits.keySet()){
				if(mc.equals(uc)){
					get(uc).setVisible(true);
				}
			}
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
	
	public void setVisible(MapChit mc, boolean vis) {
		get(mc).setVisible(vis);
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
		textureLocations.put(mc, textures.addImage(new MapChitImageGenerator(mc)));
	}
	
	private LWJGLCounterGenerator resources;
	
	private LWJGLCounterLocator locations;
	private LWJGLTextureArrayLoader textures;
	private Map<MapChit, LWJGLCounterDrawable> mapChits;
	private Set<LWJGLCounterDrawable> drawables;
	private Map<MapChit, LWJGLTextureLoader> textureLocations;

}
