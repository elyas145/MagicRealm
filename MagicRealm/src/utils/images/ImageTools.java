package utils.images;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import config.GraphicsConfiguration;

public class ImageTools {

	public static interface GraphicsHandler {
		void draw(Graphics g);
	}

	public static BufferedImage createImage(int width, int height,
			GraphicsHandler gh) {

		BufferedImage newImage = new BufferedImage(
				GraphicsConfiguration.IMAGE_SCALE_WIDTH,
				GraphicsConfiguration.IMAGE_SCALE_HEIGHT,
				BufferedImage.TYPE_INT_ARGB);

		Graphics g = newImage.createGraphics();
		gh.draw(g);
		g.dispose();
		return newImage;
	}

	public static int loadRawImage(BufferedImage img, int offset, int width, int height,
			ByteBuffer dest) {
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int i = img.getRGB(x, y);
				dest.putInt(offset, i);
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
