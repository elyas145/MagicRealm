package utils.resources;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import config.GraphicsConfiguration;

public class Images {

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

	public static BufferedImage getImage(ResourceHandler rh, String path)
			throws IOException {
		BufferedImage before = rh.readImage(ResourceHandler.joinPath("images",
				path));
		int width, height;
		width = GraphicsConfiguration.IMAGE_SCALE_WIDTH;
		height = GraphicsConfiguration.IMAGE_SCALE_HEIGHT;
		BufferedImage newImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = newImage.createGraphics();
		g.drawImage(before, 0, 0, width, height, null);
		g.dispose();
		return createImage(width, height,
				new ImageScaler(before, width, height));
	}

	private static class ImageScaler implements GraphicsHandler {

		public ImageScaler(BufferedImage before, int w, int h) {
			img = before;
			width = w;
			height = h;
		}

		@Override
		public void draw(Graphics g) {
			g.drawImage(img, width, height, null);
		}

		private BufferedImage img;
		private int width, height;

	}
}
