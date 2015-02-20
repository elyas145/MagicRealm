package controller;

import java.util.ArrayList;

import swingview.MainView;
import view.controller.game.BoardView;
import model.activity.Activity;
import model.activity.Move;
import model.board.clearing.Clearing;
import model.controller.ModelController;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.TileName;
import model.interfaces.ClearingInterface;
import model.player.PersonalHistory;
import model.player.Player;

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

	MainView getMainView();

	ModelController getModel();

	BoardView getBoardView();

	CharacterType getCurrentCharacter();

	void displayMessage(String string);

	void setHiding(CharacterType character);

	void setCurrentCharacter(CharacterType character);

	void moveCounter(CounterType counter, TileName tt, int clearing);
	
}
