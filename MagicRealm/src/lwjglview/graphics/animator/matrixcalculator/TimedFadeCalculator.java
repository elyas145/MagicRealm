package lwjglview.graphics.animator.matrixcalculator;

import utils.math.linear.Matrix;
import utils.time.Timing;

public class TimedFadeCalculator implements MatrixCalculator {
	
	public TimedFadeCalculator(float tm, Matrix st, Matrix en) {
		start = Matrix.clone(st);
		end = Matrix.clone(en);
		bufferA = Matrix.clone(start);
		bufferB = Matrix.clone(bufferA);
		timeScale = 1f / tm;
		startTime = Timing.getSeconds();
	}

	@Override
	public Matrix calculateMatrix() {
		float fade = (Timing.getSeconds() - startTime) * timeScale;
		if(fade > 1f) {
			fade = 1f;
		}
		start.multiply(1f - fade, bufferA);
		end.multiply(fade, bufferB);
		bufferA.add(bufferB, bufferA);
		return bufferA;
	}
	
	private Matrix start;
	private Matrix end;
	private Matrix bufferA;
	private Matrix bufferB;
	private float timeScale;
	private float startTime;

}
