package lwjglview.controller;

import java.awt.Color;
import java.awt.Font;

import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLPanel;

public class LWJGLWaitingView {
	
	private static final Font FONT = new Font("Times New Roman", Font.PLAIN, 100);
	private static final Color COLOR = Color.WHITE;

	public LWJGLWaitingView(LWJGLContentPane par, String msg) {
		message = msg;
		messagePanel = LWJGLPanel.fromString(par, message, FONT, COLOR, 1500, 120, -1.2f, 0f, .2f, false);
		par.add(messagePanel);
	}
	
	public void setText(String msg) {
		message = msg;
		messagePanel.updateFromString(message, FONT, COLOR);
	}
	
	public void setVisible(boolean vis) {
		messagePanel.setVisible(vis);
	}
	
	private String message;
	
	private LWJGLPanel messagePanel;

}
