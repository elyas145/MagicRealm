package lwjglview.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import config.NetworkConfiguration;
import client.ClientController;
import client.ControllerMain;
import lwjglview.controller.birdsong.LWJGLBirdsong;
import lwjglview.controller.mainmenu.LWJGLMainMenu;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.menus.LWJGLAlertDialog;
import lwjglview.menus.LWJGLCounterView;
import lwjglview.menus.LWJGLMenuLayer;
import lwjglview.menus.LWJGLPanel;
import lwjglview.menus.characterselection.LWJGLCharacterSelection;
import lwjglview.selection.SelectionFrame;
import model.activity.Activity;
import model.character.Phase;
import model.enums.CharacterType;
import model.enums.TileName;
import model.player.PersonalHistory;
import utils.handler.Handler;
import utils.resources.ResourceHandler;
import view.controller.ViewController;
import view.controller.search.SearchView;

public class LWJGLViewController implements ViewController {

	public LWJGLViewController(ResourceHandler rh, ControllerMain controller) {
		resources = rh;
		graphics = new LWJGLGraphics(rh);
		graphics.prepareLayer(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				gfx.clearColourBuffer();
			}
			
		}, LWJGLGraphics.LAYER0);
		selections = new SelectionFrame(graphics);
		menus = new LWJGLMenuLayer(graphics, selections);
		mainMenu = new LWJGLMainMenu(this, resources);
		LWJGLCharacterSelection character = new LWJGLCharacterSelection(rh, graphics, menus);
		menus.add(character);
		splash = LWJGLPanel.fromPicture(menus, resources,
				ResourceHandler.joinPath("splash", "splash.jpg"), -1.78f, -1f,
				2.3f, true);
		menus.add(splash);
		LWJGLAlertDialog ald = new LWJGLAlertDialog(menus, resources, "this is a very very long message to fill the alert!", -.5f, 1f, -.5f, 0f, .5f);
		menus.add(ald);
		ald.show();
		board = null;
		this.controller = controller;
	}

	@Override
	public void focusOnBoard(TileName selectedTile) {
		board.focusOn(selectedTile);
	}

	@Override
	public void focusOnBoard(TileName selectedTile, Integer selectedClearing) {
		board.focusOn(selectedTile, selectedClearing);
	}

	@Override
	public void setPlayerActivities(CharacterType character,
			ArrayList<Activity> activities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPlayerActivities(CharacterType character,
			List<Activity> activities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterBirdSong(CharacterType type, int day, List<Phase> phases,
			PersonalHistory personalHistory,
			Map<TileName, List<Integer>> tileClrs) {

	}

	@Override
	public void displayMessage(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterMainMenu() {
		mainMenu.start(menus);
		graphics.start();
	}

	@Override
	public void enterLobby() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterSplashScreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startNetworkGame() {
		try {
			controller.connect(NetworkConfiguration.DEFAULT_IP, NetworkConfiguration.DEFAULT_PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		splash.setVisible(true);
		new Thread() {
			@Override
			public void run() {
				startBoard();
			}
		}.start();
		//controller.startGame();
	}

	@Override
	public void exit() {
		System.exit(0);
	}

	@Override
	public SearchView enterSearchView(CharacterType character) {
		// TODO Auto-generated method stub
		return null;
	}

	private void startBoard() {
		try {
			board = new LWJGLBoardDrawable(resources, graphics, selections);
			board.setDefaultClearingFocus();
			splash.setVisible(false);
			birdsong = new LWJGLBirdsong(resources, menus);
			controller.setBoardView(board);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ResourceHandler resources;
	private LWJGLBoardDrawable board;
	private SelectionFrame selections;
	private LWJGLGraphics graphics;
	private LWJGLMenuLayer menus;
	private LWJGLMainMenu mainMenu;
	private LWJGLBirdsong birdsong;
	private LWJGLPanel splash;

	private ClientController controller;

	private int frontBuffer, backBuffer;

}
