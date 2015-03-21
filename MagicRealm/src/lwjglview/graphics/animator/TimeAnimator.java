package lwjglview.graphics.animator;

import utils.math.linear.Matrix;
import utils.time.Timing;

public abstract class TimeAnimator extends Animator {
	
	public TimeAnimator(float ts) {
		setTimeScale(ts);
		accumTime = 0f;
		currentTime = Timing.getSeconds();
		paused = true;
	}
	
	public void setTimeScale(float ts) {
		timeScale = ts;
	}
	
	@Override
	public Matrix apply() {
		if(!paused) {
			float time = Timing.getSeconds();
			accumTime += (time - currentTime) * timeScale;
			currentTime = time;
		}
		return super.apply();
	}

	@Override
	public void pause() {
		paused = true;
	}

	@Override
	public void resume() {
		currentTime = Timing.getSeconds();
		paused = false;
	}

	protected float getTime() {
		return accumTime;
	}
	
	private float timeScale;
	private float currentTime;
	private float accumTime;
	private boolean paused;

}
