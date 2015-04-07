package lwjglview.menus;

import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import utils.resources.ResourceHandler;

public class LWJGLConfirmationDialog extends LWJGLDialog {

	public LWJGLConfirmationDialog(LWJGLContentPane cp, ResourceHandler rh,
			String msg, String yesMsg, String noMsg, float xi, float yi,
			float xf, float yf, float size) {
		super(cp, rh, msg, xi, yi, xf, yf, size);
		confirm = false;
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
		yButton.setVisible(true);
		nButton.setVisible(true);
		cp.add(root);
		visible = false;
	}

	public synchronized boolean ask(String question, String yes, String no) {
		setMessage(question);
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

	private boolean visible;
	private LWJGLButton yButton;
	private LWJGLButton nButton;
	private boolean confirm;

}
