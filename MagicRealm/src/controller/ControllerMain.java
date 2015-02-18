package controller;

import java.io.IOException;
import java.util.ArrayList;

import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import model.board.Board;
import model.character.Character;
import model.character.CharacterFactory;
import model.enums.CharacterType;
import model.player.PersonalHistory;
import model.player.Player;
import swingview.MainView;
import utils.resources.ResourceHandler;

public class ControllerMain implements Controller {

	private LWJGLGraphics gfx;
	private ResourceHandler rh;
	private Board board; // main game board
	private ArrayList<Player> players;
	private int numCharacters = 1;
	private MainView mainView;
	private Player currentPlayer;

	public ControllerMain() {
		rh = new ResourceHandler();
		board = new Board(rh);
		mainView = new MainView(this);
		players = new ArrayList<Player>();
		players.add(new Player(1, "Player One"));
		players.get(0).setCharacter(CharacterFactory.create(CharacterType.AMAZON));
	}

	private void start() {
		mainView.enterSplashScreen();
	}

	public void startBoardView() {
		gfx = new LWJGLGraphics(rh);
		try {
			gfx.addDrawable(new LWJGLBoardDrawable(board, rh));
			(new Thread(new BoardRunnable(gfx))).start();
			
		} catch (IOException e) {
			e.printStackTrace();
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
		System.exit(0);		
	}

	@Override
	public void startGameView() {
		startBoardView();
		//mainView.startGameView();
		
	}
	
	private class BoardRunnable implements Runnable{
		private LWJGLGraphics gfx;
		public BoardRunnable(LWJGLGraphics gfx){
			this.gfx = gfx;
		}
		@Override
		public void run() {
			gfx.start();			
		}		
	}

	@Override
	public PersonalHistory getPlayerHistory() {
		return currentPlayer.getPersonalHistory();
	}
}
