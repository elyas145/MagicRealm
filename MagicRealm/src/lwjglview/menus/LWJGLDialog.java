package lwjglview.menus;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import config.GraphicsConfiguration;
import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;

public class LWJGLDialog {

	private static final Font FONT = new Font("Times New Roman", Font.PLAIN, 80);
	private static final Color COLOR = Color.GRAY;

	private static final int MAX_LINES = 3;

	private static int MAX_CHARS = 42;

	public LWJGLDialog(LWJGLContentPane cp, ResourceHandler rh,
			String msg, float xi, float yi, float xf, float yf, float size) {
		showPosition = Matrix.columnVector(xf, yf, 0f);
		root = LWJGLPanel.fromPicture(cp, rh,
				ResourceHandler.joinPath("menus", "alert", "bg.gif"), xi, yi,
				size, true);
		message = new LWJGLTextLog(root, 0f, size * .7f, size * 1.83f, size * .1f, 0f,
				-.1f, MAX_LINES, FONT, COLOR, 0f);
		root.add(message);
		root.setVisible(true);
		cp.add(root);
	}

	public void setMessage(String msg) {
		ArrayList<String> lines = new ArrayList<String>();
		int i = MAX_CHARS;
		while (i < msg.length()) { // split msg
			if (i >= MAX_CHARS) { // find whitespace
				int j;
				for (j = i; j > 0; --j) {
					if (msg.charAt(j) == ' ') {
						i = j - 1;
						j = 0;
					}
				}
				for (j = i; j > 0; --j) {
					if (msg.charAt(j) != ' ') {
						i = j + 1;
						j = 0;
					}
				}
				lines.add(msg.substring(0, i));
				msg = msg.substring(i, msg.length());
				if (lines.size() >= MAX_LINES) {
					i = msg.length();
				}
			}
		}
		if (lines.size() < MAX_LINES) {
			lines.add(msg);
		}
		while (lines.size() < MAX_LINES) {
			lines.add("");
		}
		for(int j = MAX_LINES - 1; j >= 0; --j) {
			lines.add(lines.get(j));
		}
		for(int j = 0; j < MAX_LINES; ++j) {
			lines.remove(0);
		}
		for (String line : lines) {
			message.addText(line);
		}
	}

	public void hide() {
		root.resetPosition();
	}

	public void show() {
		root.moveTo(showPosition, GraphicsConfiguration.PANEL_TIME);
	}

	private Matrix showPosition;
	protected LWJGLPanel root;
	private LWJGLTextLog message;

}
