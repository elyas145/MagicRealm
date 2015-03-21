package lwjglview.menus.dropdown;

import java.awt.Color;
import java.awt.Font;

import config.GraphicsConfiguration;
import utils.math.linear.Matrix;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import lwjglview.menus.LWJGLPanel;

public class LWJGLDropdownItem<T> extends LWJGLPanel {

	public LWJGLDropdownItem(LWJGLDropdown<T> par, LWJGLTextureLoader bg, T it,
			float w, float h, int idx) {
		super(par, bg, 0f, 0f, w, h, true);
		index = idx;
		buffer = Matrix.zeroVector(3);
		parent = par;
		item = it;
		int height = 60;
		Font fnt = new Font("Times New Roman", Font.PLAIN, height);
		LWJGLPanel pane = LWJGLPanel.fromString(this, it.toString(), fnt,
				Color.RED, (int) (height * w / h), height * 7 / 5, 0f, 0f, h, false);
		text = pane.getTexture();
		pane.setVisible(true);
		add(pane);
	}

	public void expand(LWJGLDropdown.Type dir) {
		float width, height;
		width = parent.getWidth() * index;
		height = parent.getHeight() * index;
		switch (dir) {
		case UP:
			buffer.fill(0f, height, 0f);
			break;
		case DOWN:
			buffer.fill(0f, -height, 0f);
			break;
		case RIGHT:
			buffer.fill(width, 0f, 0f);
			break;
		case LEFT:
			buffer.fill(-width, 0f, 0f);
			break;
		}
		moveTo(buffer, GraphicsConfiguration.PANEL_TIME);
	}

	public void collapse() {
		resetPosition();
	}

	public T getItem() {
		return item;
	}

	public LWJGLTextureLoader getText() {
		return text;
	}

	private int index;
	private Matrix buffer;
	private LWJGLDropdown<T> parent;
	private T item;
	private LWJGLTextureLoader text;

}
