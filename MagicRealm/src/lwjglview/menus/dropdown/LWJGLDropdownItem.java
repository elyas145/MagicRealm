package lwjglview.menus.dropdown;

import java.awt.Color;
import java.awt.Font;

import config.GraphicsConfiguration;
import utils.math.linear.Matrix;
import view.selection.CursorListener;
import view.selection.CursorSelection;
import lwjglview.graphics.LWJGLTextureLoader;
import lwjglview.menus.LWJGLPanel;

public class LWJGLDropdownItem<T> extends LWJGLPanel {

	public LWJGLDropdownItem(LWJGLDropdown<T> par, LWJGLTextureLoader bg, T it,
			float w, float h, int idx) {
		super(par, bg, 0f, 0f, w, h, LWJGLPanel.Type.FOREGROUND, true);
		index = idx;
		buffer = Matrix.zeroVector(3);
		parent = par;
		item = it;
		int height = 50;
		Font fnt = new Font("Times New Roman", Font.PLAIN, height);
		LWJGLPanel text = LWJGLPanel.fromString(this, it.toString(), fnt,
				Color.RED, (int) (height * w / h), 50, 0f, 0f, h,
				LWJGLPanel.Type.FOREGROUND, false);
		text.setVisible(true);
		add(text);
		setCursorListener(new CursorListener() {

			@Override
			public void onMove(int x, int y) {
			}

			@Override
			public void onSelection(CursorSelection select, boolean down) {
				if (down) {
					parent.onSelect(item);
				}
			}

		});
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

	private int index;
	private Matrix buffer;
	private LWJGLDropdown<T> parent;
	private T item;

}
