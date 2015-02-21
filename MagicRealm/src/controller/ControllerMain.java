package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import config.GameConfiguration;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import model.activity.Activity;
import model.activity.Move;
import model.board.Board;
import model.board.clearing.Clearing;
import model.board.tile.HexTile;
import model.character.Character;
import model.character.CharacterFactory;
import model.controller.ModelController;
import model.enums.ActivityType;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.TileName;
import model.enums.TimeOfDay;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;
import model.player.PersonalHistory;
import model.player.Player;
import swingview.MainView;
import utils.resources.ResourceHandler;
import view.controller.game.BoardView;
import view.controller.search.SearchView;

public class ControllerMain implements Controller {

	private LWJGLGraphics gfx;
	private ResourceHandler rh;
	private MainView mainView = null;
	private BoardView boardView;
	private ModelController model;
	private Controller thisController = this;

	public ControllerMain() {
		rh = new ResourceHandler();
		model = new ModelController(rh, this);
		mainView = new MainView(thisController);
		new Thread(mainView).start();
		model.setBoard();
		model.setNumberPlayers(GameConfiguration.MAX_PLAYERS);
		model.setCharacters();
		model.setPlayers();
		model.setSiteLocations();
		model.setPlayersInitialLocations();
	}

	private void start() {
		mainView.enterSplashScreen();
	}

	public void startBoardView() {
		gfx = new LWJGLGraphics(rh, this);
		LWJGLBoardDrawable boardDrawable;
		try {
			boardDrawable = new LWJGLBoardDrawable(rh);
			boardView = boardDrawable;
			gfx.addDrawable(boardDrawable);
			for (HexTileInterface hti : model.getBoard().iterateTiles()) {
				boardView.setTile(hti.getName(), hti.getBoardColumn(),
						hti.getBoardRow(), hti.getRotation(),
						hti.getClearings());
			}
			for (CounterType ct : model.getBoard().getCounters()) {
				ClearingInterface ci = model.getBoard()
						.getLocationOfCounter(ct);
				boardView.setCounter(ct, ci.getParentTile().getName(),
						ci.getClearingNumber());
			}

			gfx.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public ArrayList<Character> getAllCharacters() {
		return CharacterFactory.getPossibleCharacters();
	}

	public ResourceHandler getRh() {
		return rh;
	}

	public Iterable<Player> getPlayers() {
		return model.getPlayers();
	}

	public int getNumCharacters() {
		return model.getNumPlayers();
	}

	public static void main(String[] args) throws IOException {
		ControllerMain main = new ControllerMain();
		main.start();
	}

	public void goToMainMenu() {
		mainView.enterMainMenu();
	}

	@Override
	public void onSplashScreenEnd() {
		goToMainMenu();
	}

	@Override
	public void exit() {
		String ObjButtons[] = { "Yes", "No" };
		int PromptResult = JOptionPane.showOptionDialog(null,
				"Are you sure you want to exit?", "Magic Realm",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
				ObjButtons, ObjButtons[1]);
		if (PromptResult == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	@Override
	public void startGameView() {
		startBoardView();
	}

	@Override
	public PersonalHistory getPlayerHistory() {
		return model.getCurrentPlayer().getPersonalHistory();
	}

	@Override
	public int getCurrentDay() {
		return model.getCurrentDay();
	}

	@Override
	public ArrayList<Integer> getPossibleClearings(TileName tile) {
		ArrayList<ClearingInterface> clearings = new ArrayList<ClearingInterface>(
				model.getBoard().getTile(tile).getClearings());
		ArrayList<Integer> ints = new ArrayList<Integer>();
		for (ClearingInterface ci : clearings) {
			ints.add(ci.getClearingNumber());
		}
		return ints;
	}

	@Override
	public ArrayList<TileName> getPossibleTiles() {
		// TODO Auto-generated method stub
		return new ArrayList<TileName>(model.getBoard().getAllTiles());
	}

	@Override
	public void setCurrentPlayerActivities(List<Activity> activities) {
		model.setCurrentPlayerActivities(activities);
		model.setPlayerDone();
	}

	private void playCurrentActivities() {
		List<Activity> activities = model.getCurrentActivities();
		// unhide player.
		model.unhideCurrent();
		for (Activity activity : activities) {
			activity.perform(model);
		}
		model.setPlayerDone();
	}

	@Override
	public void startGame() {
		this.startBoardView();
		TimeOfDay tod = TimeOfDay.MIDNIGHT;
		for (Player plr : model.getPlayers()) {
			boardView.hideCounter(plr.getCharacter().getType().toCounter());
		}
		while (model.getCurrentDay() <= GameConfiguration.LUNAR_MONTH) {
			model.newDay();
			model.newDayTime();
			tod = tod.next();
			boardView.setTimeOfDay(tod);
			Player plr;
			// birdsong
			for (int i = 0; i < model.getNumPlayers(); i++) {
				synchronized (model) {
					plr = model.getCurrentPlayer();
					boardView.focusOn(plr.getCharacter().getType().toCounter());
					startBirdSong(plr);
					while (!model.isPlayerDone()) {
						try {
							model.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
						}
					}
					model.nextPlayer();
				}
			}
			model.newDayTime();
			tod = tod.next();
			boardView.setTimeOfDay(tod);
			// dayLight
			for (int i = 0; i < model.getNumPlayers(); i++) {
				synchronized (model) {
					plr = model.getCurrentPlayer();
					boardView.focusOn(getCurrentCharacter().toCounter());
					if (plr.getCharacter().isHiding()) {
						model.unhideCurrent();
						boardView.unHideCounter(model.getCurrentCharacterType()
								.toCounter());
					}
					playCurrentActivities();
					while (!model.isPlayerDone()) {
						try {
							model.wait();
						} catch (InterruptedException e) {
							// e.printStackTrace();
						}
					}
					while (!boardView.isAnimationFinished(getCurrentCharacter()
							.toCounter()))
						;
					model.nextPlayer();
				}
			}
		}
	}

	private void startBirdSong(Player player) {
		mainView.enterBirdSong(player.getCharacter().getType().toString(),
				model.getAllowedPhases());
	}

	@Override
	public MainView getMainView() {

		return mainView;
	}

	@Override
	public ModelController getModel() {
		return model;
	}

	@Override
	public BoardView getBoardView() {

		return boardView;
	}

	@Override
	public CharacterType getCurrentCharacter() {
		return model.getCurrentCharacterType();
	}

	@Override
	public void displayMessage(String string) {
		getMainView().displayMessage("Illegal move cancelled.");
	}

	@Override
	public void setHiding(CharacterType character) {
		boardView.hideCounter(character.toCounter());
	}

	@Override
	public void setCurrentCharacter(CharacterType character) {
		boardView.focusOn(character.toCounter());
	}

	@Override
	public void moveCounter(CounterType counter, TileName tt, int clearing) {
		boardView.setCounter(counter, tt, clearing);
	}

	@Override
	public void startSearch(CharacterType searcher) {
		SearchView sv = mainView.enterSearchView(searcher);
		synchronized (sv) {
			while (!sv.doneSearching()) {
				try {
					sv.wait();
				} catch (InterruptedException e) {
				}
			}
			model.performSearch(sv.getSelectedTable());
		}
	}

}
