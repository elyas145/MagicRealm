package view.controller;

import view.controller.mainmenu.MainMenuView;
import view.controller.splashscreen.SplashScreen;

public interface ViewController {
	
	MainMenuView enterMainMenu();
	SplashScreen enterSplashScreen();
}
