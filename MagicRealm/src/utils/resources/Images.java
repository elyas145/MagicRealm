package utils.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;

import utils.images.ImageTools;
import config.GraphicsConfiguration;

public class Images {

	public static BufferedImage getImage(ResourceHandler rh, String path)
			throws IOException {
		return rh.readImage(ResourceHandler.joinPath("images", path));
	}

	public static BufferedImage getScaledImage(ResourceHandler rh, String path)
			throws IOException {
		BufferedImage before = getImage(rh, path);
		int width, height;
		width = GraphicsConfiguration.IMAGE_SCALE_WIDTH;
		height = GraphicsConfiguration.IMAGE_SCALE_HEIGHT;
		return ImageTools.scaleImage(before, width, height);
	}
}
