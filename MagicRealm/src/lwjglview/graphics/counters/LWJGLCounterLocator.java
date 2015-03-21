package lwjglview.graphics.counters;

import lwjglview.graphics.LWJGLDrawableNode;
import utils.math.linear.Matrix;

public abstract class LWJGLCounterLocator extends LWJGLDrawableNode {

	protected LWJGLCounterLocator(LWJGLDrawableNode par) {
		super(par);
	}

	public abstract void getCounterPosition(LWJGLCounterDrawable cd, Matrix pos);

	public abstract int addCounterDrawable(LWJGLCounterDrawable counter);

	public abstract void removeCounterDrawable(LWJGLCounterDrawable cd);

}
