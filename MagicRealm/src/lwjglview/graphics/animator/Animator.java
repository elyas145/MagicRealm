package lwjglview.graphics.animator;

import utils.math.Matrix;
import view.graphics.Drawable;

public abstract class Animator {
	
	public abstract boolean isFinished();
	
	public abstract void pause();
	
	public abstract void resume();
	
	public abstract void finish();
	
	public void start() {
		resume();
	}
	
	public Matrix apply() {
		Matrix ret = calculateTransform();
		if(isFinished()) {
			finish();
		}
		return ret;
	}
	
	protected abstract Matrix calculateTransform();

}
