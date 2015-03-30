package lwjglview.graphics.board.counter;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.math.linear.Matrix;
import utils.resources.LWJGLCounterGenerator;
import utils.resources.ResourceHandler;
import lwjglview.graphics.LWJGLDrawableNode;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.counters.LWJGLCounterDrawable;
import lwjglview.graphics.counters.LWJGLCounterLocator;
import lwjglview.graphics.shader.ShaderType;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.ValleyChit;

public class LWJGLCounterCollection extends LWJGLDrawableNode {

	public LWJGLCounterCollection(LWJGLCounterLocator par, ResourceHandler res) throws IOException {
		super(par);
		locations = par;
		resources = res.getCounterGenerator();
		counters = new HashMap<CounterType, LWJGLCounterDrawable>();
		List<CounterType> counters = new ArrayList<CounterType>();
		for (CharacterType ct : CharacterType.values()) {
			counters.add(ct.toCounter());
		}
		for (ValleyChit vc : ValleyChit.values()) {
			counters.add(vc.toCounterType());
		}
	}

	public LWJGLCounterDrawable get(CounterType counter) {
		return counters.get(counter);
	}

	public LWJGLCounterDrawable create(CounterType tp) {
		counters.put(tp, resources.generate(tp, locations));
		return get(tp);
	}

	public boolean contains(CounterType ct) {
		return counters.containsKey(ct);
	}

	public void getVector(CounterType count, Matrix dest) {
		get(count).getVector(dest);
	}

	public boolean isAnimationFinished(CounterType ct) {
		return get(ct).isAnimationFinished();
	}

	public void moveTo(CounterType ct, Matrix pos) {
		get(ct).moveTo(pos);
	}

	public void changeColour(CounterType ct, Color col) {
		get(ct).changeColour(col);
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		updateTransformation();
		gfx.getShaders().useShaderProgram(ShaderType.CHIT_SHADER);
		// draw all counters
		for (LWJGLCounterDrawable counter : counters.values()) {
			counter.draw(gfx);
		}
	}

	private LWJGLCounterLocator locations;
	private Map<CounterType, LWJGLCounterDrawable> counters;
	private LWJGLCounterGenerator resources;

}
