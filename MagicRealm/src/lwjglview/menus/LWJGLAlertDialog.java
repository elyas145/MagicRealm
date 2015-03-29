package lwjglview.menus;

import java.awt.Color;
import java.awt.Font;

import config.GraphicsConfiguration;
import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;

public class LWJGLAlertDialog {

	private static final Font FONT = new Font("Times New Roman", Font.PLAIN, 80);
	private static final Color COLOR = Color.GRAY;

	public LWJGLAlertDialog(LWJGLContentPane cp, ResourceHandler rh,
			String msg, float xi, float yi, float xf, float yf, float size) {
		showPosition = Matrix.columnVector(xf, yf, 0f);
		root = LWJGLPanel.fromPicture(cp, rh,
				ResourceHandler.joinPath("menus", "alert", "bg.gif"), xi, yi,
				size, true);
		message = LWJGLPanel.fromString(root, msg, FONT, COLOR, 1800, 100, 0f,
				size * .6f, size * .1f, false);
		root.add(message);
		LWJGLTextureLoader buttonBG = new LWJGLSingleTextureLoader(rh,
				ResourceHandler.joinPath("menus", "main", "button.png"));
		button = new LWJGLButton(root, "OK", buttonBG, size * .85f, size * .3f,
				size * .1f);
		button.setListener(new Runnable() {

			@Override
			public void run() {
				hide();
			}

		});
		root.setVisible(true);
		message.setVisible(true);
		button.setVisible(true);
		cp.add(root);
	}

	public void setMessage(String msg) {
		message.updateFromString(msg, FONT, COLOR);
	}

	public void hide() {
		root.resetPosition();
	}

	public void show() {
		root.moveTo(showPosition, GraphicsConfiguration.PANEL_TIME);
	}
	
	private Matrix showPosition;
	private LWJGLPanel root;
	private LWJGLPanel message;
	private LWJGLButton button;

}
