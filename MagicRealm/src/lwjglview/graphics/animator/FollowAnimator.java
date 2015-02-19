package lwjglview.graphics.animator;

import utils.math.Matrix;
import utils.time.Timing;

public class FollowAnimator extends Animator {

	public FollowAnimator(Matrix cPos, MatrixCalculator position, float vel) {
		currentPosition = cPos;
		calculator = position;
		speed = vel;
		lastTime = Timing.getSeconds();
	}
	
	public void changeFocus(MatrixCalculator mc) {
		calculator = mc;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void pause() {
		paused = true;
	}

	@Override
	public void resume() {
		paused = false;
		lastTime = Timing.getSeconds();
	}

	@Override
	public void finish() {
	}

	@Override
	protected Matrix calculateTransform() {
		if (paused) {
			return Matrix.translation(currentPosition);
		}
		Matrix pos = calculator.calculateMatrix();
		Matrix diff = pos.subtract(currentPosition);
		float ct = Timing.getSeconds();
		float dist = (ct - lastTime) * speed;
		lastTime = ct;
		float len = diff.length();
		Matrix delt;
		if (len > dist) {
			delt = diff.multiply(dist / len);
		} else {
			delt = diff;
		}
		Matrix tmp = currentPosition;
		currentPosition = tmp.add(delt);
		return Matrix.translation(tmp);
	}

	private Matrix currentPosition;
	private MatrixCalculator calculator;
	private float lastTime;
	private float speed;
	private boolean paused;

}