package lwjglview.controller.mainmenu;

import java.awt.Color;
import java.awt.Font;

import utils.resources.ResourceHandler;
import view.controller.mainmenu.MenuItem;
import view.controller.mainmenu.MenuItemListener;
import view.controller.mainmenu.MenuView;
import view.selection.CursorListener;
import view.selection.CursorSelection;
import lwjglview.graphics.textures.LWJGLSingleTextureLoader;
import lwjglview.graphics.textures.LWJGLTextureLoader;
import lwjglview.menus.LWJGLButton;
import lwjglview.menus.LWJGLMenuLayer;
import lwjglview.menus.LWJGLPanel;
import lwjglview.menus.dropdown.LWJGLDropdown;

public class LWJGLMainMenu implements MenuView {

	private static final int FONT_HEIGHT = 100;

	private static final Font FONT = new Font("Times New Roman", Font.PLAIN, FONT_HEIGHT);

	private static final Color COLOR = Color.WHITE;

	public LWJGLMainMenu(LWJGLMenuLayer par, ResourceHandler rh) {
		parent = par;
		backgroundTexture = new LWJGLSingleTextureLoader(rh,
				ResourceHandler.joinPath("menus", "main", "background.jpg"));
		resources = rh;
		start();
	}

	LWJGLDropdown<String> dropdown;

	@Override
	public void setVisible(boolean vis) {
		root.setVisible(vis);
	}

	@Override
	public void setMenuItemListener(MenuItemListener mil) {
		onSelect = mil;
	}

	private void start() {
		LWJGLMenuLayer menus = parent;
		float ar = backgroundTexture.getWidth()
				/ (float) backgroundTexture.getHeight();
		root = new LWJGLPanel(menus, backgroundTexture, -ar, -1f, 2 * ar, 2f,
				false);
		LWJGLTextureLoader button = new LWJGLSingleTextureLoader(resources,
				ResourceHandler.joinPath("menus", "main", "button.png"));
		
		start = new LWJGLButton(root, "Start", button, .38f, 1f, .2f);
		start.setListener(new Runnable() {

			@Override
			public void run() {
					onSelect.onItemSelect(MenuItem.START_GAME);
			}
		
		});
		start.setVisible(true);
		
		exit = new LWJGLButton(root, "Exit", button, .38f, .5f, .2f);
		exit.setListener(new Runnable() {

			@Override
			public void run() {
				onSelect.onItemSelect(MenuItem.EXIT);
			}
			
		});
		exit.setVisible(true);
		
		menus.add(root);
	}

	private MenuItemListener onSelect;

	private LWJGLTextureLoader backgroundTexture;
	private LWJGLPanel root;
	private LWJGLButton start;
	private LWJGLButton exit;
	private ResourceHandler resources;

	private LWJGLMenuLayer parent;

}
