package view.controller.mainmenu;

import view.controller.game.GameView;

public interface MainMenuView {
	
	GameView enterGameView();
	void whenItemSelected(MenuItemListener listener);
	void startMenu();
}
