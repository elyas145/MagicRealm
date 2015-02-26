package lwjglview.graphics.animator;

import utils.math.linear.Matrix;
import utils.time.Timing;

public abstract class TimedAnimator extends Animator {

	// implement apply and whenFinished for a full class
	
	public TimedAnimator(float time) {
		paused = true;
		length = time;
		currentTime = Timing.getSeconds();
		pauseTime = currentTime;
		endTime = pauseTime + length;
	}

	@Override
	public boolean isFinished() {
		return paused ? pauseTime > endTime : currentTime > endTime;
	}

	@Override
	public void pause() {
		paused = true;
		pauseTime = Timing.getSeconds();
	}

	@Override
	public void resume() {
		if (paused) {
			paused = false;
			currentTime = Timing.getSeconds();
			endTime += currentTime - pauseTime;
		}
	}
	
	@Override
	public Matrix apply() {
		currentTime = Timing.getSeconds();
		return super.apply();
	}

	protected float getInterval() {
		if(paused) {
			return 1f - (endTime - pauseTime) / length;
		}
		if(isFinished()) {
			return 1f;
		}
		return 1f - (endTime - currentTime) / length;
	}
	
	protected float getLength() {
		return length;
	}

	private float length;
	private float currentTime;
	private float endTime;
	private float pauseTime;
	private boolean paused;

}
