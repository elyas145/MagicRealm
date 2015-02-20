package view.controller;

import model.enums.CharacterType;
import view.controller.search.SearchView;

public interface ViewController {
	
	void enterMainMenu();
	void enterSplashScreen();
	void startGameView();
	void exit();
	SearchView enterSearchView(CharacterType character);
}
