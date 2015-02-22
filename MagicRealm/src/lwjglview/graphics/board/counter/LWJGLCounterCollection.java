package lwjglview.graphics.board.counter;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.GraphicsConfiguration;
import utils.math.Matrix;
import utils.resources.CounterImages;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.LWJGLTextureArrayLoader;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.graphics.shader.ShaderType;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.ValleyChit;

public class LWJGLCounterCollection extends LWJGLDrawableNode {

	public LWJGLCounterCollection(LWJGLBoardDrawable par) throws IOException {
		super(par);
		board = par;
		counters = new HashMap<CounterType, LWJGLCounterDrawable>();
		textureLocations = new HashMap<CounterType, Integer>();
		textures = new LWJGLTextureArrayLoader(
				GraphicsConfiguration.IMAGE_SCALE_WIDTH,
				GraphicsConfiguration.IMAGE_SCALE_HEIGHT);
		List<CounterType> counters = new ArrayList<CounterType>();
		for (CharacterType ct : CharacterType.values()) {
			counters.add(ct.toCounter());
		}
		for (ValleyChit vc : ValleyChit.values()) {
			counters.add(vc.toCounterType());
		}
		loadImages(counters);
	}

	public LWJGLCounterDrawable get(CounterType counter) {
		return counters.get(counter);
	}

	public LWJGLCounterDrawable create(CounterType tp, LWJGLDrawableNode repr) {
		counters.put(tp, new LWJGLCounterDrawable(board, tp, repr, textureLocations.get(tp)));
		return get(tp);
	}

	public boolean contains(CounterType ct) {
		return counters.containsKey(ct);
	}

	public Matrix getVector(CounterType count) {
		return get(count).getVector();
	}

	public boolean isAnimationFinished(CounterType ct) {
		return get(ct).isAnimationFinished();
	}

	public void moveTo(CounterType type, float x, float y) {
		get(type).moveTo(x, y);
	}

	public void changeColour(CounterType ct, Color col) {
		get(ct).changeColour(col);
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
		// draw all counters
		for (LWJGLCounterDrawable counter : counters.values()) {
			counter.draw(gfx);
		}
	}

	private void loadImages(Iterable<CounterType> counters) throws IOException {
		for (CounterType ct : counters) {
			loadImage(ct);
		}
		textures.loadImages();
	}

	private void loadImage(CounterType ct) throws IOException {
		textureLocations.put(
				ct,
				textures.addImage(CounterImages.getCounterImage(
						board.getResourceHandler(), ct)));
	}

	private Map<CounterType, Integer> textureLocations;
	private LWJGLBoardDrawable board;
	private LWJGLTextureArrayLoader textures;
	private Map<CounterType, LWJGLCounterDrawable> counters;

}
