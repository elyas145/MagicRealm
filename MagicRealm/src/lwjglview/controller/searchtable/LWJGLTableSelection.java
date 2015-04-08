package lwjglview.controller.searchtable;

import java.awt.Color;
import java.awt.Font;

import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLPanel;
import model.enums.TableType;
import utils.resources.ResourceHandler;
import view.controller.search.SearchView;
import view.controller.search.TableSelectionListener;
import view.selection.PrimaryClickListener;

public class LWJGLTableSelection implements SearchView {

	private static final Font FONT = new Font("Times New Roman", Font.PLAIN,
			60);
	private static final Color COLOR = Color.RED;

	public LWJGLTableSelection(ResourceHandler rh, LWJGLContentPane par) {
		LWJGLTextureLoader itembg = new LWJGLSingleTextureLoader(rh,
				ResourceHandler.joinPath("menus", "birdsong", "comboCell.png"));
		root = LWJGLPanel.fromPicture(par, rh,
				ResourceHandler.joinPath("menus", "alert", "bg.gif"), -.35f,
				.34f, .4f, false);
		par.add(root);
		LWJGLPanel text = LWJGLPanel.fromString(root, "Search Table",
				FONT, COLOR, 500, 70, 0f, .15f, .1f, false);
		text.setVisible(true);
		root.add(text);
		LWJGLPanel tmp, last = root;
		int i = 0;
		for (final TableType tt : TableType.values()) {
			tmp = LWJGLPanel.fromTextureLoader(last, itembg, 0f, -.2f, .2f, true);
			last.add(tmp);
			last = tmp;
			text = LWJGLPanel.fromString(last,
					String.format("%d: %s", ++i, tt.toString()), FONT, COLOR,
					700, 70, 0f, .06f, .07f, false);
			text.setVisible(true);
			last.add(text);
			last.setCursorListener(new PrimaryClickListener() {

				@Override
				public void onClick() {
					selected(tt);
				}
				
			});
			last.setVisible(true);
		}
	}

	@Override
	public void selectTable(TableSelectionListener tsl) {
		onSelect = tsl;
		setVisible(true);
	}

	@Override
	public void setVisible(boolean vis) {
		root.setVisible(vis);
	}

	private void selected(TableType tt) {
		if (onSelect != null) {
			TableSelectionListener tsl = onSelect;
			onSelect = null;
			setVisible(false);
			tsl.onSelect(tt);
		}
	}

	private TableSelectionListener onSelect;
	private LWJGLPanel root;

}
