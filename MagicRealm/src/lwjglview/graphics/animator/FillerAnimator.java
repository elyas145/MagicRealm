package lwjglview.graphics.animator;

import utils.math.linear.Matrix;
import lwjglview.graphics.animator.matrixcalculator.StaticMatrixCalculator;

public class FillerAnimator extends StaticAnimator {

	public FillerAnimator(Matrix focus) {
		super(new StaticMatrixCalculator(focus));
	}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public void finish() {
	}

}
