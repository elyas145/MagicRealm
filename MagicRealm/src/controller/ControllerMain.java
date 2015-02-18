package controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import model.activity.Activity;
import model.activity.Move;
import model.board.Board;
import model.board.clearing.Clearing;
import model.board.tile.HexTile;
import model.character.Character;
import model.character.CharacterFactory;
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
	private Board board; // main game board
	private ArrayList<Player> players;
	private int numCharacters = 1;
	private MainView mainView;
	private Player currentPlayer;
	private BoardView boardView;

	public ControllerMain() {
		rh = new ResourceHandler();
		board = new Board(rh);
		mainView = new MainView(this);
		players = new ArrayList<Player>();
		players.add(new Player(1, "Player One"));
		players.get(0).setCharacter(
				CharacterFactory.create(CharacterType.AMAZON));
		currentPlayer = players.get(0);
		board.setLocationOfCounter(CounterType.SITE_INN, TileName.BAD_VALLEY, 4);
		board.setLocationOfCounter(currentPlayer.getCharacter().getType().toCounter(),SiteType.INN);
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
			for (HexTileInterface hti : board.iterateTiles()) {
				boardView.setTile(hti.getName(), hti.getBoardColumn(),
						hti.getBoardRow(), hti.getRotation(),
						hti.getClearings());
			}
			for(CounterType ct : board.getCounters()){
				ClearingInterface ci = board.getLocationOfCounter(ct);
				boardView.setCounter(ct, ci.getParentTile().getName(), ci.getClearingNumber());
			}
			
			gfx.start();
			
			board.setLocationOfCounter(CounterType.CHARACTER_AMAZON, TileName.BAD_VALLEY, 1);
			boardView.setCounter(CounterType.CHARACTER_AMAZON, TileName.BAD_VALLEY, 1);
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
		return players;
	}

	public int getNumCharacters() {
		return numCharacters;
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
		mainView.enterBirdSong();
	}


	@Override
	public PersonalHistory getPlayerHistory() {
		return currentPlayer.getPersonalHistory();
	}

	@Override
	public int getCurrentDay() {
		return currentPlayer.getPersonalHistory().getCurrentDay();
	}

	@Override
	public ArrayList<Integer> getPossibleClearings(TileName tile) {
		ArrayList<ClearingInterface> clearings = new ArrayList<ClearingInterface>(board.getTile(tile).getClearings());
		ArrayList<Integer> ints = new ArrayList<Integer>();
		for(ClearingInterface ci : clearings){
			ints.add(ci.getClearingNumber());
		}
		return ints;
	}

	@Override
	public ArrayList<TileName> getPossibleTiles() {
		// TODO Auto-generated method stub
		return new ArrayList<TileName>(board.getAllTiles());
	}

	@Override
	public void setCurrentPlayerActivities(ArrayList<Activity> activities) {
		currentPlayer.setActivities(activities);
		playCurrentActivities();
	}

	private void playCurrentActivities() {
		ArrayList<Activity> activities = currentPlayer.getPersonalHistory().getCurrentActivities();
		for(Activity activity : activities){
			if(activity.getType() == ActivityType.MOVE){
				Move move = (Move)activity;
				boardView.setCounter(currentPlayer.getCharacter().getType().toCounter(), move.getTile(), move.getClearing());
			}
		}
	}
}
