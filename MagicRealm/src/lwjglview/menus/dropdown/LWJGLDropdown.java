package lwjglview.menus.dropdown;

import java.util.ArrayList;
import java.util.List;

import utils.handler.Handler;
import view.selection.CursorListener;
import view.selection.CursorSelection;
import lwjglview.graphics.LWJGLTextureLoader;
import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLPanel;

public class LWJGLDropdown <T> extends LWJGLPanel {

	public enum Type {
		UP, DOWN, RIGHT, LEFT
	}

	public LWJGLDropdown(LWJGLContentPane par, LWJGLTextureLoader mainbg,
			LWJGLTextureLoader dropbg, List<T> its, Type tp, float xPos, float yPos,
			float w, float h) {
		super(par, mainbg, xPos, yPos, w, h, LWJGLPanel.Type.BACKGROUND, true);
		selections = new ArrayList<LWJGLDropdownItem<T>>();
		type = tp;
		showing = false;
		int idx = 0;
		for(T item: its) {
			LWJGLDropdownItem<T> pane = new LWJGLDropdownItem<T>(this, dropbg, item, w, h, ++idx);
			pane.setVisible(true);
			selections.add(pane);
		}
		setCursorListener(new CursorListener() {

			@Override
			public void onMove(int x, int y) {
			}

			@Override
			public void onSelection(CursorSelection select, boolean down) {
				if(down) {
					for(LWJGLDropdownItem<T> item: selections) {
						if(showing) {
							item.collapse();
						}
						else {
							item.expand(type);
						}
					}
					showing ^= true;
				}
			}
			
		});
	}
	
	public void setSelectionListener(Handler<T> selection) {
		selectionListener = selection;
	}
	
	public void onSelect(T item) {
		if(selectionListener != null) {
			selectionListener.handle(item);
		}
	}
	
	private boolean showing;
	private Type type;
	
	private List<LWJGLDropdownItem<T>> selections;
	
	private Handler<T> selectionListener;

}
