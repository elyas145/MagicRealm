package lwjglview.graphics.animator.matrixcalculator;

import utils.math.Matrix;

public class StaticTranslationCalculator extends StaticMatrixCalculator {
	
	public StaticTranslationCalculator(Matrix focus) {
		super(Matrix.translation(focus));
	}

}
