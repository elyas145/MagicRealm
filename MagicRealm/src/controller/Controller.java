package controller;

import java.util.ArrayList;

import model.board.clearing.Clearing;
import model.enums.TileName;
import model.player.PersonalHistory;

public interface Controller {

	void onSplashScreenEnd();

	void exit();

	void startGameView();

	PersonalHistory getPlayerHistory();

	int getCurrentDay();

	ArrayList<TileName> getPossibleTiles();

	ArrayList<Clearing> getPossibleClearings();
	
}
