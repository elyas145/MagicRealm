package controller;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;

import config.GameConfiguration;
import controlview.StartView;
import lwjglview.graphics.LWJGLGraphics;
import lwjglview.graphics.board.LWJGLBoardDrawable;
import model.board.Board;
import model.board.Treasure;
import model.character.Character;
import model.character.CharacterFactory;
import model.interfaces.CharacterFactoryInterface;
import utils.resources.ResourceHandler;

public class ControllerMain {
	private LWJGLGraphics gfx;
	private ResourceHandler rh;
	private Board board; // main game board
	private Character[] characters;
	private int numCharacters = 1;
	private StartView startView;

	public ControllerMain() {
		rh = new ResourceHandler();
		board = new Board(rh);
		startView = new StartView(this);
	}

	private void start() {
		// startView.start();
		startBoardView();
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

	public static void main(String[] args) throws IOException {
		ControllerMain main = new ControllerMain();
		main.start();
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
}
