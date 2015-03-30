package lwjglview.graphics.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import lwjglview.graphics.LWJGLGraphics;
import utils.images.ImageTools;
import utils.images.ImageTools.GraphicsHandler;
import utils.resources.Images;
import utils.resources.ResourceHandler;

public class LWJGLSingleTextureLoader implements LWJGLTextureLoader {

	public LWJGLSingleTextureLoader(int w, int h, boolean inter) {
		width = w;
		height = h;
		imageBuffer = null;
		init();
		interpolate = inter;
		refresh = false;
	}

	public LWJGLSingleTextureLoader(ResourceHandler rh, String fileName) {
		try {
			imageBuffer = Images.getImage(rh, fileName);
			width = imageBuffer.getWidth();
			height = imageBuffer.getHeight();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		init();
		interpolate = true;
	}

	public LWJGLSingleTextureLoader(ResourceHandler rh, String fileName, int w,
			int h) {
		try {
			imageBuffer = Images.getImage(rh, fileName);
			width = imageBuffer.getWidth();
			height = imageBuffer.getHeight();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		imageBuffer = ImageTools.scaleImage(imageBuffer, w, h);
		init();
		interpolate = true;
	}

	public LWJGLSingleTextureLoader(ImageTools.GraphicsHandler gh, int w, int h) {
		imageBuffer = ImageTools.createImage(w, h, gh);
		width = imageBuffer.getWidth();
		height = imageBuffer.getHeight();
		init();
		interpolate = true;
	}

	public LWJGLSingleTextureLoader(int loc, int w, int h) {
		height = h;
		width = w;
		textureLocation = loc;
	}

	@Override
	public boolean isLoaded() {
		return getTextureLocation() >= 0 && !refresh;
	}

	@Override
	public void loadTexture(LWJGLGraphics gfx) {
		if (!isLoaded()) {
			if (getTextureLocation() < 0) {
				textureLocation = gfx.loadTexture(rawData, height, width,
						interpolate);
			} else {
				gfx.updateTexture(textureLocation, rawData, height, width);
			}
		}
		refresh = false;
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

	@Override
	public void updateFromGraphicsHandler(GraphicsHandler gh) {
		rawData.rewind();
		checkBuffer();
		ImageTools.createImage(imageBuffer, width, height, gh);
		ImageTools.loadRawImage(imageBuffer, 0, width, height, rawData);
		refresh = true;
	}

	private void checkBuffer() {
		boolean wasNull = imageBuffer == null;
		if (wasNull) {
			imageBuffer = new BufferedImage(height, height, height);
		}
		if (rawData == null) {
			rawData = ByteBuffer.allocateDirect(4 * width * height);
			if (!wasNull) {
				ImageTools.loadRawImage(imageBuffer, 0, width, height, rawData);
			}
		}
	}

	private void init() {
		checkBuffer();
		textureLocation = -1;
	}

	private boolean interpolate;
	private boolean refresh;
	private int height;
	private int width;
	private ByteBuffer rawData;
	private BufferedImage imageBuffer;
	private int textureLocation;

}
