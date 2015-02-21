package lwjglview.graphics;

public abstract class LWJGLDrawableNode extends LWJGLDrawable {
	
	public abstract void applyNodeTransformation(LWJGLGraphics gfx);
	
	public abstract void updateNodeUniforms(LWJGLGraphics gfx);
	
	public LWJGLDrawable getParent() {
		return parent;
	}
	
	public void setParent(LWJGLDrawable par) {
		parent = par;
	}
	
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
	
	private LWJGLDrawable parent;

}
