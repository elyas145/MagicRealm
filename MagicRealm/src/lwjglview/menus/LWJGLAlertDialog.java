package lwjglview.menus;

import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import utils.resources.ResourceHandler;

public class LWJGLAlertDialog extends LWJGLDialog {

	public LWJGLAlertDialog(LWJGLContentPane cp, ResourceHandler rh,
			String msg, float xi, float yi, float xf, float yf, float size) {
		super(cp, rh, msg, xi, yi, xf, yf, size);
		LWJGLTextureLoader buttonBG = new LWJGLSingleTextureLoader(rh,
				ResourceHandler.joinPath("menus", "main", "button.png"));
		button = new LWJGLButton(root, "OK", buttonBG, size * .76f,
				size * .25f, size * .15f);
		button.setListener(new Runnable() {

			@Override
			public void run() {
				Runnable onclick = handler;
				handler = null;
				hide();
				if (onclick != null) {
					onclick.run();
				}
			}

		});
		handler = null;
		button.setVisible(true);
	}

	public void setHandler(Runnable run) {
		handler = run;
	}

	public void clearHandler() {
		handler = null;
	}

	private Runnable handler;

	private LWJGLButton button;

}
