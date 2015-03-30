package lwjglview.menus;

import java.awt.Color;
import java.awt.Font;

import config.GraphicsConfiguration;
import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;

public class LWJGLConfirmationDialog {

	private static final Font FONT = new Font("Times New Roman", Font.PLAIN, 80);
	private static final Color COLOR = Color.GRAY;

	public LWJGLConfirmationDialog(LWJGLContentPane cp, ResourceHandler rh,
			String msg, String yesMsg, String noMsg, float xi, float yi,
			float xf, float yf, float size) {
		confirm = false;
		showPosition = Matrix.columnVector(xf, yf, 0f);
		root = LWJGLPanel.fromPicture(cp, rh,
				ResourceHandler.joinPath("menus", "alert", "bg.gif"), xi, yi,
				size, true);
		message = LWJGLPanel.fromString(root, msg, FONT, COLOR, 1800, 100, 0f,
				size * .6f, size * .1f, false);
		root.add(message);
		LWJGLTextureLoader buttonBG = new LWJGLSingleTextureLoader(rh,
				ResourceHandler.joinPath("menus", "main", "button.png"));
		yButton = new LWJGLButton(root, yesMsg, buttonBG, size * .5f,
				size * .25f, size * .15f);
		yButton.setListener(new Runnable() {

			@Override
			public void run() {
				confirm = true;
				synchronized (LWJGLConfirmationDialog.this) {
					LWJGLConfirmationDialog.this.notify();
				}
			}

		});
		nButton = new LWJGLButton(root, noMsg, buttonBG, size * 1f,
				size * .25f, size * .15f);
		nButton.setListener(new Runnable() {

			@Override
			public void run() {
				confirm = false;
				synchronized (LWJGLConfirmationDialog.this) {
					LWJGLConfirmationDialog.this.notify();
				}
			}

		});
		root.setVisible(true);
		message.setVisible(true);
		yButton.setVisible(true);
		nButton.setVisible(true);
		cp.add(root);
		visible = false;
	}

	public synchronized boolean ask(String question, String yes, String no) {
		message.updateFromString(question, FONT, COLOR);
		yButton.setText(yes);
		nButton.setText(no);
		if (!visible) {
			show();
			visible = true;
		}
		confirm = false;
		try {
			wait();
		} catch (InterruptedException e) {
		}
		hide();
		visible = false;
		return confirm;
	}

	public void hide() {
		root.resetPosition();
	}

	private void show() {
		root.moveTo(showPosition, GraphicsConfiguration.PANEL_TIME);
	}

	private boolean visible;
	private Matrix showPosition;
	private LWJGLPanel root;
	private LWJGLPanel message;
	private LWJGLButton yButton;
	private LWJGLButton nButton;
	private boolean confirm;

}
