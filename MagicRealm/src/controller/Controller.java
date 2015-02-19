package controller;

import java.util.ArrayList;

import model.activity.Activity;
import model.board.clearing.Clearing;
import model.enums.TileName;
import model.interfaces.ClearingInterface;
import model.player.PersonalHistory;

public interface Controller {

	void onSplashScreenEnd();

	void exit();

	void startGameView();

	PersonalHistory getPlayerHistory();

	int getCurrentDay();

	ArrayList<TileName> getPossibleTiles();

	ArrayList<Integer> getPossibleClearings(TileName selectedTile);
	
	void setCurrentPlayerActivities(ArrayList<Activity> activities);

	void startGame();
	
}
