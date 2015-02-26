package lwjglview.graphics;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import utils.images.ImageTools;

public class LWJGLTextureArrayLoader {

	public LWJGLTextureArrayLoader(int w, int h) {
		width = w;
		height = h;
		textureIndex = -1;
		images = new ArrayList<BufferedImage>();
		rawData = null;
	}

	public int addImage(BufferedImage bi) {
		if (bi.getHeight() == height && bi.getWidth() == width) {
			images.add(bi);
		} else {
			images.add(ImageTools.scaleImage(bi, width, height));
		}
		return images.size() - 1;
	}

	public int addImage(ImageTools.GraphicsHandler gh) {
		images.add(ImageTools.createImage(width, height, gh));
		return images.size() - 1;
	}

	public void loadImages() {//BufferUtils.createByteBuffer
		rawData = ByteBuffer.allocateDirect(4 * width * height
				* images.size());
		int offset = 0;
		for(BufferedImage bi: images) {
			offset = ImageTools.loadRawImage(bi, offset, width, height, rawData);
		}
	}
	
	public void loadTextures(LWJGLGraphics gfx) {
		if(!loadedTextures()) {
			textureIndex = gfx.loadTextureArray(rawData, images.size(), height, width);
			rawData = null;
		}
	}
	
	public boolean loadedTextures() {
		return textureIndex >= 0;
	}
	
	public void useTextures(LWJGLGraphics gfx) {
		if(!loadedTextures()) {
			loadTextures(gfx);
		}
		gfx.bindTextureArray(textureIndex);
	}

	private int width;
	private int height;
	private int textureIndex;
	private List<BufferedImage> images;
	private ByteBuffer rawData;

}
