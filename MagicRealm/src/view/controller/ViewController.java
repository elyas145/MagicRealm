package view.controller;

import java.util.List;

import model.character.Phase;
import model.counter.chit.MapChit;
import model.enums.CharacterType;
import model.enums.SearchType;
import model.enums.TileName;
import model.enums.TimeOfDay;
import view.controller.characterselection.CharacterSelectionListener;
import view.controller.cheatmode.DieSelectionListener;
import view.controller.mainmenu.MenuItemListener;
import view.controller.search.SearchTypeListener;
import view.controller.search.TableSelectionListener;

public interface ViewController {
	
	void focusOnBoard(TileName selectedTile);
	void focusOnBoard(TileName selectedTile, Integer selectedClearing);
	
	void setTimeOfDay(TimeOfDay tod);
	
	void enterBirdSong(int day, List<Phase> phases, BirdsongFinishedListener onfinish);
	
	void selectClearing(ClearingSelectedListener csl);
	
	void displayMessage(String string);
	void displayMessage(String string, Runnable onClose);
	
	void displayBanner(String message);
	void hideBanner();
	
	boolean confirm(String message, String confirm, String deny);
	
	void enterMainMenu(MenuItemListener mil);
	
	void enterLobby();
	void waitingForPlayers(int count);
	
	void selectSearchTable(TableSelectionListener tsl);
	
	void selectSearchType(List<SearchType> avail, SearchTypeListener stl);
	
	void selectDie(DieSelectionListener dsl);
	
	void enterCharacterSelection(List<CharacterType> characters, CharacterSelectionListener onselect);
	void disableCharacter(CharacterType character);
	
	void enterSplashScreen();

	void startGame(BoardReadyListener brl);
	
	void updateLog(List<String> updates);
	void updateBelongings(List<String> updates);
	
	void revealAllMapChits(Iterable<MapChit> chits);
	void hideMainMenu();
}
