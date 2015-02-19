package lwjglview.graphics.animator;

import utils.math.Matrix;

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
