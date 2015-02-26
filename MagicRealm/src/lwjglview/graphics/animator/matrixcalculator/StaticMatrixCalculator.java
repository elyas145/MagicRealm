package lwjglview.graphics.animator.matrixcalculator;

import utils.math.linear.Matrix;

public class StaticMatrixCalculator implements MatrixCalculator {
	
	public StaticMatrixCalculator(Matrix focus) {
		value = focus;
	}

	@Override
	public Matrix calculateMatrix() {
		return value;
	}
	
	private Matrix value;

}
