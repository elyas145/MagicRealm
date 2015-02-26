package lwjglview.graphics;

import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import lwjglview.graphics.animator.matrixcalculator.StaticMatrixCalculator;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import utils.math.linear.Matrix;

public class TransformationDrawable extends LWJGLDrawableNode {

	public TransformationDrawable(LWJGLDrawableNode parent, LWJGLDrawable draw, MatrixCalculator transform) {
		super(parent, transform);
		drawable = draw;
	}

	public TransformationDrawable(LWJGLBoardDrawable parent,
			LWJGLDrawableNode draw, Matrix transform) {
		super(parent, new StaticMatrixCalculator(transform));
		drawable = draw;
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		updateTransformation();
		drawable.draw(gfx);
	}

	private LWJGLDrawable drawable;

}
