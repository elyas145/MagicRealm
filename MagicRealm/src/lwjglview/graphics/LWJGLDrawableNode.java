package lwjglview.graphics;

public abstract class LWJGLDrawableNode extends LWJGLDrawable {
	
	protected void applyChildTransformation(LWJGLGraphics gfx, LWJGLDrawable child) {
		child.applyTransformation(gfx);
		applyTransformation(gfx);
	}
	
	protected void updateChildUniforms(LWJGLGraphics gfx, LWJGLDrawable child) {
		updateUniforms(gfx);
		child.updateUniforms(gfx);
	}
	
	protected void drawChild(LWJGLGraphics gfx, LWJGLDrawable child) {
		LWJGLGraphics.MVPState state = gfx.saveState();
		applyChildTransformation(gfx, child);
		updateChildUniforms(gfx, child);
		child.draw(gfx);
		gfx.loadState(state);
	}
	
	private LWJGLDrawable parent;

}
