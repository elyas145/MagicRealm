package controller;

import model.player.PersonalHistory;

public interface Controller {

	void onSplashScreenEnd();

	void exit();

	void startGameView();

	PersonalHistory getPlayerHistory();

	int getCurrentDay();
	
}
