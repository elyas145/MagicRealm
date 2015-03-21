package utils.resources;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import config.GraphicsConfiguration;
import utils.images.ImageTools;
import model.counter.chit.MapChit;

public class MapChitImageGenerator implements ImageTools.GraphicsHandler {
	public MapChitImageGenerator(MapChit mc) {
		name = mc.getType().toString();
		identifier = mc.getIdentifier();
	}
	
	public MapChitImageGenerator(String nm, char id) {
		name = nm;
		identifier = id;
	}

	@Override
	public void draw(Graphics g, int w, int h) {
		double width = w;
		double height = h;
		g.fillRect(0, 0, (int) width, (int) height);
		g.setFont(new Font("TimesRoman", Font.BOLD, (int) (height * .19)));
		g.setColor(Color.BLACK);
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D rekt = fm.getStringBounds(name, g);
		g.drawString(name, (int) ((width - rekt.getWidth()) * .5),
				(int) (height * .6 - rekt.getHeight() * .5));
		String id = Character.toString(identifier);
		rekt = fm.getStringBounds(id, g);
		g.drawString(id, (int) ((width - rekt.getWidth()) * .5),
				(int) (height - rekt.getHeight() * .5));
	}
	
	@Override
	public int post(int in) {
		return in;
	}

	private String name;
	private char identifier;
}
