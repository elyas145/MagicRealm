package lwjglview.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jogamp.audio.JogAmpSoundController;
import lwjglview.controller.birdsong.LWJGLBirdsong;
import lwjglview.controller.lobby.LWJGLLobbyView;
import lwjglview.controller.mainmenu.LWJGLMainMenu;
import lwjglview.controller.characterselection.LWJGLCharacterSelection;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.ClearingFocusHandler;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import lwjglview.menus.LWJGLAlertDialog;
import lwjglview.menus.LWJGLConfirmationDialog;
import lwjglview.menus.LWJGLMenuLayer;
import lwjglview.menus.LWJGLPanel;
import lwjglview.selection.SelectionFrame;
import model.activity.Activity;
import model.character.Phase;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.TileName;
import model.player.PersonalHistory;
import utils.handler.Handler;
import utils.resources.ResourceHandler;
import view.audio.SoundController;
import view.controller.BirdsongFinishedListener;
import view.controller.BoardReadyListener;
import view.controller.ClearingSelectedListener;
import view.controller.ViewController;
import view.controller.birdsong.ActivitiesListener;
import view.controller.characterselection.CharacterSelectionListener;
import view.controller.mainmenu.MenuItemListener;
import view.controller.search.SearchView;

public class LWJGLViewController implements ViewController {

	public LWJGLViewController(ResourceHandler rh) {
		init(rh, null);
	}

	public LWJGLViewController(ResourceHandler rh, SoundController sc) {
		init(rh, sc);
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
	public void enterBirdSong(int day, List<Phase> phases, final BirdsongFinishedListener onfinish) {
		lobbyView.setVisible(false);
		birdsong.setActivitiesListener(new ActivitiesListener() {

			@Override
			public void onActivitiesChosen(List<ActivityType> acts) {
				onfinish.onFinish(acts);
			}

		});
		birdsong.showPhases(phases);
	}
	
	@Override
	public boolean confirm(String message, String confirm, String deny) {
		return confirmation.ask(message, confirm, deny);
	}

	@Override
	public void enterMainMenu(MenuItemListener mil) {
		mainMenu.setVisible(true);
		mainMenu.setMenuItemListener(mil);
		if(sounds != null) {
			sounds.playMainTheme();
		}
	}

	@Override
	public void enterLobby() {
		mainMenu.setVisible(false);
		lobbyView.setVisible(true);
		if(sounds != null) {
			sounds.playLobbyTheme();
		}
	}

	@Override
	public void waitingForPlayers(int count) {
		lobbyView.waitingForPlayers(count);
	}

	@Override
	public void enterCharacterSelection(List<CharacterType> characters,
			CharacterSelectionListener onselect) {
		lobbyView.setVisible(false);
		characterSelection.selectCharacter(characters, onselect);
		displayMessage("Please select your character");
	}

	@Override
	public void enterSplashScreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startGame(final BoardReadyListener brl) {
		splash.setVisible(true);
		new Thread() {
			@Override
			public void run() {
				startBoard(brl);
			}
		}.start();
		// controller.startGame();
	}

	@Override
	public SearchView enterSearchView(CharacterType character) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void selectClearing(final ClearingSelectedListener csl) {
		board.setCleaingFocus(new ClearingFocusHandler() {

			@Override
			public void onFocus(TileName tile, int clearing) {
				board.focusOn(tile, clearing);
				if(clearing > 0) {
					csl.onClearingSelection(tile, clearing);
				}
				board.setDefaultClearingFocus();
			}
			
		});
	}

	@Override
	public void displayMessage(String string) {
		displayMessage(string, null);
	}
	
	@Override
	public void displayMessage(String string, Runnable onClose) {
		if(sounds != null) {
			sounds.alert();
		}
		alert.setMessage(string);
		alert.setHandler(onClose);
		alert.show();
	}
	
	private void init(ResourceHandler rh, SoundController sc) {
		resources = rh;
		sounds = sc;
		graphics = new LWJGLGraphics(rh);
		graphics.prepareLayer(new Handler<LWJGLGraphics>() {

			@Override
			public void handle(LWJGLGraphics gfx) {
				gfx.clearColourBuffer();
			}

		}, LWJGLGraphics.LAYER0);
		selections = new SelectionFrame(graphics);
		menus = new LWJGLMenuLayer(graphics, selections);
		mainMenu = new LWJGLMainMenu(menus, resources);
		characterSelection = new LWJGLCharacterSelection(rh, graphics, menus);
		menus.add(characterSelection);
		splash = LWJGLPanel.fromPicture(menus, resources,
				ResourceHandler.joinPath("splash", "splash.jpg"), -1.78f, -1f,
				2.3f, true);
		menus.add(splash);
		alert = new LWJGLAlertDialog(menus, resources,
				"this is a very very long message to fill the alert!", -.73f,
				1f, -.73f, -.4f, .8f);
		confirmation = new LWJGLConfirmationDialog(menus, resources,
				"Will you join the dark side?", "Yes master", "Never!", -.73f,
				1f, -.73f, -.4f, .8f);
		birdsong = new LWJGLBirdsong(resources, menus);
		board = null;
		messageOverlay = new LWJGLWaitingView(menus, "Waiting");
		lobbyView = new LWJGLLobbyView(messageOverlay);
		graphics.start();
	}

	private void startBoard(BoardReadyListener brl) {
		try {
			board = new LWJGLBoardDrawable(resources, graphics, selections);
			board.setDefaultClearingFocus();
			splash.setVisible(false);
			brl.boardReady(board);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ResourceHandler resources;
	private SoundController sounds;
	private LWJGLBoardDrawable board;
	private SelectionFrame selections;
	private LWJGLGraphics graphics;
	private LWJGLMenuLayer menus;
	private LWJGLMainMenu mainMenu;
	private LWJGLCharacterSelection characterSelection;
	private LWJGLBirdsong birdsong;
	private LWJGLPanel splash;
	private LWJGLAlertDialog alert;
	private LWJGLConfirmationDialog confirmation;
	private LWJGLWaitingView messageOverlay;
	private LWJGLLobbyView lobbyView;

}
