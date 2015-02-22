package controller;

import java.util.ArrayList;
import java.util.List;

import swingview.MainView;
import view.controller.game.BoardView;
import model.activity.Activity;
import model.activity.Move;
import model.board.clearing.Clearing;
import model.controller.ModelController;
import model.counter.chit.MapChit;
import model.enums.CharacterType;
import model.enums.CounterType;
import model.enums.PeerType;
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

	List<TileName> getPossibleTiles();

	List<Integer> getPossibleClearings(TileName selectedTile);
	
	void setCurrentPlayerActivities(List<Activity> activities);

	void startGame();

	MainView getMainView();

	ModelController getModel();

	BoardView getBoardView();

	CharacterType getCurrentCharacter();

	void displayMessage(String string);

	void setHiding(CharacterType character);

	void setCurrentCharacter(CharacterType character);

	void moveCounter(CounterType counter, TileName tt, int clearing);

	void startSearch(CharacterType actor);
	
	void focusOnBoard(TileName tile);
	
	void focusOnBoard(TileName tile, int clearing);
	
	void focusOnBoard(CounterType counter);

	PeerType getPeerChoice();

	void displayMessage(ArrayList<MapChit> peek);
	
}
