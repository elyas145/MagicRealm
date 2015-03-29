package view.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.activity.Activity;
import model.character.Phase;
import model.enums.CharacterType;
import model.enums.TileName;
import model.player.PersonalHistory;
import utils.handler.Handler;
import view.controller.characterselection.CharacterSelectionListener;
import view.controller.mainmenu.MenuItemListener;
import view.controller.search.SearchView;

public interface ViewController {
	
	void focusOnBoard(TileName selectedTile);
	void focusOnBoard(TileName selectedTile, Integer selectedClearing);
	
	void setPlayerActivities(CharacterType character,
			ArrayList<Activity> activities);
	void setPlayerActivities(CharacterType character, List<Activity> activities);
	
	void enterBirdSong(CharacterType type, int day, List<Phase> phases,
			PersonalHistory personalHistory,
			Map<TileName, List<Integer>> tileClrs);
	
	void displayMessage(String string);
	
	void enterMainMenu(MenuItemListener mil);
	
	void enterLobby();
	void waitingForPlayers(int count);
	
	void enterCharacterSelection(List<CharacterType> characters, CharacterSelectionListener onselect);
	void enterSplashScreen();
	void exit();
	SearchView enterSearchView(CharacterType character);
	void startGame(BoardReadyListener brl);
}
