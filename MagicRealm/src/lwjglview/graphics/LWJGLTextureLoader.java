package lwjglview.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import utils.images.ImageTools;
import utils.resources.Images;
import utils.resources.ResourceHandler;

public class LWJGLTextureLoader {
	
	public LWJGLTextureLoader(int w, int h, boolean inter) {
		width = w;
		height = h;
		init(null);
		interpolate = inter;
	}
	
	public LWJGLTextureLoader(ResourceHandler rh, String fileName) {
		BufferedImage bi;
		try {
			bi = Images.getImage(rh, fileName);
		}
		catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
		init(bi);
		interpolate = true;
	}
	
	public LWJGLTextureLoader(ResourceHandler rh, String fileName, int w, int h) {
		BufferedImage bi;
		try {
			bi = Images.getImage(rh, fileName);
		}
		catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
		bi = ImageTools.scaleImage(bi, w, h);
		init(bi);
		interpolate = true;
	}
	
	public LWJGLTextureLoader(ImageTools.GraphicsHandler gh, int w, int h) {
		BufferedImage bi = ImageTools.createImage(w, h, gh);
		System.out.println(String.format("%x", bi.getRGB(0, 0)));
		init(bi);
		interpolate = true;
	}
	
	public boolean isLoaded() {
		return textureLocation >= 0;
	}
	
	public void loadTexture(LWJGLGraphics gfx) {
		if(!isLoaded()) {
			textureLocation = gfx.loadTexture(rawData, height, width, interpolate);
		}
	}
	
	public void useTexture(LWJGLGraphics gfx) {
		loadTexture(gfx);
		gfx.bindTexture(textureLocation);
	}
	
	public void useTexture(LWJGLGraphics gfx, int unit) {
		loadTexture(gfx);
		gfx.bindTexture(textureLocation, unit);
	}
	
	public int getTextureLocation() {
		return textureLocation;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	private void init(BufferedImage bi) {
		if(bi != null) {
			height = bi.getHeight();
			width = bi.getWidth();
		}
		rawData = ByteBuffer.allocateDirect(4 * width * height);
		if(bi != null) {
			ImageTools.loadRawImage(bi, 0, width, height, rawData);
		}
		textureLocation = -1;
	}
	
	private boolean interpolate;
	private int height;
	private int width;
	private ByteBuffer rawData;
	private int textureLocation;

}
