package lwjglview.graphics;

import utils.math.Matrix;
import view.graphics.Drawable;
import view.graphics.Graphics;

public class TransformationDrawable extends LWJGLDrawableNode {

	public TransformationDrawable(LWJGLDrawable parent, LWJGLDrawable draw, Matrix transform) {
		super(parent);
		drawable = draw;
		transformation = transform;
	}
	
	@Override
	public void applyNodeTransformation(LWJGLGraphics gfx) {
		gfx.applyModelTransform(transformation);
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		drawChild(gfx, drawable);
	}

	private LWJGLDrawable drawable;
	private Matrix transformation;

}
