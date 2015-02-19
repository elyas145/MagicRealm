package controller;

import java.io.IOException;
import java.util.ArrayList;

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
import model.enums.SiteType;
import model.enums.TileName;
import model.interfaces.ClearingInterface;
import model.interfaces.HexTileInterface;
import model.player.PersonalHistory;
import model.player.Player;
import swingview.MainView;
import utils.resources.ResourceHandler;
import view.controller.game.BoardView;

public class ControllerMain implements Controller {

	private LWJGLGraphics gfx;
	private ResourceHandler rh;
	private MainView mainView = null;
	private BoardView boardView;
	private ModelController model;
	private Controller thisController = this;
	private Thread gameLoop;

	public ControllerMain() {
		rh = new ResourceHandler();
		model = new ModelController(rh);
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

	public ArrayList<Player> getPlayers() {
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
	public void setCurrentPlayerActivities(ArrayList<Activity> activities) {
		model.setCurrentPlayerActivities(activities);
		model.setPlayerDone();
	}

	private void playCurrentActivities() {
		ArrayList<Activity> activities = model.getCurrentActivities();
		for (Activity activity : activities) {
			if (activity.getType() == ActivityType.MOVE) {
				Move move = (Move) activity;
				if (checkMoveLegality(move)) {
					boardView.setCounter(model.getCurrentCounter(),
							move.getTile(), move.getClearing());
				} else {
					JOptionPane.showMessageDialog(null,
							"Illegal move cancelled.");
				}
			}
		}
	}

	private boolean checkMoveLegality(Move move) {
		// return true if current character clearing is connected to move
		// clearing
		ArrayList<ClearingInterface> clearings = model.getBoard()
				.getConntectedClearings(
						model.getBoard().getLocationOfCounter(
								model.getCurrentCounter()));
		ClearingInterface c = model.getBoard().getClearing(move.getTile(),
				move.getClearing());
		for (ClearingInterface clearing : clearings) {
			if (clearing.getClearingNumber() == move.getClearing()
					&& clearing.getParentTile().getName()
							.equals(move.getTile())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void startGame() {
		this.startBoardView();
		while (model.getCurrentDay() <= GameConfiguration.LUNAR_MONTH) {
			model.newDayTime();
			for (int i = 0; i < model.getNumPlayers(); i++) {
				synchronized (model) {
					startBirdSong(model.getCurrentPlayer());
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
			for (int i = 0; i < model.getNumPlayers(); i++) {
				synchronized (model) {
					playCurrentActivities();
					while (!model.isPlayerDone()) {
						try {
							model.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private void startBirdSong(Player player) {
		mainView.enterBirdSong(player.getCharacter().getType().toString());
	}
}
