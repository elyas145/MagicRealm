package lwjglview.menus;

import java.awt.Color;
import java.awt.Font;

import config.GraphicsConfiguration;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.selection.SelectionFrame;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import view.selection.CursorListener;
import view.selection.CursorSelection;

public class LWJGLAlertDialog extends LWJGLContentPane {

	private static final Font FONT = new Font("Times New Roman", Font.PLAIN, 80);

	public LWJGLAlertDialog(LWJGLContentPane cp, ResourceHandler rh,
			String msg, float xi, float yi, float xf, float yf, float size) {
		super(cp);
		selectFrame = cp.getSelectionFrame();
		showPosition = Matrix.columnVector(xf, yf, 0f);
		root = LWJGLPanel.fromPicture(this, rh,
				ResourceHandler.joinPath("menus", "alert", "bg.gif"), xi, yi,
				size, true);
		message = LWJGLPanel.fromString(root, msg, FONT, Color.GRAY, 1800, 100,
				0f, size * .6f, size * .1f, false);
		root.add(message);
		button = LWJGLPanel.fromString(root, "OK", FONT, Color.GRAY, 140, 100,
				size * .85f, size * .3f, size * .1f, true);
		root.add(button);
		button.setCursorListener(new CursorListener() {

			@Override
			public void onMove(int x, int y) {
			}

			@Override
			public void onSelection(CursorSelection select, boolean down) {
				if (down && select == CursorSelection.PRIMARY) {
					hide();
				}
			}

		});
		root.setVisible(true);
		message.setVisible(true);
		button.setVisible(true);
	}

	public void hide() {
		root.resetPosition();
	}

	public void show() {
		root.moveTo(showPosition, GraphicsConfiguration.PANEL_TIME);
	}

	@Override
	public void draw(LWJGLGraphics gfx) {
		root.draw(gfx);
	}

	@Override
	public void add(LWJGLContentPane pane) {
	}

	@Override
	public void remove(LWJGLContentPane pane) {
	}

	@Override
	public SelectionFrame getSelectionFrame() {
		return selectFrame;
	}

	@Override
	public void updateNodeUniforms(LWJGLGraphics gfx) {
	}

	private SelectionFrame selectFrame;
	private Matrix showPosition;
	private LWJGLPanel root;
	private LWJGLPanel message;
	private LWJGLPanel button;

}
