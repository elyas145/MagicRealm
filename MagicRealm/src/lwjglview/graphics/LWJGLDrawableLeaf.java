package lwjglview.graphics;

public class LWJGLDrawableLeaf extends LWJGLDrawableNode {

	public LWJGLDrawableLeaf(LWJGLDrawable par, LWJGLDrawable draw) {
		super(par);
		drawable = draw;
	}

	@Override
	public void applyNodeTransformation(LWJGLGraphics gfx) {
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}
	
	@Override
	public void draw(LWJGLGraphics gfx) {
		drawable.applyTransformation(gfx);
		applyTransformation(gfx);
		updateUniforms(gfx);
		drawable.updateUniforms(gfx);
		drawable.draw(gfx);
	}
	
	private LWJGLDrawable drawable;

}
