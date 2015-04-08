package utils.images;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import config.GraphicsConfiguration;

public class ImageTools {

	public static interface GraphicsHandler {
		void draw(Graphics g, int width, int height);
		int post(int in);
	}

	public static class StringDrawer implements GraphicsHandler {
		public StringDrawer(String lb, Font fnt, Color col) {
			label = lb;
			font = fnt;
			color = col.getRGB();
		}

		@Override
		public void draw(Graphics g, int width, int height) {
			g.setColor(CLEAR);
			g.clearRect(0, 0, width, height);
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();
			Rectangle2D bounds = fm.getStringBounds(label, g);
			g.setColor(FILL);
			g.drawString(label, (int) ((width - bounds.getWidth()) * .5),
					(int) ((height + bounds.getHeight()) * .4));
		}
		
		@Override
		public int post(int in) {
			return (color & 0x00FFFFFF) | ((in & 0x000000FF) << 24);
		}

		private int color;
		private String label;
		private Font font;
		private static Color CLEAR = new Color(0, 0, 0, 0);
		private static Color FILL = new Color(255, 255, 255, 255);
	}

	public static BufferedImage createImage(BufferedImage buffer, int width, int height,
			GraphicsHandler gh) {

		Graphics g = buffer.createGraphics();
		gh.draw(g, width, height);
		g.dispose();
		
		for(int i = 0; i < buffer.getWidth(); ++i) {
			for(int j = 0; j < buffer.getHeight(); ++j) {
				buffer.setRGB(i, j, gh.post(buffer.getRGB(i, j)));
			}
		}
		return buffer;
	}

	public static BufferedImage createImage(int width, int height,
			GraphicsHandler gh) {

		BufferedImage newImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		return createImage(newImage, width, height, gh);
	}

	public static BufferedImage createScaledImage(int width, int height,
			GraphicsHandler gh) {

		BufferedImage newImage = ImageTools.createImage(width, height, gh);

		return ImageTools.scaleImage(newImage,
				GraphicsConfiguration.IMAGE_SCALE_WIDTH,
				GraphicsConfiguration.IMAGE_SCALE_HEIGHT);
	}

	public static int loadRawImage(BufferedImage img, int offset, int width,
			int height, ByteBuffer dest) {
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

	public static int loadRawImageArray(BufferedImage[] imgs, int offset,
			int width, int height, ByteBuffer dest) {
		for (BufferedImage img : imgs) {
			offset = loadRawImage(img, offset, width, height, dest);
		}
		return offset;
	}

	public static BufferedImage scaleImage(BufferedImage bi, int width,
			int height) {
		return ImageTools.createImage(width, height, new ImageScaler(bi));
	}

	private static class ImageScaler implements ImageTools.GraphicsHandler {
		public ImageScaler(BufferedImage bi) {
			image = bi;
		}

		@Override
		public void draw(Graphics g, int width, int height) {
			g.drawImage(image, 0, 0, width, height, null);
		}

		private Image image;

		@Override
		public int post(int in) {
			return in;
		}
	}

}
