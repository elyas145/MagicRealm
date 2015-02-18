package controller;

import java.io.IOException;
import java.util.ArrayList;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import model.board.Board;
import model.character.Character;
import model.character.CharacterFactory;
import swingview.MainView;
import utils.resources.ResourceHandler;

public class ControllerMain implements Controller {

	private LWJGLGraphics gfx;
	private ResourceHandler rh;
	private Board board; // main game board
	private Character[] characters;
	private int numCharacters = 1;
	private MainView mainView;

	public ControllerMain() {
		rh = new ResourceHandler();
		board = new Board(rh);
		mainView = new MainView(this);
	}

	private void start() {
		mainView.enterSplashScreen();
	}

	public void startBoardView() {
		gfx = new LWJGLGraphics(rh);
		try {
			gfx.addDrawable(new LWJGLBoardDrawable(board, rh));
			gfx.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Character> getAllCharacters() {
		CharacterFactory cf = new CharacterFactory();
		return cf.getPossibleCharacters();
	}

	public ResourceHandler getRh() {
		return rh;
	}

	public Character[] getCharacters() {
		return characters;
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
		mainView.startGameView();
		
	}
}
