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

	private static final Font FONT = new Font("Times New Roman", Font.PLAIN, 50);

	private static final Color COLOR = Color.RED;

	public LWJGLMainMenu(LWJGLViewController vc, ResourceHandler rh) {
		viewController = vc;
		backgroundTexture = new LWJGLTextureLoader(rh,
				ResourceHandler.joinPath("menus", "main", "background.jpg"));
	}

	LWJGLDropdown<String> dropdown;

	public void start(LWJGLMenuLayer menus) {
		float ar = backgroundTexture.getWidth()
				/ (float) backgroundTexture.getHeight();
		root = new LWJGLPanel(menus, backgroundTexture, -ar, -1f, 2 * ar, 2f,
				LWJGLPanel.Type.FOREGROUND, false);
		root.setVisible(true);
		start = LWJGLPanel.fromString(root, "Start", FONT, COLOR, 100, 75,
				.45f, 1f, .2f, LWJGLPanel.Type.FOREGROUND, true);
		start.setVisible(true);
		start.setCursorListener(new CursorListener() {
			@Override
			public void onMove(int x, int y) {
			}

			@Override
			public void onSelection(CursorSelection select, boolean down) {
				root.setVisible(false);
				viewController.startNetworkGame();
			}
		});
		exit = LWJGLPanel.fromString(root, "Exit", FONT, COLOR, 100, 75, .45f,
				.5f, .2f, LWJGLPanel.Type.FOREGROUND, true);
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

}
