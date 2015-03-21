package lwjglview.graphics.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import lwjglview.graphics.LWJGLGraphics;

import utils.images.ImageTools;
import utils.resources.Images;
import utils.resources.ResourceHandler;

public class LWJGLSingleTextureLoader implements LWJGLTextureLoader {
	
	public LWJGLSingleTextureLoader(int w, int h, boolean inter) {
		width = w;
		height = h;
		init(null);
		interpolate = inter;
	}
	
	public LWJGLSingleTextureLoader(ResourceHandler rh, String fileName) {
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
	
	public LWJGLSingleTextureLoader(ResourceHandler rh, String fileName, int w, int h) {
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
	
	public LWJGLSingleTextureLoader(ImageTools.GraphicsHandler gh, int w, int h) {
		BufferedImage bi = ImageTools.createImage(w, h, gh);
		init(bi);
		interpolate = true;
	}
	
	public LWJGLSingleTextureLoader(int loc, int w, int h) {
		height = h;
		width = w;
		textureLocation = loc;
	}
	
	@Override
	public boolean isLoaded() {
		return getTextureLocation() >= 0;
	}

	@Override
	public void loadTexture(LWJGLGraphics gfx) {
		if(!isLoaded()) {
			textureLocation = gfx.loadTexture(rawData, height, width, interpolate);
		}
	}

	@Override
	public void useTexture(LWJGLGraphics gfx) {
		loadTexture(gfx);
		gfx.bindTexture(getTextureLocation());
	}

	@Override
	public void useTexture(LWJGLGraphics gfx, String uniform, int unit) {
		loadTexture(gfx);
		gfx.bindTexture(getTextureLocation(), unit);
		gfx.getShaders().setUniformIntValue(uniform, unit);
	}

	@Override
	public int getTextureLocation() {
		return textureLocation;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
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
