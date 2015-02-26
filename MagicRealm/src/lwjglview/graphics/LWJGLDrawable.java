package lwjglview.graphics;

import view.graphics.Drawable;
import view.graphics.Graphics;

public abstract class LWJGLDrawable implements Drawable {
	
	public abstract void updateUniforms(LWJGLGraphics gfx);
	
	public abstract void draw(LWJGLGraphics gfx);
	
	@Override
	public void draw(Graphics gfx) {
		draw((LWJGLGraphics) gfx);
	}
	
}
