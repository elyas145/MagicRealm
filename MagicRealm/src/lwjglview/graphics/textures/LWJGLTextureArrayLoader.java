package lwjglview.graphics.textures;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import lwjglview.graphics.LWJGLGraphics;

import utils.images.ImageTools;

public class LWJGLTextureArrayLoader {
	
	public class UnitLoader implements LWJGLTextureLoader {
		
		protected UnitLoader(int loc) {
			location = loc;
		}

		@Override
		public boolean isLoaded() {
			return loadedTextures();
		}

		@Override
		public void loadTexture(LWJGLGraphics gfx) {
			loadTextures(gfx);
		}

		@Override
		public void useTexture(LWJGLGraphics gfx) {
			useTextures(gfx);
			gfx.getShaders().setUniformIntValue("index", location);
		}

		@Override
		public void useTexture(LWJGLGraphics gfx, String uniform, int unit) {
			useTextures(gfx, uniform, unit);
			gfx.getShaders().setUniformIntValue("index" + unit, location);
		}

		@Override
		public int getTextureLocation() {
			return textureIndex;
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}
		
		private int location;
		
	}

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
		loadTextures(gfx, true);
	}
	
	public void loadTextures(LWJGLGraphics gfx, boolean accurate) {
		if(!loadedTextures()) {
			textureIndex = gfx.loadTextureArray(rawData, images.size(), height, width, accurate);
			rawData = null;
		}
	}
	
	public boolean loadedTextures() {
		return textureIndex >= 0;
	}
	
	public void useTextures(LWJGLGraphics gfx) {
		loadTextures(gfx);
		gfx.bindTextureArray(textureIndex);
	}
	
	public void useTextures(LWJGLGraphics gfx, String uniform, int unit) {
		loadTextures(gfx);
		gfx.bindTextureArray(textureIndex, unit);
		gfx.getShaders().setUniformIntValue(uniform, unit);
	}

	private int width;
	private int height;
	private int textureIndex;
	private List<BufferedImage> images;
	private ByteBuffer rawData;

}
