package lwjglview.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import controller.ClientController;
import lwjglview.controller.birdsong.LWJGLBirdsong;
import lwjglview.controller.mainmenu.LWJGLMainMenu;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.menus.LWJGLMenuLayer;
import lwjglview.menus.LWJGLPanel;
import lwjglview.selection.SelectionFrame;
import model.activity.Activity;
import model.board.Board;
import model.character.Phase;
import model.enums.CharacterType;
import model.enums.TileName;
import model.interfaces.HexTileInterface;
import model.player.PersonalHistory;
import swingview.ControllerMain;
import utils.resources.ResourceHandler;
import view.controller.ViewController;
import view.controller.search.SearchView;

public class LWJGLViewController implements ViewController {

	public static void main(String[] args) {
		new LWJGLViewController(new ResourceHandler());
	}

	public LWJGLViewController(ResourceHandler rh) {
		resources = rh;
		graphics = new LWJGLGraphics(rh);
		selections = new SelectionFrame(graphics);
		menus = new LWJGLMenuLayer(graphics, selections);
		mainMenu = new LWJGLMainMenu(this, resources);
		splash = LWJGLPanel.fromPicture(menus, resources,
				ResourceHandler.joinPath("splash", "splash.jpg"), -1.78f, -1f,
				2.3f, LWJGLPanel.Type.FOREGROUND, true);
		board = null;
		controller = new ControllerMain(this);
		enterMainMenu();
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
		splash.setVisible(true);
		new Thread() {
			@Override
			public void run() {
				startBoard();
			}
		}.start();
		controller.startGame();
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
			Board tmp = new Board(resources);
			for (TileName tn : tmp.getAllTiles()) {
				HexTileInterface hti = tmp.getTile(tn);
				board.setTile(tn, hti.getBoardRow(), hti.getBoardColumn(),
						hti.getRotation(), hti.getClearings());
			}
			splash.setVisible(false);
			birdsong = new LWJGLBirdsong(resources, menus);
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
