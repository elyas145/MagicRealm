package lwjglview.graphics;

import lwjglview.graphics.shader.ShaderType;
import config.GraphicsConfiguration;
import view.graphics.Drawable;
import view.graphics.Graphics;

public class LWJGLChitDrawable implements Drawable {
	
	public static final ShaderType SHADER = ShaderType.CHIT_SHADER;

	public LWJGLChitDrawable(float x, float y, Drawable chitBlock, int texid) {
		xPosition = x;
		yPosition = y;
		representation = chitBlock;
		textureIndex = texid;
	}

	@Override
	public void draw(Graphics gfx) {
		LWJGLGraphics lwgfx = (LWJGLGraphics) gfx;
		
		lwgfx.resetModelMatrix();
		lwgfx.scaleModel(GraphicsConfiguration.CHIT_SCALE);
		lwgfx.translateModel(xPosition, yPosition,
				GraphicsConfiguration.CHIT_HOVER);
		
		lwgfx.updateModelViewUniform(SHADER, "modelViewMatrix");
		lwgfx.updateMVPUniform(SHADER, "mvpMatrix");
		lwgfx.getShaders().setUniformIntValue(SHADER, "index",
				textureIndex);
		
		representation.draw(gfx);
	}

	private float xPosition;
	private float yPosition;
	private Drawable representation;
	private int textureIndex;

}
