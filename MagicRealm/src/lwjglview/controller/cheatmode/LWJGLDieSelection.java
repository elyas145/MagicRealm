package lwjglview.controller.cheatmode;

import java.awt.Color;
import java.awt.Font;

import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import lwjglview.menus.LWJGLContentPane;
import lwjglview.menus.LWJGLPanel;
import model.enums.TableType;
import utils.resources.ResourceHandler;
import view.controller.cheatmode.DieSelectionListener;
import view.controller.cheatmode.DieSelectionView;
import view.controller.search.TableSelectionListener;
import view.selection.PrimaryClickListener;

public class LWJGLDieSelection implements DieSelectionView {

	private static final Font FONT = new Font("Times New Roman", Font.PLAIN,
			60);
	private static final Color COLOR = Color.RED;

	public LWJGLDieSelection(ResourceHandler rh, LWJGLContentPane par) {
		resources = rh;
		root = LWJGLPanel.fromPicture(par, resources,
				ResourceHandler.joinPath("menus", "alert", "bg.gif"), -.35f,
				.34f, .4f, false);
		par.add(root);
		LWJGLPanel text = LWJGLPanel.fromString(root, "Die Selection",
				FONT, COLOR, 500, 70, 0f, .15f, .1f, false);
		text.setVisible(true);
		root.add(text);
		LWJGLPanel tmp, head = root;
		int count = 0;
		for(int i = 0; i < 2; ++i) { // row
			tmp = createDie(head, 0f, -.2f, ++count);
			head.add(tmp);
			head = tmp;
			head.setVisible(true);
			LWJGLPanel last = head;
			for(int j = 1; j < 3; ++j) { // column
				tmp = createDie(last, .2f, 0f, ++count);
				last.add(tmp);
				last = tmp;
				last.setVisible(true);
			}
		}
	}

	@Override
	public void selectDie(DieSelectionListener dsl) {
		onSelect = dsl;
	}

	@Override
	public void setVisible(boolean vis) {
		root.setVisible(vis);
	}
	
	private LWJGLPanel createDie(LWJGLPanel last, float xoff, float yoff, final int count) {
		LWJGLPanel pane = LWJGLPanel.fromPicture(last, resources, getPath(count), xoff, yoff, .15f, true);
		pane.setCursorListener(new PrimaryClickListener() {

			@Override
			public void onClick() {
				selected(count);
			}
			
		});
		return pane;
	}
	
	private static String getPath(int count) {
		return ResourceHandler.joinPath("dice", String.format("dice_%d.gif", count));
	}

	private void selected(int die) {
		if (onSelect != null) {
			DieSelectionListener dsl = onSelect;
			onSelect = null;
			dsl.dieSelected(die);
		}
	}

	private DieSelectionListener onSelect;
	private ResourceHandler resources;
	private LWJGLPanel root;

}
