package utils.resources;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import utils.math.Mathf;
import utils.math.linear.Matrix;
import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLDrawableLeaf;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.TransformationDrawable;
import lwjglview.graphics.counters.LWJGLCounterDrawable;
import lwjglview.graphics.counters.LWJGLCounterLocator;
import lwjglview.graphics.model.ModelData;
import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import model.counter.chit.MapChit;
import model.enums.CounterType;

public class LWJGLCounterGenerator {

	public LWJGLCounterGenerator(ResourceHandler rh) {
		resources = rh;
		counters = new HashMap<CounterType, CounterData>();
		models = new HashMap<String, LWJGLDrawableNode>();
		mapChitTextures = new HashMap<MapChit, LWJGLTextureLoader>();
	}

	public LWJGLCounterDrawable generate(CounterType counter,
			LWJGLCounterLocator par) {
		CounterData data = counters.get(counter);
		if (data == null) {
			try {
				LWJGLTextureLoader tex = getTexture(counter);
				LWJGLDrawableNode md = getModel(counter);
				data = new CounterData(md, tex);
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
		return new LWJGLCounterDrawable(par, data.modelData, data.texture, Color.WHITE);
	}
	
	public LWJGLCounterDrawable generate(MapChit mc, LWJGLCounterLocator par) {
		return generate(mc, par, Color.WHITE);
	}

	public LWJGLCounterDrawable generate(MapChit mc, LWJGLCounterLocator par, Color col) {
		CounterData data = getCounterData(CounterType.MAP_CHIT);
		LWJGLTextureLoader texture = mapChitTextures.get(mc);
		if(texture == null) {
			int w, h;
			w = GraphicsConfiguration.IMAGE_SCALE_WIDTH;
			h = GraphicsConfiguration.IMAGE_SCALE_HEIGHT;
			texture = new LWJGLSingleTextureLoader(new MapChitImageGenerator(mc), w, h);
			mapChitTextures.put(mc, texture);
		}
		return new LWJGLCounterDrawable(par, data.modelData, texture, col);
	}
	
	private CounterData getCounterData(CounterType ct) {
		CounterData data = counters.get(CounterType.MAP_CHIT);
		if (data == null) {
			try {
				LWJGLDrawableNode md = getModel(CounterType.MAP_CHIT);
				data = new CounterData(md, null);
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
		return data;
	}

	private LWJGLTextureLoader getTexture(CounterType counter) {
		LWJGLSingleTextureLoader ret = new LWJGLSingleTextureLoader(resources,
				ResourceHandler.joinPath("counters", getSubDir(counter),
						getName(counter)));
		return ret;
	}

	private LWJGLDrawableNode getModel(CounterType ct) throws IOException {
		String path;
		switch (ct) {
		case VALLEY_CHAPEL:
		case VALLEY_GUARD_HOUSE:
		case VALLEY_HOUSE:
		case VALLEY_INN:
			path = "circle_counter.obj";
			break;
		case CHARACTER_AMAZON:
		case CHARACTER_CAPTAIN:
		case CHARACTER_SWORDSMAN:
			if (GraphicsConfiguration.SIMPLE_COUNTERS) {
				path = "circle_counter.obj";
			} else {
				switch (ct) {
				case CHARACTER_AMAZON:
					path = "lara.obj";
					break;
				case CHARACTER_CAPTAIN:
					path = "captain.obj";
					break;
				case CHARACTER_SWORDSMAN:
					path = "knight4.obj";
					break;
				default:
					path = null;
				}
			}
			break;
		default:
			path = "square_counter.obj";
		}
		LWJGLDrawableNode node = models.get(path);
		if (node == null) {
			node = loadModel(ct, path);
			models.put(path, node);
		}
		return node;
	}

	private LWJGLDrawableNode loadModel(CounterType ct, String path)
			throws IOException {
		LWJGLDrawableNode ret = new LWJGLDrawableLeaf(null,
				ModelData.loadModelData(resources, path));
		if (!GraphicsConfiguration.SIMPLE_COUNTERS) {
			TransformationDrawable transform;
			switch (ct) {
			case CHARACTER_AMAZON:
				transform = new TransformationDrawable(null, ret, Matrix
						.dilation(1.3f, 1.3f, 1.3f, 1f).multiply(
								Matrix.rotationX(4, Mathf.PI * .5f)));
				break;
			case CHARACTER_CAPTAIN:
				transform = new TransformationDrawable(null, ret, Matrix
						.dilation(1.3f, 1.3f, 1.3f, 1f).multiply(
								Matrix.rotationX(4, Mathf.PI * .5f)));
				break;
			case CHARACTER_SWORDSMAN:
				transform = new TransformationDrawable(null, ret, Matrix
						.dilation(2.7f, 2.7f, 2.7f, 1f).multiply(
								Matrix.rotationX(4, Mathf.PI * .5f)));
				break;
			default:
				transform = null;
			}
			ret = transform;
		}
		return ret;
	}

	private static String getSubDir(CounterType counter) {
		switch (counter) {
		case CHARACTER_AMAZON:
		case CHARACTER_CAPTAIN:
		case CHARACTER_SWORDSMAN:
			return "characters";
		case VALLEY_CHAPEL:
		case VALLEY_GUARD_HOUSE:
		case VALLEY_HOUSE:
		case VALLEY_INN:
			return "sites";
		default:
			return ".";
		}
	}

	private static String getName(CounterType counter) {
		if (GraphicsConfiguration.SIMPLE_COUNTERS) {
			switch (counter) {
			case CHARACTER_AMAZON:
				return "amazon.png";
			case CHARACTER_CAPTAIN:
				return "captain.png";
			case CHARACTER_SWORDSMAN:
				return "swordsman.png";
			case VALLEY_CHAPEL:
				return "chapel.gif";
			case VALLEY_GUARD_HOUSE:
				return "guard.gif";
			case VALLEY_HOUSE:
				return "house.gif";
			case VALLEY_INN:
				return "inn.gif";
			default:
				return "penguin.png";
			}
		}
		switch (counter) {
		case CHARACTER_AMAZON:
			return "jungle.jpg";
		case CHARACTER_CAPTAIN:
			return "flag.jpg";
		case CHARACTER_SWORDSMAN:
			return "iron.jpg";
		case VALLEY_CHAPEL:
			return "chapel.gif";
		case VALLEY_GUARD_HOUSE:
			return "guard.gif";
		case VALLEY_HOUSE:
			return "house.gif";
		case VALLEY_INN:
			return "inn.gif";
		default:
			return "penguin.png";
		}
	}

	private class CounterData {
		public CounterData(LWJGLDrawableNode md, LWJGLTextureLoader img) {
			modelData = md;
			texture = img;
		}

		public final LWJGLDrawableNode modelData;
		public final LWJGLTextureLoader texture;
	}

	private ResourceHandler resources;
	private Map<CounterType, CounterData> counters;
	private Map<String, LWJGLDrawableNode> models;
	private Map<MapChit, LWJGLTextureLoader> mapChitTextures;

}
