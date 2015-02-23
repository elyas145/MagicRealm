package lwjglview.graphics.animator;

import utils.math.Matrix;

public class MovementAnimator extends FadeAnimator {

	public MovementAnimator(float vel, Matrix init, Matrix fin) {
		super(getTime(vel, init, fin), init, fin);
	}
	
	@Override
	public Matrix calculateTransform() {
		return Matrix.translation(super.calculateTransform());
	}

	private static float getTime(float vel, Matrix init,
			Matrix fin) {
		return init.subtract(fin).length() / vel;
	}

}
