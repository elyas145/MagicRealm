package swingview;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.character.Phase;
import model.counter.chit.MapChit;
import model.enums.CharacterType;
import model.enums.ChitType;
import model.player.Player;
import swingview.controller.birdsong.BirdSongView;
import swingview.controller.mainmenu.MainMenu;
import swingview.controller.search.SearchView;
import utils.time.Timing;
import view.controller.ViewController;
import view.controller.splashscreen.SplashScreen;
import config.GraphicsConfiguration;
import controller.Controller;

@SuppressWarnings("serial")
public class MainView extends JFrame implements ViewController, WindowListener,
		Runnable {
	private Controller parent;

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static int xSize = ((int) tk.getScreenSize().getWidth());
	private static int ySize = ((int) tk.getScreenSize().getHeight());

	public MainView(Controller parent) {
		super("Magic Realm");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(
				GraphicsConfiguration.INITIAL_MAIN_WIDTH,
				GraphicsConfiguration.INITIAL_MAIN_HEIGHT));
		setLocationRelativeTo(null);
		setLocation(xSize / 2 + GraphicsConfiguration.INITIAL_WINDOW_WIDTH / 2,
				ySize / 2 - GraphicsConfiguration.INITIAL_MAIN_HEIGHT / 2);
		this.addWindowListener(this);
		this.parent = parent;
		pack();
	}

	@Override
	public void run() {
	}

	@Override
	public void enterMainMenu() {
		System.out.println("main");
		MainMenu mainMenu = new MainMenu(this);
		setContentPane(mainMenu);
		pack();
		setVisible(true);
	}

	public void enterBirdSong(String player, ArrayList<Phase> phases) {
		setPreferredSize(new Dimension(
				GraphicsConfiguration.INITIAL_BIRD_WIDTH,
				GraphicsConfiguration.INITIAL_BIRD_HEIGHT));
		BirdSongView birdSong = new BirdSongView(parent, player, phases);
		setContentPane(birdSong);
		pack();
	}

	public void enterSplashScreen() {

		final java.awt.SplashScreen splash = java.awt.SplashScreen
				.getSplashScreen();
		if (splash == null) {
			System.out.println("SplashScreen.getSplashScreen() returned null");
			return;
		}

		Graphics2D g = splash.createGraphics();
		if (g == null) {
			System.out.println("g is null");
			return;
		}
		Rectangle2D r = new Rectangle2D.Double(0, 0, xSize, ySize);
		float endTime = Timing.getSeconds() + GraphicsConfiguration.SPLASH_TIME;
		for (int i = 0; Timing.getSeconds() < endTime; i++) {
			g.setPaint(Color.BLACK);
			g.fill(r);
			renderSplashFrame(g, i);
			splash.update();
			try {
				Thread.sleep(90);
			} catch (InterruptedException e) {
			}
		}
		splash.close();
		parent.onSplashScreenEnd();
	}

	static void renderSplashFrame(Graphics2D g, int frame) {
		final String[] comps = { "objects", "map", "tiles", "characters",
				"environment", "view", "handlers" };
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, xSize, ySize);
		g.setPaintMode();
		g.setColor(Color.WHITE);
		g.setFont(new Font("Algerian", Font.PLAIN, 32));
		g.drawString("Loading " + comps[(frame / 5) % 7] + "...",
				(xSize / 2) - 300, ySize / 2);
	}

	public void displayMessage(String string) {
		JOptionPane.showMessageDialog(this, string);

	}
	
	@Override
	public view.controller.search.SearchView enterSearchView(CharacterType character) {
		SearchView search = new SearchView(this, character);
		setContentPane(search);
		pack();
		return search;
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		exit();
	}

	@Override
	public void startGameView() {
		parent.startGame();
	}

	@Override
	public void exit() {
		parent.exit();
	}

	// don't worry about these.
	@Override
	public void windowClosed(WindowEvent ae) {
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}


}
