package lwjglview.controller.searchtable;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLPanel;
import lwjglview.menus.dropdown.LWJGLDropdown;
import lwjglview.selection.SelectionFrame;
import model.enums.SearchType;
import model.enums.TableType;
import utils.handler.Handler;
import utils.math.linear.Matrix;
import utils.resources.ResourceHandler;
import view.controller.search.SearchTypeListener;
import view.controller.search.SearchChoiceView;
import view.controller.search.SearchView;
import view.controller.search.TableSelectionListener;
import view.selection.PrimaryClickListener;

public class LWJGLSearchChoiceView implements SearchChoiceView {

	private static final Font FONT = new Font("Times New Roman", Font.PLAIN, 60);
	private static final Color COLOR = Color.RED;

	public LWJGLSearchChoiceView(ResourceHandler rh, LWJGLContentPane par) {
		LWJGLTextureLoader itembg = new LWJGLSingleTextureLoader(rh,
				ResourceHandler.joinPath("menus", "birdsong", "comboCell.png"));
		root = LWJGLPanel.fromPicture(par, rh,
				ResourceHandler.joinPath("menus", "alert", "bg.gif"), -.35f,
				.34f, .4f, false);
		par.add(root);
		LWJGLPanel text = LWJGLPanel.fromString(root, "Choice Table", FONT,
				COLOR, 500, 70, 0f, .15f, .1f, false);
		text.setVisible(true);
		root.add(text);
		LWJGLPanel tmp;
		types = new HashMap<SearchType, LWJGLPanel>();
		for (final SearchType st : SearchType.values()) {
			tmp = LWJGLPanel.fromTextureLoader(root, itembg, 0f, 0f, .2f, true);
			root.add(tmp);
			text = LWJGLPanel.fromString(tmp, st.toString(), FONT, COLOR, 700,
					70, 0f, .07f, .07f, false);
			text.setVisible(true);
			tmp.add(text);
			tmp.setCursorListener(new PrimaryClickListener() {

				@Override
				public void onClick() {
					selected(st);
				}

			});
			types.put(st, tmp);
		}
	}

	@Override
	public void selectSearchType(List<SearchType> avail, SearchTypeListener stl) {
		onSelect = stl;
		float pos = 0f;
		float diff = .2f;
		for (SearchType st : avail) {
			pos += diff;
			LWJGLPanel pane = types.get(st);
			if (buffer == null) {
				buffer = Matrix.columnVector(0f, -pos, 0f);
			} else {
				buffer.fill(0f, -pos, 0f);
			}
			pane.moveTo(buffer, 0f);
			pane.setVisible(true);
		}
		setVisible(true);
	}

	@Override
	public void setVisible(boolean vis) {
		root.setVisible(vis);
		if (vis == false) {
			for (LWJGLPanel pane : types.values()) {
				pane.setVisible(false);
			}
		}
	}

	private void selected(SearchType st) {
		if (onSelect != null) {
			SearchTypeListener stl = onSelect;
			onSelect = null;
			setVisible(false);
			stl.onSearchTypeSelected(st);
		}
	}

	private SearchTypeListener onSelect;
	private LWJGLPanel root;
	private Map<SearchType, LWJGLPanel> types;
	private Matrix buffer = null;

}
