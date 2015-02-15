package controller;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;

import config.GameConfiguration;
import controlview.StartView;
import lwjglview.graphics.LWJGLBoardDrawable;
import lwjglview.graphics.LWJGLGraphics;
import model.board.Board;
import model.board.Character;
import model.board.CharacterFactory;
import model.board.Treasure;
import model.interfaces.CharacterFactoryInterface;
import utils.resources.ResourceHandler;

public class ControllerMain {
	private LWJGLGraphics gfx;
	private ResourceHandler rh;

	public ResourceHandler getRh() {
		return rh;
	}

	public Character[] getCharacters() {
		return characters;
	}

	public int getNumCharacters() {
		return numCharacters;
	}

	private Board board; // main game board
	private Character[] characters;
	private int numCharacters;
	private StartView startView;

	public ControllerMain() {
		rh = new ResourceHandler();
		board = new Board();
		startView = new StartView(this);
	}

	private void start() {
		startView.start();
	}

	public void StartBoardView() {
		gfx = new LWJGLGraphics(rh);
		try {
			gfx.addDrawable(new LWJGLBoardDrawable(board, rh));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<Character> getAllCharacters(){
		CharacterFactory cf = new CharacterFactory();		
		return cf.getPossibleCharacters();
		
	}
	public static void main(String[] args) throws IOException {
		ControllerMain main = new ControllerMain();
		main.start();
	}

}
