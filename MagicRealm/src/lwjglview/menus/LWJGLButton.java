package lwjglview.menus;

import java.awt.Color;
import java.awt.Font;

import view.controller.ItemGroup;
import view.selection.PrimaryClickListener;
import lwjglview.graphics.textures.LWJGLTextureLoader;

public class LWJGLButton implements ItemGroup {

	private static final int FONT_HEIGHT = 100;

	private static final Font FONT = new Font("Times New Roman", Font.PLAIN, FONT_HEIGHT);

	private static final Color COLOR = Color.WHITE;

	public LWJGLButton(LWJGLContentPane par, String txt, LWJGLTextureLoader image, float x, float y, float sz) {
		backdrop = new LWJGLPanel(par, image, x, y, sz, true);
		text = LWJGLPanel.fromString(backdrop, txt, FONT, COLOR,
				FONT_HEIGHT * 15 / 6, FONT_HEIGHT * 13 / 10, 0f, 0f, sz,
				false);
		text.setVisible(true);
		backdrop.add(text);
		backdrop.setCursorListener(new PrimaryClickListener() {

			@Override
			public void onClick() {
				if(onClick != null) {
					onClick.run();
				}
			}
			
		});
		par.add(backdrop);
	}
	
	public void setText(String txt) {
		text.updateFromString(txt, FONT, COLOR);
	}
	
	public void setListener(Runnable click) {
		onClick = click;
	}

	@Override
	public void setVisible(boolean vis) {
		backdrop.setVisible(vis);
	}
	
	private LWJGLPanel backdrop;
	private LWJGLPanel text;
	private Runnable onClick;

}
