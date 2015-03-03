package utils.resources;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import utils.images.ImageTools;
import config.GraphicsConfiguration;

public class Images {

	public static BufferedImage getImage(ResourceHandler rh, String path)
			throws IOException {
		BufferedImage before = rh.readImage(ResourceHandler.joinPath("images",
				path));
		int width, height;
		width = GraphicsConfiguration.IMAGE_SCALE_WIDTH;
		height = GraphicsConfiguration.IMAGE_SCALE_HEIGHT;
		return ImageTools.createImage(width, height,
				new ImageScaler(before));
	}

	private static class ImageScaler implements ImageTools.GraphicsHandler {

		public ImageScaler(BufferedImage before) {
			img = before;
		}

		@Override
		public void draw(Graphics g, int width, int height) {
			g.drawImage(img, 0, 0, width, height, null);
		}

		private BufferedImage img;

	}
}
