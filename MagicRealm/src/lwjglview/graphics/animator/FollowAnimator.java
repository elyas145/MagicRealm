package lwjglview.graphics.animator;

import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import utils.math.linear.Matrix;
import utils.time.Timing;

public class FollowAnimator extends Animator {

	public FollowAnimator(Matrix cPos, MatrixCalculator position, float vel) {
		currentPosition = cPos;
		calculator = position;
		speed = vel;
		lastTime = Timing.getSeconds();
		bufferNx1 = Matrix.clone(currentPosition);
		bufferN1xN1 = Matrix.translation(bufferNx1);
	}
	
	public void changeFocus(MatrixCalculator mc) {
		calculator = mc;
	}

	@Override
	public boolean isFinished() {
		return currentPosition.equals(calculator.calculateMatrix());
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
			bufferN1xN1.translate(currentPosition);
			return bufferN1xN1;
		}
		Matrix pos = calculator.calculateMatrix();
		Matrix diff = bufferNx1;
		if((pos == null) || (currentPosition == null) || (diff == null)) {
			int i = 5*4;
			i *= 2;
		}
		pos.subtract(currentPosition, diff);
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
		currentPosition.add(delt, currentPosition);
		bufferN1xN1.translate(currentPosition);
		return bufferN1xN1;
	}

	private Matrix currentPosition;
	private Matrix bufferNx1;
	private Matrix bufferN1xN1;
	private MatrixCalculator calculator;
	private float lastTime;
	private float speed;
	private boolean paused;

}
