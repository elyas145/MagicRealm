package lwjglview.graphics.animator;

import utils.math.Matrix;

public class StaticTranslationCalculator extends StaticMatrixCalculator {
	
	public StaticTranslationCalculator(Matrix focus) {
		super(Matrix.translation(focus));
	}

}
