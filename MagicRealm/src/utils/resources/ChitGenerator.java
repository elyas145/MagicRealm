package utils.resources;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import config.GraphicsConfiguration;
import utils.resources.Images.GraphicsHandler;
import model.counter.chit.MapChit;

public class ChitGenerator {

	public static BufferedImage generateMapChit(MapChit chit) {
		int width, height;
		width = GraphicsConfiguration.IMAGE_SCALE_WIDTH;
		height = GraphicsConfiguration.IMAGE_SCALE_HEIGHT;
		return Images.createImage(width, height, new MapChitDrawer(chit
				.getType().toString(), chit.getIdentifier(), width, height));
	}

	private static class MapChitDrawer implements GraphicsHandler {
		public MapChitDrawer(String nm, char id, int w, int h) {
			name = nm;
			identifier = id;
			width = w;
			height = h;
		}

		@Override
		public void draw(Graphics g) {
			g.fillRect(0, 0, width, height);
			g.setFont(new Font("TimesRoman", Font.PLAIN, height / 5));
			FontMetrics fm = g.getFontMetrics();
			Rectangle2D rect = fm.getStringBounds(name, g);
			g.drawString(name, (int) ((width - rect.getWidth()) * .5),
					(int) (height * .4 - rect.getHeight() * .5));
			String id = Character.toString(identifier);
			rect = fm.getStringBounds(id, g);
			g.drawString(id, (int) ((width - rect.getWidth()) * .5),
					(int) (height * .6 - rect.getHeight() * .5));
		}

		private String name;
		private char identifier;
		private int width, height;
	}

}
