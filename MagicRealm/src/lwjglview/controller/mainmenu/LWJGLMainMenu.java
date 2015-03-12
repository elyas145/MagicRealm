package lwjglview.controller.mainmenu;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import utils.handler.Handler;
import utils.resources.ResourceHandler;
import view.controller.ViewController;
import view.selection.CursorListener;
import view.selection.CursorSelection;
import lwjglview.controller.LWJGLViewController;
import lwjglview.graphics.LWJGLTextureLoader;
import lwjglview.menus.LWJGLMenuLayer;
import lwjglview.menus.LWJGLPanel;
import lwjglview.menus.dropdown.LWJGLDropdown;

public class LWJGLMainMenu {

	private static final int FONT_HEIGHT = 100;

	private static final Font FONT = new Font("eufm10", Font.PLAIN, FONT_HEIGHT);

	private static final Color COLOR = Color.WHITE;

	public LWJGLMainMenu(LWJGLViewController vc, ResourceHandler rh) {
		viewController = vc;
		backgroundTexture = new LWJGLTextureLoader(rh,
				ResourceHandler.joinPath("menus", "main", "background.jpg"));
		resources = rh;
	}

	LWJGLDropdown<String> dropdown;

	public void start(LWJGLMenuLayer menus) {
		float ar = backgroundTexture.getWidth()
				/ (float) backgroundTexture.getHeight();
		root = new LWJGLPanel(menus, backgroundTexture, -ar, -1f, 2 * ar, 2f,
				LWJGLPanel.Type.FOREGROUND, false);
		root.setVisible(true);
		LWJGLTextureLoader button = new LWJGLTextureLoader(resources,
				ResourceHandler.joinPath("menus", "main", "button.png"));
		start = new LWJGLPanel(root, button, .38f, 1f, .2f,
				LWJGLPanel.Type.FOREGROUND, true);
		LWJGLPanel.fromString(start, "Start", FONT, COLOR, FONT_HEIGHT * 15 / 6,
				FONT_HEIGHT * 13 / 10, .02f, 0f, .18f,
				LWJGLPanel.Type.FOREGROUND, false).setVisible(true);
		start.setVisible(true);
		start.setCursorListener(new CursorListener() {
			@Override
			public void onMove(int x, int y) {
			}

			@Override
			public void onSelection(CursorSelection select, boolean down) {
				if (down) {
					root.setVisible(false);
					viewController.startNetworkGame();
				}
			}
		});
		exit = new LWJGLPanel(root, button, .38f, .5f, .2f,
				LWJGLPanel.Type.FOREGROUND, true);
		LWJGLPanel.fromString(exit, "Exit", FONT, COLOR, FONT_HEIGHT * 15 / 6,
				FONT_HEIGHT * 13 / 10, .02f, 0f, .18f,
				LWJGLPanel.Type.FOREGROUND, false).setVisible(true);
		exit.setVisible(true);
		exit.setCursorListener(new CursorListener() {
			@Override
			public void onMove(int x, int y) {
			}

			@Override
			public void onSelection(CursorSelection select, boolean down) {
				viewController.exit();
			}
		});
		List<String> selections = new ArrayList<String>();
		selections.add("Hello");
		selections.add("Goodbye");
	}

	private ViewController viewController;
	private LWJGLTextureLoader backgroundTexture;
	private LWJGLPanel root;
	private LWJGLPanel start;
	private LWJGLPanel exit;
	private ResourceHandler resources;

}
