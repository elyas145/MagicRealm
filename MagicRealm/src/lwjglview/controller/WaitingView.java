package lwjglview.controller;

import java.awt.Color;
import java.awt.Font;

import lwjglview.graphics.LWJGLGraphics;
import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLPanel;
import lwjglview.selection.SelectionFrame;

public class WaitingView extends LWJGLContentPane {
	
	private static final Font FONT = new Font("Times New Roman", Font.PLAIN, 100);
	private static final Color COLOR = Color.WHITE;

	public WaitingView(LWJGLContentPane par, String msg) {
		super(par);
		selectionFrame = par.getSelectionFrame();
		message = msg;
		messagePanel = LWJGLPanel.fromString(this, message, FONT, COLOR, 1500, 120, 0f, 0f, .2f, false);
		messagePanel.setVisible(true);
		visible = false;
	}
	
	public void setVisible(boolean vis) {
		visible = vis;
	}
	
	public void setText(String msg) {
		message = msg;
		messagePanel.updateFromString(message, FONT, COLOR);
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		if(visible) {
			messagePanel.draw(gfx);
		}
	}

	@Override
	public void add(LWJGLContentPane pane) {
	}

	@Override
	public void remove(LWJGLContentPane pane) {
	}

	@Override
	public SelectionFrame getSelectionFrame() {
		return selectionFrame;
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}
	
	private boolean visible;
	
	private String message;
	
	private LWJGLPanel messagePanel;
	
	private SelectionFrame selectionFrame;

}
