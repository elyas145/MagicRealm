package lwjglview.graphics.animator;

import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import utils.math.Matrix;

public abstract class StaticAnimator extends Animator {
	
	public StaticAnimator(MatrixCalculator focus) {
		value = focus;
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	protected Matrix calculateTransform() {
		return value.calculateMatrix();
	}
	
	private MatrixCalculator value;

}
