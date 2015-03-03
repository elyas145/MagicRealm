package utils.images;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import config.GraphicsConfiguration;

public class ImageTools {

	public static interface GraphicsHandler {
		void draw(Graphics g, int width, int height);
	}

	public static BufferedImage createImage(int width, int height,
			GraphicsHandler gh) {

		BufferedImage newImage = new BufferedImage(
				GraphicsConfiguration.IMAGE_SCALE_WIDTH,
				GraphicsConfiguration.IMAGE_SCALE_HEIGHT,
				BufferedImage.TYPE_INT_ARGB);

		Graphics g = newImage.createGraphics();
		gh.draw(g, width, height);
		g.dispose();
		return newImage;
	}

	public static int loadRawImage(BufferedImage img, int offset, int width, int height,
			ByteBuffer dest) {
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int i = img.getRGB(x, y);
				int c = (i >> 16) & 0xFF; // blue
				c <<= 8;
				c |= (i >> 8) & 0xFF; // green
				c <<= 8;
				c |= i & 0xFF; // red
				c <<= 8;
				c |= (i >> 24) & 0xFF; // alpha
				dest.putInt(offset, c);
				offset += 4;
			}
		}
		return offset;
	}

	public static int loadRawImageArray(BufferedImage[] imgs, int offset, int width,
			int height, ByteBuffer dest) {
		for (BufferedImage img : imgs) {
			offset = loadRawImage(img, offset, width, height, dest);
		}
		return offset;
	}

	public static BufferedImage scaleImage(BufferedImage bi, int width,
			int height) {
		// TODO Auto-generated method stub
		return null;
	}

}
