package swingview;

import javax.swing.JFrame;

import swingview.controller.mainmenu.MainMenu;
import swingview.controller.start.StartPanel;
import view.controller.ViewController;
import view.controller.mainmenu.MainMenuView;
import view.controller.splashscreen.SplashScreen;
import view.controller.splashscreen.SplashScreenFinishedListener;
import controller.ControllerMain;

public class MainView extends JFrame implements ViewController {
	ControllerMain parent;

	public MainView(ControllerMain parent) {
		super("Magic Realm");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	@Override
	public MainMenuView enterMainMenu() {
		MainMenu mainMenu = new MainMenu();
		setContentPane(mainMenu);
		pack();
		return mainMenu;
	}

	public SplashScreen enterSplashScreen() {
		StartPanel start = new StartPanel();
		setContentPane(start);
		pack();
		return start;
	}

}
