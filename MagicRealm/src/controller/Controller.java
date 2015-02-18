package controller;

import java.util.ArrayList;

import model.board.clearing.Clearing;
import model.board.tile.HexTile;
import model.player.PersonalHistory;

public interface Controller {

	void onSplashScreenEnd();

	void exit();

	void startGameView();

	PersonalHistory getPlayerHistory();

	int getCurrentDay();

	ArrayList<HexTile> getPossibleTiles();

	ArrayList<Clearing> getPossibleClearings();
	
}
