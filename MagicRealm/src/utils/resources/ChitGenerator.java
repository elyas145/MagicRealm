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

public class ChitGenerator implements ImageTools.GraphicsHandler {
	public ChitGenerator(MapChit mc, int w, int h) {
		name = mc.getType().toString();
		identifier = mc.getIdentifier();
		width = w;
		height = h;
	}
	
	public ChitGenerator(String nm, char id, int w, int h) {
		name = nm;
		identifier = id;
		width = w;
		height = h;
	}

	@Override
	public void draw(Graphics g) {
		double width = this.width;
		double height = this.height;
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

	private String name;
	private char identifier;
	private int width;
	private int height;
}
