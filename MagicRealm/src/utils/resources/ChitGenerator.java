package utils.resources;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import config.GraphicsConfiguration;
import utils.images.ImageTools;
import model.counter.chit.MapChit;

public class ChitGenerator implements ImageTools.GraphicsHandler {
	public ChitGenerator(MapChit mc) {
		name = mc.getType().toString();
		identifier = mc.getIdentifier();
	}
	
	public ChitGenerator(String nm, char id) {
		name = nm;
		identifier = id;
	}

	@Override
	public void draw(Graphics g) {
		Rectangle2D rect = g.getClipBounds();
		double width = rect.getWidth();
		double height = rect.getHeight();
		g.fillRect(0, 0, (int) width, (int) height);
		g.setFont(new Font("TimesRoman", Font.PLAIN, (int) (height * .2)));
		FontMetrics fm = g.getFontMetrics();
		rect = fm.getStringBounds(name, g);
		g.drawString(name, (int) ((width - rect.getWidth()) * .5),
				(int) (height * .4 - rect.getHeight() * .5));
		String id = Character.toString(identifier);
		rect = fm.getStringBounds(id, g);
		g.drawString(id, (int) ((width - rect.getWidth()) * .5),
				(int) (height * .6 - rect.getHeight() * .5));
	}

	private String name;
	private char identifier;
}
