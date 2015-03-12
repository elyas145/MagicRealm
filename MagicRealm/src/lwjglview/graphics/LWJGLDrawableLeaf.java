package lwjglview.graphics;

import utils.math.linear.Matrix;
import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;

public class LWJGLDrawableLeaf extends LWJGLDrawableNode {

	public LWJGLDrawableLeaf(LWJGLDrawableNode par, LWJGLDrawable draw) {
		super(par);
		drawable = draw;
	}

	public LWJGLDrawableLeaf(LWJGLDrawableNode par, Matrix matr, LWJGLDrawable draw) {
		super(par, matr);
		drawable = draw;
	}
	
	public LWJGLDrawableLeaf(LWJGLDrawableNode par, MatrixCalculator matr, LWJGLDrawable draw) {
		super(par, matr);
		drawable = draw;
	}
	
	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}
	
	@Override
	public final void draw(LWJGLGraphics gfx) {
		updateTransformation();
		gfx.resetModelMatrix();
		gfx.applyModelTransform(getTransformation());
		updateUniforms(gfx);
		drawable.updateUniforms(gfx);
		drawable.draw(gfx);
	}
	
	private LWJGLDrawable drawable;

}
