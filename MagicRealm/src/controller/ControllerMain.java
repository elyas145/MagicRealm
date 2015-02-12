package controller;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;

import config.GameConfiguration;
import lwjglview.graphics.LWJGLBoardDrawable;
import lwjglview.graphics.LWJGLGraphics;
import model.board.Board;
import model.board.Character;
import model.board.Treasure;
import utils.resources.ResourceHandler;

public class ControllerMain {
	private LWJGLGraphics gfx;
	private ResourceHandler rh;
	private Board board;	// main game board
	private ArrayList<Treasure> treasures;	// holds a bunch of treasures.
	private Character[] characters;
	private int numCharacters;
	
	public ControllerMain() {
		rh = new ResourceHandler();
		gfx = new LWJGLGraphics(rh);
		board = new Board();
		treasures = new ArrayList<Treasure>();
		randomTreasures();
		try {
			gfx.addDrawable(new LWJGLBoardDrawable(board, rh));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void randomTreasures() {
		ArrayList<Integer> possibleValues = new ArrayList<Integer>();
		
		for(int i = 1; i < 6; i++){
			possibleValues.add(i*10);
		}
		System.out.println(possibleValues.size());
		for (int i = 0; i < treasures.size(); i++){
			treasures.add(new Treasure(possibleValues));
		}
		
	}

	private void start() {
		gfx.start();

	}

	public static void main(String[] args) throws IOException {
		ControllerMain main = new ControllerMain();
		main.start();
	}

}
