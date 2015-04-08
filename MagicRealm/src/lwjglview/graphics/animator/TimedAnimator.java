package lwjglview.graphics.animator;

public abstract class TimedAnimator extends TimeAnimator {

	// implement apply and whenFinished for a full class
	
	public TimedAnimator(float time) {
		super(1f);
		length = time;
	}

	@Override
	public boolean isFinished() {
		return getTime() > length;
	}

	protected float getInterval() {
		if(isFinished()) {
			return 1f;
		}
		return getTime() / length;
	}
	
	protected float getLength() {
		return length;
	}

	private float length;

}
