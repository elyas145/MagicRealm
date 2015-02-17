package controller;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;

import config.GameConfiguration;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import model.board.Board;
import model.board.Treasure;
import model.character.Character;
import model.character.CharacterFactory;
import model.interfaces.CharacterFactoryInterface;
import swingview.MainView;
import swingview.controller.mainmenu.MainMenu;
import utils.resources.ResourceHandler;
import view.controller.mainmenu.MainMenuView;
import view.controller.mainmenu.MenuItem;
import view.controller.mainmenu.MenuItemListener;
import view.controller.splashscreen.SplashScreen;
import view.controller.splashscreen.SplashScreenFinishedListener;

public class ControllerMain {

	public ControllerMain() {
		rh = new ResourceHandler();
		board = new Board(rh);
		mainView = new MainView(this);
	}

	private void start() {
		SplashScreen sc = mainView.enterSplashScreen();
		sc.whenFinished(new splashDone());
		sc.startScreen();
		// startBoardView();
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

	private class splashDone implements SplashScreenFinishedListener {

		@Override
		public void onSplashScreenFinish() {
			goToMainMenu();
		}
	}
	
	private class MainMenuListener implements MenuItemListener{

		@Override
		public void onItemSelect(MenuItem item) {
			System.out.println("clickes.");
			
		}
		
	}

	public static void main(String[] args) throws IOException {
		ControllerMain main = new ControllerMain();
		main.start();
	}
	
	public void goToMainMenu() {
		mainMenu = mainView.enterMainMenu();
		mainMenu.whenItemSelected(new MainMenuListener());
		mainMenu.startMenu();
		
	}

	private LWJGLGraphics gfx;
	private ResourceHandler rh;
	private Board board; // main game board
	private Character[] characters;
	private int numCharacters = 1;
	private MainView mainView;
	private MainMenuView mainMenu;
}
