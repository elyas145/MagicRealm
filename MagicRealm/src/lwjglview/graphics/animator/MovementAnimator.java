package lwjglview.graphics.animator;

import utils.math.linear.Matrix;

public class MovementAnimator extends FadeAnimator {

	public MovementAnimator(float vel, Matrix init, Matrix fin) {
		super(getTime(vel, init, fin), init, fin);
		buffer = Matrix.translation(init);
	}
	
	@Override
	public Matrix calculateTransform() {
		buffer.translate(super.calculateTransform());
		return buffer;
	}

	private static float getTime(float vel, Matrix init,
			Matrix fin) {
		init.subtract(fin, init);
		float len = init.length();
		init.add(fin, init);
		return len / vel;
	}
	
	private Matrix buffer;

}
