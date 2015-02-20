package lwjglview.graphics;

import utils.math.Matrix;
import view.graphics.Drawable;
import view.graphics.Graphics;

public class TransformationDrawable extends LWJGLDrawableNode {

	public TransformationDrawable(LWJGLDrawable draw, Matrix transform) {
		drawable = draw;
		transformation = transform;
	}
	
	@Override
	public void applyTransformation(LWJGLGraphics gfx) {
		gfx.applyModelTransform(transformation);
	}

	@Override
	public void updateUniforms(LWJGLGraphics gfx) {
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		drawChild(gfx, drawable);
	}

	private LWJGLDrawable drawable;
	private Matrix transformation;

}
