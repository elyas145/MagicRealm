package controller;

import java.util.ArrayList;

import swingview.MainView;
import view.controller.game.BoardView;
import model.activity.Activity;
import model.activity.Move;
import model.board.clearing.Clearing;
import model.controller.ModelController;
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

	boolean checkMoveLegality(Move move);
	MainView getMainView();

	ModelController getModel();

	BoardView getBoardView();
	
}
