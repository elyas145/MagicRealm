package lwjglview.controller;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import utils.string.TextTools;
import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLTextLog;

public class LWJGLWaitingView {

	private static final Font FONT = new Font("Times New Roman", Font.PLAIN,
			100);
	private static final Color COLOR = Color.WHITE;
	private static final int MAX_CHARS = 35;
	private static final int MAX_LINES = 3;

	public LWJGLWaitingView(LWJGLContentPane par, String msg) {
		message = msg;
		buffer = new ArrayList<String>();
		messagePanel = new LWJGLTextLog(par, -1.4f, -.3f, 2.8f, .2f, 0f, .2f,
				MAX_LINES, FONT, COLOR, 0f);
		messagePanel.setVisible(false);
		par.add(messagePanel);
		setText(message);
	}

	public void setText(String msg) {
		message = msg;
		buffer.clear();
		TextTools.wrap(message, buffer, MAX_CHARS);
		while (buffer.size() < MAX_LINES) {
			buffer.add("");
		}
		while (buffer.size() > MAX_LINES) {
			buffer.remove(buffer.size() - 1);
		}
		for (String str : buffer) {
			messagePanel.addText(str);
		}
	}

	public void setVisible(boolean vis) {
		messagePanel.setVisible(vis);
	}

	private String message;

	private ArrayList<String> buffer;

	private LWJGLTextLog messagePanel;

}
