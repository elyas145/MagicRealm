package lwjglview.menus.dropdown;

import java.util.ArrayList;
import java.util.List;

import utils.handler.Handler;
import view.selection.CursorListener;
import view.selection.CursorSelection;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLPanel;

public class LWJGLDropdown<T> extends LWJGLPanel {

	public enum Type {
		UP, DOWN, RIGHT, LEFT
	}

	public LWJGLDropdown(LWJGLContentPane par, LWJGLTextureLoader mainbg,
			LWJGLTextureLoader dropbg, List<T> its, Type tp, float xPos,
			float yPos, float w, float h) {
		super(par, mainbg, xPos, yPos, w, h, true);
		selections = new ArrayList<LWJGLDropdownItem<T>>();
		type = tp;
		showing = false;
		size = h;
		int idx = 0;
		for (T item : its) {
			LWJGLDropdownItem<T> pane = new LWJGLDropdownItem<T>(this, dropbg,
					item, w, h, idx + 1);
			pane.setVisible(true);
			pane.setCursorListener(new ItemListener(idx));
			selections.add(pane);
			add(pane, LWJGLPanel.Type.BACKGROUND);
			++idx;
		}
		setCursorListener(new CursorListener() {

			@Override
			public void onMove(int x, int y) {
			}

			@Override
			public void onSelection(CursorSelection select, boolean down) {
				if (down) {
					setShowing(!showing);
				}
			}

		});
		text = null;
	}
	
	public void disableAll() {
		for(LWJGLDropdownItem<T> drop: selections) {
			drop.setVisible(false);
		}
	}
	
	public void enableAll(List<T> items) {
		for(T item: items) {
			enable(item);
		}
	}
	
	public void enable(T item) {
		for(LWJGLDropdownItem<T> drop: selections) {
			if(item.equals(drop.getItem())) {
				drop.setVisible(true);
			}
		}
	}

	public void setSelectionListener(Handler<T> selection) {
		selectionListener = selection;
	}

	public void setShowing(boolean show) {
		if (show ^ showing) {
			for (LWJGLDropdownItem<T> item : selections) {
				if (showing) {
					item.collapse();
				} else {
					item.expand(type);
				}
			}
		}
		showing = show;
	}
	
	public void reset() {
		if(text != null) {
			remove(text, LWJGLPanel.Type.FOREGROUND);
			text = null;
		}
	}
	
	protected void setText(LWJGLTextureLoader texture) {
		text = LWJGLPanel.fromTextureLoader(this, texture, 0f, 0f,
				size, false);
		add(text, LWJGLPanel.Type.FOREGROUND);
		text.setVisible(true);
	}

	private void onSelect(T item) {
		if (selectionListener != null) {
			selectionListener.handle(item);
		}
	}

	private class ItemListener implements CursorListener {

		public ItemListener(int i) {
			index = i;
		}

		@Override
		public void onMove(int x, int y) {
		}

		@Override
		public void onSelection(CursorSelection select, boolean down) {
			if(down) {
				LWJGLDropdownItem<T> item = selections.get(index);
				onSelect(item.getItem());
				reset();
				LWJGLTextureLoader txt = item.getText();
				setText(txt);
				setShowing(false);
			}
		}

		private int index;

	}

	private boolean showing;
	private Type type;

	private float size;

	private LWJGLPanel text;

	private List<LWJGLDropdownItem<T>> selections;

	private Handler<T> selectionListener;

}
