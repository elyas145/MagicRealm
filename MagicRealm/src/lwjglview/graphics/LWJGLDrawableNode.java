package lwjglview.graphics;

public abstract class LWJGLDrawableNode extends LWJGLDrawable {
	
	public abstract void applyNodeTransformation(LWJGLGraphics gfx);
	
	public abstract void updateNodeUniforms(LWJGLGraphics gfx);
	
	@Override
	public final void applyTransformation(LWJGLGraphics gfx) {
		applyNodeTransformation(gfx);
		if(parent != null) {
			parent.applyTransformation(gfx);
		}
	}
	
	@Override
	public final void updateUniforms(LWJGLGraphics gfx) {
		if(parent != null) {
			parent.updateUniforms(gfx);
		}
		updateNodeUniforms(gfx);
	}
	
	protected LWJGLDrawableNode(LWJGLDrawable par) {
		parent = par;
	}
	
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
