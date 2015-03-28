package lwjglview.graphics.textures;

import utils.images.ImageTools.GraphicsHandler;
import lwjglview.graphics.LWJGLGraphics;

public interface LWJGLTextureLoader {
	
	boolean isLoaded();
	
	void loadTexture(LWJGLGraphics gfx);
	
	void useTexture(LWJGLGraphics gfx);
	
	void useTexture(LWJGLGraphics gfx, String uniform, int unit);
	
	int getTextureLocation();
	
	int getWidth();
	
	int getHeight();

	void updateFromGraphicsHandler(GraphicsHandler gh);

}
