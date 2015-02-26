package lwjglview.graphics;

import utils.math.linear.Matrix;
import lwjglview.graphics.animator.matrixcalculator.MatrixCalculator;
import lwjglview.graphics.animator.matrixcalculator.StaticMatrixCalculator;

public abstract class LWJGLDrawableNode extends LWJGLDrawable {
	
	public abstract void updateNodeUniforms(LWJGLGraphics gfx);
	
	public Matrix getTransformation() {
		return buffered;
	}
	
	public LWJGLDrawable getParent() {
		return parent;
	}
	
	public void setParent(LWJGLDrawableNode par) {
		parent = par;
	}
	
	@Override
	public final void updateUniforms(LWJGLGraphics gfx) {
		if(parent != null) {
			parent.updateUniforms(gfx);
		}
		updateNodeUniforms(gfx);
	}
	
	protected LWJGLDrawableNode(LWJGLDrawableNode par) {
		parent = par;
		buffered = Matrix.identity(4);
		transformation = new StaticMatrixCalculator(Matrix.identity(4));
	}
	
	protected LWJGLDrawableNode(LWJGLDrawableNode par, MatrixCalculator calc) {
		parent = par;
		buffered = Matrix.identity(4);
		transformation = calc;
	}
	
	protected LWJGLDrawableNode(LWJGLDrawableNode par, Matrix mat) {
		parent = par;
		buffered = Matrix.identity(4);
		transformation = new StaticMatrixCalculator(mat);
	}

	protected void setCalculator(MatrixCalculator calc) {
		transformation = calc;
	}
	
	protected final void updateTransformation() {
		Matrix mat = transformation.calculateMatrix();
		if(parent != null) {
			parent.getTransformation().multiply(mat, buffered);
		}
		else {
			buffered.copyFrom(mat);
		}
	}
	
	private LWJGLDrawableNode parent;
	private MatrixCalculator transformation;
	private Matrix buffered;
}
