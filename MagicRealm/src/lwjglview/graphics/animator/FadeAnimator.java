package lwjglview.graphics.animator;

import lwjglview.graphics.animator.matrixcalculator.MatrixFadeCalculator;
import utils.math.linear.Matrix;

public class FadeAnimator extends TimedAnimator {

	public FadeAnimator(float time, Matrix init, Matrix fin) {
		super(time);
		fader = new MatrixFadeCalculator(0f, init, fin);
	}

	@Override
	public void finish() {
	}

	@Override
	protected Matrix calculateTransform() {
		fader.setFade(getInterval());
		return fader.calculateMatrix();
	}

	private MatrixFadeCalculator fader;
}
