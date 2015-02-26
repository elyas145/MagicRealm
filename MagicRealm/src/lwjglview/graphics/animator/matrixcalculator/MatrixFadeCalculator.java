package lwjglview.graphics.animator.matrixcalculator;

import utils.math.linear.Matrix;

public class MatrixFadeCalculator implements MatrixCalculator {
	
	public MatrixFadeCalculator(float fd, Matrix init, Matrix fin) {
		int rows = init.rowCount();
		matrix = Matrix.empty(rows, 2);
		for(int i = 0; i < rows; ++i) {
			matrix.set(i, 0, init.get(i, 0));
			matrix.set(i, 1, fin.get(i, 0));
		}
		buffer = Matrix.columnVector(1f - fade, fade);
		calc = Matrix.clone(init);
		fade = fd;
	}
	
	public void setFade(float fd) {
		fade = fd;
	}

	@Override
	public Matrix calculateMatrix() {
		fade = fade < 0f ? 0f : fade;
		fade = fade > 1f ? 1f : fade;
		buffer.set(0, 0, 1f - fade);
		buffer.set(1, 0, fade);
		matrix.multiply(buffer, calc);
		return calc;
	}
	
	private float fade;
	private Matrix matrix;
	private Matrix buffer;
	private Matrix calc;
}
