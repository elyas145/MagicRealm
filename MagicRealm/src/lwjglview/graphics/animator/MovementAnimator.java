package lwjglview.graphics.animator;

import utils.math.Mathf;
import utils.math.Matrix;

public abstract class MovementAnimator extends TimedAnimator {

	public MovementAnimator(float vel, float xinit, float yinit, float xfin,
			float yfin) {
		super(getTime(vel, xinit, yinit, xfin, yfin));
		initialPosition = Matrix.columnVector(new float[] { xinit, yinit, 0f });
		finalPosition = Matrix.columnVector(new float[] { xfin, yfin, 0f });
	}

	@Override
	protected Matrix calculateTransform() {
		float scale = getInterval();
		Matrix a = initialPosition.multiply(1f - scale);
		Matrix b = finalPosition.multiply(scale);
		return Matrix.translation(a.add(b));
	}

	private static float getTime(float vel, float xinit, float yinit,
			float xfin, float yfin) {
		return Mathf.length(xfin - xinit, yfin - yinit) / vel;
	}

	private Matrix initialPosition;
	private Matrix finalPosition;

}
