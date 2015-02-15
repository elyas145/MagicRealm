package lwjglview.graphics.animator;

import utils.math.Matrix;
import view.graphics.Drawable;

public abstract class Animator {
	
	public abstract void start();
	
	public abstract Matrix apply();
	
	public abstract boolean isFinished();
	
	public abstract void pause();
	
	public abstract void resume();

}
