package lwjglview.graphics.animator;

import utils.time.Timing;

public abstract class TimedAnimator extends Animator {

	// implement apply and whenFinished for a full class
	
	public TimedAnimator(float time) {
		paused = true;
		length = time;
		pauseTime = Timing.getSeconds();
		endTime = pauseTime + length;
	}
	
	public void start() {
		resume();
	}

	public boolean isFinished() {
		return paused ? pauseTime > endTime : Timing.getSeconds() > endTime;
	}

	public void pause() {
		paused = true;
		pauseTime = Timing.getSeconds();
	}

	public void resume() {
		if (paused) {
			paused = false;
			endTime += Timing.getSeconds() - pauseTime;
		}
	}

	protected float getInterval() {
		if(paused) {
			return 1f - (endTime - pauseTime) / length;
		}
		return 1f - (endTime - Timing.getSeconds()) / length;
	}
	
	protected float getLength() {
		return length;
	}

	private float length;
	private float endTime;
	private float pauseTime;
	private boolean paused;

}
