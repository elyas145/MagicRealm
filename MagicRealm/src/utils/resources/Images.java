package utils.resources;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import model.enums.CounterType;
import config.GraphicsConfiguration;

public class Images {
	
	public static BufferedImage getImage(ResourceHandler rh, String path) throws IOException {
		BufferedImage before = rh.readImage(ResourceHandler.joinPath("images",
				path));
		BufferedImage newImage = new BufferedImage(
				GraphicsConfiguration.IMAGE_SCALE_WIDTH,
				GraphicsConfiguration.IMAGE_SCALE_HEIGHT,
				BufferedImage.TYPE_INT_ARGB);

		Graphics g = newImage.createGraphics();
		g.drawImage(before, 0, 0, GraphicsConfiguration.IMAGE_SCALE_WIDTH,
				GraphicsConfiguration.IMAGE_SCALE_HEIGHT, null);
		g.dispose();
		return newImage;
	}
}

