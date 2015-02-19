package lwjglview.graphics.animator.matrixcalculator;

import utils.math.Matrix;

public class MatrixFadeCalculator implements MatrixCalculator {
	
	public MatrixFadeCalculator(float fd, Matrix init, Matrix fin) {
		initialMatrix = init;
		finalMatrix = fin;
		fade = fd;
	}
	
	public void setFade(float fd) {
		fade = fd;
	}

	@Override
	public Matrix calculateMatrix() {
		Matrix a = initialMatrix.multiply(1f - fade);
		Matrix b = finalMatrix.multiply(fade);
		return a.add(b);
	}
	
	private float fade;
	private Matrix initialMatrix;
	private Matrix finalMatrix;

}
